package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.reflect.ClassReflection;

public class Actor {
    private final Array<Action> actions = new Array<>(0);
    private final DelayedRemovalArray<EventListener> captureListeners = new DelayedRemovalArray<>(0);
    final Color color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    private boolean debug;
    float height;
    private final DelayedRemovalArray<EventListener> listeners = new DelayedRemovalArray<>(0);
    private String name;
    float originX;
    float originY;
    Group parent;
    float rotation;
    float scaleX = 1.0f;
    float scaleY = 1.0f;
    private Stage stage;
    private Touchable touchable = Touchable.enabled;
    private Object userObject;
    private boolean visible = true;
    float width;
    float x;
    float y;

    public void draw(Batch batch, float parentAlpha) {
    }

    public void act(float delta) {
        Array<Action> actions2 = this.actions;
        if (actions2.size != 0) {
            Stage stage2 = this.stage;
            if (stage2 != null && stage2.getActionsRequestRendering()) {
                Gdx.graphics.requestRendering();
            }
            int i = 0;
            while (i < actions2.size) {
                try {
                    Action action = actions2.get(i);
                    if (action.act(delta) && i < actions2.size) {
                        int actionIndex = actions2.get(i) == action ? i : actions2.indexOf(action, true);
                        if (actionIndex != -1) {
                            actions2.removeIndex(actionIndex);
                            action.setActor((Actor) null);
                            i--;
                        }
                    }
                    i++;
                } catch (RuntimeException ex) {
                    String context = toString();
                    throw new RuntimeException("Actor: " + context.substring(0, Math.min(context.length(), 128)), ex);
                }
            }
        }
    }

    public boolean fire(Event event) {
        if (event.getStage() == null) {
            event.setStage(getStage());
        }
        event.setTarget(this);
        Array<Group> ancestors = (Array) Pools.obtain(Array.class);
        for (Group parent2 = this.parent; parent2 != null; parent2 = parent2.parent) {
            ancestors.add(parent2);
        }
        try {
            Object[] ancestorsArray = ancestors.items;
            for (int i = ancestors.size - 1; i >= 0; i--) {
                ((Group) ancestorsArray[i]).notify(event, true);
                if (event.isStopped()) {
                    return event.isCancelled();
                }
            }
            notify(event, true);
            if (event.isStopped()) {
                boolean isCancelled = event.isCancelled();
                ancestors.clear();
                Pools.free(ancestors);
                return isCancelled;
            }
            notify(event, false);
            if (!event.getBubbles()) {
                boolean isCancelled2 = event.isCancelled();
                ancestors.clear();
                Pools.free(ancestors);
                return isCancelled2;
            } else if (event.isStopped()) {
                boolean isCancelled3 = event.isCancelled();
                ancestors.clear();
                Pools.free(ancestors);
                return isCancelled3;
            } else {
                int n = ancestors.size;
                for (int i2 = 0; i2 < n; i2++) {
                    ((Group) ancestorsArray[i2]).notify(event, false);
                    if (event.isStopped()) {
                        boolean isCancelled4 = event.isCancelled();
                        ancestors.clear();
                        Pools.free(ancestors);
                        return isCancelled4;
                    }
                }
                boolean isCancelled5 = event.isCancelled();
                ancestors.clear();
                Pools.free(ancestors);
                return isCancelled5;
            }
        } finally {
            ancestors.clear();
            Pools.free(ancestors);
        }
    }

    public boolean notify(Event event, boolean capture) {
        if (event.getTarget() != null) {
            DelayedRemovalArray<EventListener> listeners2 = capture ? this.captureListeners : this.listeners;
            if (listeners2.size == 0) {
                return event.isCancelled();
            }
            event.setListenerActor(this);
            event.setCapture(capture);
            if (event.getStage() == null) {
                event.setStage(this.stage);
            }
            try {
                listeners2.begin();
                int n = listeners2.size;
                for (int i = 0; i < n; i++) {
                    EventListener listener = listeners2.get(i);
                    if (listener.handle(event)) {
                        event.handle();
                        if (event instanceof InputEvent) {
                            InputEvent inputEvent = (InputEvent) event;
                            if (inputEvent.getType() == InputEvent.Type.touchDown) {
                                event.getStage().addTouchFocus(listener, this, inputEvent.getTarget(), inputEvent.getPointer(), inputEvent.getButton());
                            }
                        }
                    }
                }
                listeners2.end();
                return event.isCancelled();
            } catch (RuntimeException ex) {
                String context = toString();
                throw new RuntimeException("Actor: " + context.substring(0, Math.min(context.length(), 128)), ex);
            }
        } else {
            throw new IllegalArgumentException("The event target cannot be null.");
        }
    }

    public Actor hit(float x2, float y2, boolean touchable2) {
        if ((!touchable2 || this.touchable == Touchable.enabled) && isVisible() && x2 >= 0.0f && x2 < this.width && y2 >= 0.0f && y2 < this.height) {
            return this;
        }
        return null;
    }

    public boolean remove() {
        Group group = this.parent;
        if (group != null) {
            return group.removeActor(this, true);
        }
        return false;
    }

    public boolean addListener(EventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null.");
        } else if (this.listeners.contains(listener, true)) {
            return false;
        } else {
            this.listeners.add(listener);
            return true;
        }
    }

    public boolean removeListener(EventListener listener) {
        if (listener != null) {
            return this.listeners.removeValue(listener, true);
        }
        throw new IllegalArgumentException("listener cannot be null.");
    }

    public DelayedRemovalArray<EventListener> getListeners() {
        return this.listeners;
    }

    public boolean addCaptureListener(EventListener listener) {
        if (listener != null) {
            if (!this.captureListeners.contains(listener, true)) {
                this.captureListeners.add(listener);
            }
            return true;
        }
        throw new IllegalArgumentException("listener cannot be null.");
    }

    public boolean removeCaptureListener(EventListener listener) {
        if (listener != null) {
            return this.captureListeners.removeValue(listener, true);
        }
        throw new IllegalArgumentException("listener cannot be null.");
    }

    public DelayedRemovalArray<EventListener> getCaptureListeners() {
        return this.captureListeners;
    }

    public void addAction(Action action) {
        action.setActor(this);
        this.actions.add(action);
        Stage stage2 = this.stage;
        if (stage2 != null && stage2.getActionsRequestRendering()) {
            Gdx.graphics.requestRendering();
        }
    }

    public void removeAction(Action action) {
        if (this.actions.removeValue(action, true)) {
            action.setActor((Actor) null);
        }
    }

    public Array<Action> getActions() {
        return this.actions;
    }

    public boolean hasActions() {
        return this.actions.size > 0;
    }

    public void clearActions() {
        for (int i = this.actions.size - 1; i >= 0; i--) {
            this.actions.get(i).setActor((Actor) null);
        }
        this.actions.clear();
    }

    public void clearListeners() {
        this.listeners.clear();
        this.captureListeners.clear();
    }

    public void clear() {
        clearActions();
        clearListeners();
    }

    public Stage getStage() {
        return this.stage;
    }

    /* access modifiers changed from: protected */
    public void setStage(Stage stage2) {
        this.stage = stage2;
    }

    public boolean isDescendantOf(Actor actor) {
        if (actor != null) {
            Actor parent2 = this;
            while (parent2 != actor) {
                parent2 = parent2.parent;
                if (parent2 == null) {
                    return false;
                }
            }
            return true;
        }
        throw new IllegalArgumentException("actor cannot be null.");
    }

    public boolean isAscendantOf(Actor actor) {
        if (actor != null) {
            while (actor != this) {
                actor = actor.parent;
                if (actor == null) {
                    return false;
                }
            }
            return true;
        }
        throw new IllegalArgumentException("actor cannot be null.");
    }

    public <T extends Actor> T firstAscendant(Class<T> type) {
        if (type != null) {
            Actor actor = this;
            while (!ClassReflection.isInstance(type, actor)) {
                actor = actor.parent;
                if (actor == null) {
                    return null;
                }
            }
            return actor;
        }
        throw new IllegalArgumentException("actor cannot be null.");
    }

    public boolean hasParent() {
        return this.parent != null;
    }

    public Group getParent() {
        return this.parent;
    }

    /* access modifiers changed from: protected */
    public void setParent(Group parent2) {
        this.parent = parent2;
    }

    public boolean isTouchable() {
        return this.touchable == Touchable.enabled;
    }

    public Touchable getTouchable() {
        return this.touchable;
    }

    public void setTouchable(Touchable touchable2) {
        this.touchable = touchable2;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public void setVisible(boolean visible2) {
        this.visible = visible2;
    }

    public boolean ancestorsVisible() {
        Actor actor = this;
        while (actor.isVisible()) {
            actor = actor.parent;
            if (actor == null) {
                return true;
            }
        }
        return false;
    }

    public boolean hasKeyboardFocus() {
        Stage stage2 = getStage();
        return stage2 != null && stage2.getKeyboardFocus() == this;
    }

    public boolean hasScrollFocus() {
        Stage stage2 = getStage();
        return stage2 != null && stage2.getScrollFocus() == this;
    }

    public boolean isTouchFocusTarget() {
        Stage stage2 = getStage();
        if (stage2 == null) {
            return false;
        }
        int n = stage2.touchFocuses.size;
        for (int i = 0; i < n; i++) {
            if (stage2.touchFocuses.get(i).target == this) {
                return true;
            }
        }
        return false;
    }

    public boolean isTouchFocusListener() {
        Stage stage2 = getStage();
        if (stage2 == null) {
            return false;
        }
        int n = stage2.touchFocuses.size;
        for (int i = 0; i < n; i++) {
            if (stage2.touchFocuses.get(i).listenerActor == this) {
                return true;
            }
        }
        return false;
    }

    public Object getUserObject() {
        return this.userObject;
    }

    public void setUserObject(Object userObject2) {
        this.userObject = userObject2;
    }

    public float getX() {
        return this.x;
    }

    public float getX(int alignment) {
        float x2 = this.x;
        if ((alignment & 16) != 0) {
            return x2 + this.width;
        }
        if ((alignment & 8) == 0) {
            return x2 + (this.width / 2.0f);
        }
        return x2;
    }

    public void setX(float x2) {
        if (this.x != x2) {
            this.x = x2;
            positionChanged();
        }
    }

    public void setX(float x2, int alignment) {
        if ((alignment & 16) != 0) {
            x2 -= this.width;
        } else if ((alignment & 8) == 0) {
            x2 -= this.width / 2.0f;
        }
        if (this.x != x2) {
            this.x = x2;
            positionChanged();
        }
    }

    public float getY() {
        return this.y;
    }

    public void setY(float y2) {
        if (this.y != y2) {
            this.y = y2;
            positionChanged();
        }
    }

    public void setY(float y2, int alignment) {
        if ((alignment & 2) != 0) {
            y2 -= this.height;
        } else if ((alignment & 4) == 0) {
            y2 -= this.height / 2.0f;
        }
        if (this.y != y2) {
            this.y = y2;
            positionChanged();
        }
    }

    public float getY(int alignment) {
        float y2 = this.y;
        if ((alignment & 2) != 0) {
            return y2 + this.height;
        }
        if ((alignment & 4) == 0) {
            return y2 + (this.height / 2.0f);
        }
        return y2;
    }

    public void setPosition(float x2, float y2) {
        if (this.x != x2 || this.y != y2) {
            this.x = x2;
            this.y = y2;
            positionChanged();
        }
    }

    public void setPosition(float x2, float y2, int alignment) {
        if ((alignment & 16) != 0) {
            x2 -= this.width;
        } else if ((alignment & 8) == 0) {
            x2 -= this.width / 2.0f;
        }
        if ((alignment & 2) != 0) {
            y2 -= this.height;
        } else if ((alignment & 4) == 0) {
            y2 -= this.height / 2.0f;
        }
        if (this.x != x2 || this.y != y2) {
            this.x = x2;
            this.y = y2;
            positionChanged();
        }
    }

    public void moveBy(float x2, float y2) {
        if (x2 != 0.0f || y2 != 0.0f) {
            this.x += x2;
            this.y += y2;
            positionChanged();
        }
    }

    public float getWidth() {
        return this.width;
    }

    public void setWidth(float width2) {
        if (this.width != width2) {
            this.width = width2;
            sizeChanged();
        }
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height2) {
        if (this.height != height2) {
            this.height = height2;
            sizeChanged();
        }
    }

    public float getTop() {
        return this.y + this.height;
    }

    public float getRight() {
        return this.x + this.width;
    }

    /* access modifiers changed from: protected */
    public void positionChanged() {
    }

    /* access modifiers changed from: protected */
    public void sizeChanged() {
    }

    /* access modifiers changed from: protected */
    public void rotationChanged() {
    }

    public void setSize(float width2, float height2) {
        if (this.width != width2 || this.height != height2) {
            this.width = width2;
            this.height = height2;
            sizeChanged();
        }
    }

    public void sizeBy(float size) {
        if (size != 0.0f) {
            this.width += size;
            this.height += size;
            sizeChanged();
        }
    }

    public void sizeBy(float width2, float height2) {
        if (width2 != 0.0f || height2 != 0.0f) {
            this.width += width2;
            this.height += height2;
            sizeChanged();
        }
    }

    public void setBounds(float x2, float y2, float width2, float height2) {
        if (!(this.x == x2 && this.y == y2)) {
            this.x = x2;
            this.y = y2;
            positionChanged();
        }
        if (this.width != width2 || this.height != height2) {
            this.width = width2;
            this.height = height2;
            sizeChanged();
        }
    }

    public float getOriginX() {
        return this.originX;
    }

    public void setOriginX(float originX2) {
        this.originX = originX2;
    }

    public float getOriginY() {
        return this.originY;
    }

    public void setOriginY(float originY2) {
        this.originY = originY2;
    }

    public void setOrigin(float originX2, float originY2) {
        this.originX = originX2;
        this.originY = originY2;
    }

    public void setOrigin(int alignment) {
        if ((alignment & 8) != 0) {
            this.originX = 0.0f;
        } else if ((alignment & 16) != 0) {
            this.originX = this.width;
        } else {
            this.originX = this.width / 2.0f;
        }
        if ((alignment & 4) != 0) {
            this.originY = 0.0f;
        } else if ((alignment & 2) != 0) {
            this.originY = this.height;
        } else {
            this.originY = this.height / 2.0f;
        }
    }

    public float getScaleX() {
        return this.scaleX;
    }

    public void setScaleX(float scaleX2) {
        this.scaleX = scaleX2;
    }

    public float getScaleY() {
        return this.scaleY;
    }

    public void setScaleY(float scaleY2) {
        this.scaleY = scaleY2;
    }

    public void setScale(float scaleXY) {
        this.scaleX = scaleXY;
        this.scaleY = scaleXY;
    }

    public void setScale(float scaleX2, float scaleY2) {
        this.scaleX = scaleX2;
        this.scaleY = scaleY2;
    }

    public void scaleBy(float scale) {
        this.scaleX += scale;
        this.scaleY += scale;
    }

    public void scaleBy(float scaleX2, float scaleY2) {
        this.scaleX += scaleX2;
        this.scaleY += scaleY2;
    }

    public float getRotation() {
        return this.rotation;
    }

    public void setRotation(float degrees) {
        if (this.rotation != degrees) {
            this.rotation = degrees;
            rotationChanged();
        }
    }

    public void rotateBy(float amountInDegrees) {
        if (amountInDegrees != 0.0f) {
            this.rotation = (this.rotation + amountInDegrees) % 360.0f;
            rotationChanged();
        }
    }

    public void setColor(Color color2) {
        this.color.set(color2);
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
    }

    public Color getColor() {
        return this.color;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public void toFront() {
        setZIndex(Integer.MAX_VALUE);
    }

    public void toBack() {
        setZIndex(0);
    }

    public boolean setZIndex(int index) {
        if (index >= 0) {
            Group parent2 = this.parent;
            if (parent2 == null) {
                return false;
            }
            Array<Actor> children = parent2.children;
            if (children.size == 1) {
                return false;
            }
            int index2 = Math.min(index, children.size - 1);
            if (children.get(index2) == this || !children.removeValue(this, true)) {
                return false;
            }
            children.insert(index2, this);
            return true;
        }
        throw new IllegalArgumentException("ZIndex cannot be < 0.");
    }

    public int getZIndex() {
        Group parent2 = this.parent;
        if (parent2 == null) {
            return -1;
        }
        return parent2.children.indexOf(this, true);
    }

    public boolean clipBegin() {
        return clipBegin(this.x, this.y, this.width, this.height);
    }

    public boolean clipBegin(float x2, float y2, float width2, float height2) {
        Stage stage2;
        if (width2 <= 0.0f || height2 <= 0.0f || (stage2 = this.stage) == null) {
            return false;
        }
        Rectangle tableBounds = Rectangle.tmp;
        tableBounds.x = x2;
        tableBounds.y = y2;
        tableBounds.width = width2;
        tableBounds.height = height2;
        Rectangle scissorBounds = (Rectangle) Pools.obtain(Rectangle.class);
        stage2.calculateScissors(tableBounds, scissorBounds);
        if (ScissorStack.pushScissors(scissorBounds)) {
            return true;
        }
        Pools.free(scissorBounds);
        return false;
    }

    public void clipEnd() {
        Pools.free(ScissorStack.popScissors());
    }

    public Vector2 screenToLocalCoordinates(Vector2 screenCoords) {
        Stage stage2 = this.stage;
        if (stage2 == null) {
            return screenCoords;
        }
        return stageToLocalCoordinates(stage2.screenToStageCoordinates(screenCoords));
    }

    public Vector2 stageToLocalCoordinates(Vector2 stageCoords) {
        Group group = this.parent;
        if (group != null) {
            group.stageToLocalCoordinates(stageCoords);
        }
        parentToLocalCoordinates(stageCoords);
        return stageCoords;
    }

    public Vector2 parentToLocalCoordinates(Vector2 parentCoords) {
        float rotation2 = this.rotation;
        float scaleX2 = this.scaleX;
        float scaleY2 = this.scaleY;
        float childX = this.x;
        float childY = this.y;
        if (rotation2 != 0.0f) {
            float cos = (float) Math.cos((double) (rotation2 * 0.017453292f));
            float sin = (float) Math.sin((double) (0.017453292f * rotation2));
            float originX2 = this.originX;
            float originY2 = this.originY;
            float tox = (parentCoords.x - childX) - originX2;
            float toy = (parentCoords.y - childY) - originY2;
            parentCoords.x = (((tox * cos) + (toy * sin)) / scaleX2) + originX2;
            parentCoords.y = ((((-sin) * tox) + (toy * cos)) / scaleY2) + originY2;
        } else if (scaleX2 == 1.0f && scaleY2 == 1.0f) {
            parentCoords.x -= childX;
            parentCoords.y -= childY;
        } else {
            float originX3 = this.originX;
            float originY3 = this.originY;
            parentCoords.x = (((parentCoords.x - childX) - originX3) / scaleX2) + originX3;
            parentCoords.y = (((parentCoords.y - childY) - originY3) / scaleY2) + originY3;
        }
        return parentCoords;
    }

    public Vector2 localToScreenCoordinates(Vector2 localCoords) {
        Stage stage2 = this.stage;
        if (stage2 == null) {
            return localCoords;
        }
        return stage2.stageToScreenCoordinates(localToAscendantCoordinates((Actor) null, localCoords));
    }

    public Vector2 localToStageCoordinates(Vector2 localCoords) {
        return localToAscendantCoordinates((Actor) null, localCoords);
    }

    public Vector2 localToParentCoordinates(Vector2 localCoords) {
        float rotation2 = -this.rotation;
        float scaleX2 = this.scaleX;
        float scaleY2 = this.scaleY;
        float x2 = this.x;
        float y2 = this.y;
        if (rotation2 != 0.0f) {
            float cos = (float) Math.cos((double) (rotation2 * 0.017453292f));
            float sin = (float) Math.sin((double) (0.017453292f * rotation2));
            float originX2 = this.originX;
            float originY2 = this.originY;
            float tox = (localCoords.x - originX2) * scaleX2;
            float toy = (localCoords.y - originY2) * scaleY2;
            localCoords.x = (tox * cos) + (toy * sin) + originX2 + x2;
            localCoords.y = ((-sin) * tox) + (toy * cos) + originY2 + y2;
        } else if (scaleX2 == 1.0f && scaleY2 == 1.0f) {
            localCoords.x += x2;
            localCoords.y += y2;
        } else {
            float originX3 = this.originX;
            float originY3 = this.originY;
            localCoords.x = ((localCoords.x - originX3) * scaleX2) + originX3 + x2;
            localCoords.y = ((localCoords.y - originY3) * scaleY2) + originY3 + y2;
        }
        return localCoords;
    }

    public Vector2 localToAscendantCoordinates(Actor ascendant, Vector2 localCoords) {
        Actor actor = this;
        do {
            actor.localToParentCoordinates(localCoords);
            actor = actor.parent;
            if (actor != ascendant) {
                break;
                break;
            }
            break;
        } while (actor != null);
        return localCoords;
    }

    public Vector2 localToActorCoordinates(Actor actor, Vector2 localCoords) {
        localToStageCoordinates(localCoords);
        return actor.stageToLocalCoordinates(localCoords);
    }

    public void drawDebug(ShapeRenderer shapes) {
        drawDebugBounds(shapes);
    }

    /* access modifiers changed from: protected */
    public void drawDebugBounds(ShapeRenderer shapes) {
        if (this.debug) {
            shapes.set(ShapeRenderer.ShapeType.Line);
            Stage stage2 = this.stage;
            if (stage2 != null) {
                shapes.setColor(stage2.getDebugColor());
            }
            shapes.rect(this.x, this.y, this.originX, this.originY, this.width, this.height, this.scaleX, this.scaleY, this.rotation);
        }
    }

    public void setDebug(boolean enabled) {
        this.debug = enabled;
        if (enabled) {
            Stage.debug = true;
        }
    }

    public boolean getDebug() {
        return this.debug;
    }

    public Actor debug() {
        setDebug(true);
        return this;
    }

    public String toString() {
        String name2 = this.name;
        if (name2 != null) {
            return name2;
        }
        String name3 = getClass().getName();
        int dotIndex = name3.lastIndexOf(46);
        if (dotIndex != -1) {
            return name3.substring(dotIndex + 1);
        }
        return name3;
    }
}

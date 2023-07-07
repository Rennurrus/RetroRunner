package com.badlogic.gdx.scenes.scene2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.Cullable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.SnapshotArray;
import java.util.Iterator;

public class Group extends Actor implements Cullable {
    private static final Vector2 tmp = new Vector2();
    final SnapshotArray<Actor> children = new SnapshotArray<>(true, 4, Actor.class);
    private final Matrix4 computedTransform = new Matrix4();
    private Rectangle cullingArea;
    private final Matrix4 oldTransform = new Matrix4();
    boolean transform = true;
    private final Affine2 worldTransform = new Affine2();

    public void act(float delta) {
        super.act(delta);
        Actor[] actors = (Actor[]) this.children.begin();
        int n = this.children.size;
        for (int i = 0; i < n; i++) {
            actors[i].act(delta);
        }
        this.children.end();
    }

    public void draw(Batch batch, float parentAlpha) {
        if (this.transform) {
            applyTransform(batch, computeTransform());
        }
        drawChildren(batch, parentAlpha);
        if (this.transform) {
            resetTransform(batch);
        }
    }

    /* access modifiers changed from: protected */
    public void drawChildren(Batch batch, float parentAlpha) {
        Rectangle cullingArea2;
        float cullRight;
        Batch batch2 = batch;
        float parentAlpha2 = this.color.a * parentAlpha;
        SnapshotArray<Actor> children2 = this.children;
        Actor[] actors = (Actor[]) children2.begin();
        Rectangle cullingArea3 = this.cullingArea;
        if (cullingArea3 != null) {
            float cullLeft = cullingArea3.x;
            float cullRight2 = cullingArea3.width + cullLeft;
            float cullBottom = cullingArea3.y;
            float cullTop = cullingArea3.height + cullBottom;
            if (this.transform) {
                int n = children2.size;
                for (int i = 0; i < n; i++) {
                    Actor child = actors[i];
                    if (child.isVisible()) {
                        float cx = child.x;
                        float cy = child.y;
                        if (cx <= cullRight2 && cy <= cullTop && child.width + cx >= cullLeft && child.height + cy >= cullBottom) {
                            child.draw(batch2, parentAlpha2);
                        }
                    }
                }
                Rectangle rectangle = cullingArea3;
            } else {
                float offsetX = this.x;
                float offsetY = this.y;
                this.x = 0.0f;
                this.y = 0.0f;
                int i2 = 0;
                int n2 = children2.size;
                while (i2 < n2) {
                    Actor child2 = actors[i2];
                    if (!child2.isVisible()) {
                        cullingArea2 = cullingArea3;
                        cullRight = cullRight2;
                    } else {
                        float cx2 = child2.x;
                        cullingArea2 = cullingArea3;
                        float cy2 = child2.y;
                        if (cx2 > cullRight2 || cy2 > cullTop) {
                            cullRight = cullRight2;
                        } else {
                            cullRight = cullRight2;
                            if (child2.width + cx2 >= cullLeft && child2.height + cy2 >= cullBottom) {
                                child2.x = cx2 + offsetX;
                                child2.y = cy2 + offsetY;
                                child2.draw(batch2, parentAlpha2);
                                child2.x = cx2;
                                child2.y = cy2;
                            }
                        }
                    }
                    i2++;
                    cullingArea3 = cullingArea2;
                    cullRight2 = cullRight;
                }
                float f = cullRight2;
                this.x = offsetX;
                this.y = offsetY;
            }
        } else {
            if (this.transform) {
                int n3 = children2.size;
                for (int i3 = 0; i3 < n3; i3++) {
                    Actor child3 = actors[i3];
                    if (child3.isVisible()) {
                        child3.draw(batch2, parentAlpha2);
                    }
                }
            } else {
                float offsetX2 = this.x;
                float offsetY2 = this.y;
                this.x = 0.0f;
                this.y = 0.0f;
                int n4 = children2.size;
                for (int i4 = 0; i4 < n4; i4++) {
                    Actor child4 = actors[i4];
                    if (child4.isVisible()) {
                        float cx3 = child4.x;
                        float cy3 = child4.y;
                        child4.x = cx3 + offsetX2;
                        child4.y = cy3 + offsetY2;
                        child4.draw(batch2, parentAlpha2);
                        child4.x = cx3;
                        child4.y = cy3;
                    }
                }
                this.x = offsetX2;
                this.y = offsetY2;
            }
        }
        children2.end();
    }

    public void drawDebug(ShapeRenderer shapes) {
        drawDebugBounds(shapes);
        if (this.transform) {
            applyTransform(shapes, computeTransform());
        }
        drawDebugChildren(shapes);
        if (this.transform) {
            resetTransform(shapes);
        }
    }

    /* access modifiers changed from: protected */
    public void drawDebugChildren(ShapeRenderer shapes) {
        SnapshotArray<Actor> children2 = this.children;
        Actor[] actors = (Actor[]) children2.begin();
        if (this.transform) {
            int n = children2.size;
            for (int i = 0; i < n; i++) {
                Actor child = actors[i];
                if (child.isVisible() && (child.getDebug() || (child instanceof Group))) {
                    child.drawDebug(shapes);
                }
            }
            shapes.flush();
        } else {
            float offsetX = this.x;
            float offsetY = this.y;
            this.x = 0.0f;
            this.y = 0.0f;
            int n2 = children2.size;
            for (int i2 = 0; i2 < n2; i2++) {
                Actor child2 = actors[i2];
                if (child2.isVisible() && (child2.getDebug() || (child2 instanceof Group))) {
                    float cx = child2.x;
                    float cy = child2.y;
                    child2.x = cx + offsetX;
                    child2.y = cy + offsetY;
                    child2.drawDebug(shapes);
                    child2.x = cx;
                    child2.y = cy;
                }
            }
            this.x = offsetX;
            this.y = offsetY;
        }
        children2.end();
    }

    /* access modifiers changed from: protected */
    public Matrix4 computeTransform() {
        Affine2 worldTransform2 = this.worldTransform;
        float originX = this.originX;
        float originY = this.originY;
        worldTransform2.setToTrnRotScl(this.x + originX, this.y + originY, this.rotation, this.scaleX, this.scaleY);
        if (!(originX == 0.0f && originY == 0.0f)) {
            worldTransform2.translate(-originX, -originY);
        }
        Group parentGroup = this.parent;
        while (parentGroup != null && !parentGroup.transform) {
            parentGroup = parentGroup.parent;
        }
        if (parentGroup != null) {
            worldTransform2.preMul(parentGroup.worldTransform);
        }
        this.computedTransform.set(worldTransform2);
        return this.computedTransform;
    }

    /* access modifiers changed from: protected */
    public void applyTransform(Batch batch, Matrix4 transform2) {
        this.oldTransform.set(batch.getTransformMatrix());
        batch.setTransformMatrix(transform2);
    }

    /* access modifiers changed from: protected */
    public void resetTransform(Batch batch) {
        batch.setTransformMatrix(this.oldTransform);
    }

    /* access modifiers changed from: protected */
    public void applyTransform(ShapeRenderer shapes, Matrix4 transform2) {
        this.oldTransform.set(shapes.getTransformMatrix());
        shapes.setTransformMatrix(transform2);
        shapes.flush();
    }

    /* access modifiers changed from: protected */
    public void resetTransform(ShapeRenderer shapes) {
        shapes.setTransformMatrix(this.oldTransform);
    }

    public void setCullingArea(Rectangle cullingArea2) {
        this.cullingArea = cullingArea2;
    }

    public Rectangle getCullingArea() {
        return this.cullingArea;
    }

    public Actor hit(float x, float y, boolean touchable) {
        if ((touchable && getTouchable() == Touchable.disabled) || !isVisible()) {
            return null;
        }
        Vector2 point = tmp;
        Actor[] childrenArray = (Actor[]) this.children.items;
        for (int i = this.children.size - 1; i >= 0; i--) {
            Actor child = childrenArray[i];
            child.parentToLocalCoordinates(point.set(x, y));
            Actor hit = child.hit(point.x, point.y, touchable);
            if (hit != null) {
                return hit;
            }
        }
        return super.hit(x, y, touchable);
    }

    /* access modifiers changed from: protected */
    public void childrenChanged() {
    }

    public void addActor(Actor actor) {
        if (actor.parent != null) {
            if (actor.parent != this) {
                actor.parent.removeActor(actor, false);
            } else {
                return;
            }
        }
        this.children.add(actor);
        actor.setParent(this);
        actor.setStage(getStage());
        childrenChanged();
    }

    public void addActorAt(int index, Actor actor) {
        if (actor.parent != null) {
            if (actor.parent != this) {
                actor.parent.removeActor(actor, false);
            } else {
                return;
            }
        }
        if (index >= this.children.size) {
            this.children.add(actor);
        } else {
            this.children.insert(index, actor);
        }
        actor.setParent(this);
        actor.setStage(getStage());
        childrenChanged();
    }

    public void addActorBefore(Actor actorBefore, Actor actor) {
        if (actor.parent != null) {
            if (actor.parent != this) {
                actor.parent.removeActor(actor, false);
            } else {
                return;
            }
        }
        this.children.insert(this.children.indexOf(actorBefore, true), actor);
        actor.setParent(this);
        actor.setStage(getStage());
        childrenChanged();
    }

    public void addActorAfter(Actor actorAfter, Actor actor) {
        if (actor.parent != null) {
            if (actor.parent != this) {
                actor.parent.removeActor(actor, false);
            } else {
                return;
            }
        }
        int index = this.children.indexOf(actorAfter, true);
        if (index == this.children.size) {
            this.children.add(actor);
        } else {
            this.children.insert(index + 1, actor);
        }
        actor.setParent(this);
        actor.setStage(getStage());
        childrenChanged();
    }

    public boolean removeActor(Actor actor) {
        return removeActor(actor, true);
    }

    public boolean removeActor(Actor actor, boolean unfocus) {
        Stage stage;
        if (!this.children.removeValue(actor, true)) {
            return false;
        }
        if (unfocus && (stage = getStage()) != null) {
            stage.unfocus(actor);
        }
        actor.setParent((Group) null);
        actor.setStage((Stage) null);
        childrenChanged();
        return true;
    }

    public void clearChildren() {
        Actor[] actors = (Actor[]) this.children.begin();
        int n = this.children.size;
        for (int i = 0; i < n; i++) {
            Actor child = actors[i];
            child.setStage((Stage) null);
            child.setParent((Group) null);
        }
        this.children.end();
        this.children.clear();
        childrenChanged();
    }

    public void clear() {
        super.clear();
        clearChildren();
    }

    public <T extends Actor> T findActor(String name) {
        Actor actor;
        Array<Actor> children2 = this.children;
        int n = children2.size;
        for (int i = 0; i < n; i++) {
            if (name.equals(children2.get(i).getName())) {
                return children2.get(i);
            }
        }
        int n2 = children2.size;
        for (int i2 = 0; i2 < n2; i2++) {
            Actor child = children2.get(i2);
            if ((child instanceof Group) && (actor = ((Group) child).findActor(name)) != null) {
                return actor;
            }
        }
        return null;
    }

    /* access modifiers changed from: protected */
    public void setStage(Stage stage) {
        super.setStage(stage);
        Actor[] childrenArray = (Actor[]) this.children.items;
        int n = this.children.size;
        for (int i = 0; i < n; i++) {
            childrenArray[i].setStage(stage);
        }
    }

    public boolean swapActor(int first, int second) {
        int maxIndex = this.children.size;
        if (first < 0 || first >= maxIndex || second < 0 || second >= maxIndex) {
            return false;
        }
        this.children.swap(first, second);
        return true;
    }

    public boolean swapActor(Actor first, Actor second) {
        int firstIndex = this.children.indexOf(first, true);
        int secondIndex = this.children.indexOf(second, true);
        if (firstIndex == -1 || secondIndex == -1) {
            return false;
        }
        this.children.swap(firstIndex, secondIndex);
        return true;
    }

    public Actor getChild(int index) {
        return this.children.get(index);
    }

    public SnapshotArray<Actor> getChildren() {
        return this.children;
    }

    public boolean hasChildren() {
        return this.children.size > 0;
    }

    public void setTransform(boolean transform2) {
        this.transform = transform2;
    }

    public boolean isTransform() {
        return this.transform;
    }

    public Vector2 localToDescendantCoordinates(Actor descendant, Vector2 localCoords) {
        Group parent = descendant.parent;
        if (parent != null) {
            if (parent != this) {
                localToDescendantCoordinates(parent, localCoords);
            }
            descendant.parentToLocalCoordinates(localCoords);
            return localCoords;
        }
        throw new IllegalArgumentException("Child is not a descendant: " + descendant);
    }

    public void setDebug(boolean enabled, boolean recursively) {
        setDebug(enabled);
        if (recursively) {
            Iterator<Actor> it = this.children.iterator();
            while (it.hasNext()) {
                Actor child = it.next();
                if (child instanceof Group) {
                    ((Group) child).setDebug(enabled, recursively);
                } else {
                    child.setDebug(enabled);
                }
            }
        }
    }

    public Group debugAll() {
        setDebug(true, true);
        return this;
    }

    public String toString() {
        StringBuilder buffer = new StringBuilder(128);
        toString(buffer, 1);
        buffer.setLength(buffer.length() - 1);
        return buffer.toString();
    }

    /* access modifiers changed from: package-private */
    public void toString(StringBuilder buffer, int indent) {
        buffer.append(super.toString());
        buffer.append(10);
        Actor[] actors = (Actor[]) this.children.begin();
        int n = this.children.size;
        for (int i = 0; i < n; i++) {
            for (int ii = 0; ii < indent; ii++) {
                buffer.append("|  ");
            }
            Actor actor = actors[i];
            if (actor instanceof Group) {
                ((Group) actor).toString(buffer, indent + 1);
            } else {
                buffer.append(actor);
                buffer.append(10);
            }
        }
        this.children.end();
    }
}

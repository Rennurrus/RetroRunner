package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class DragAndDrop {
    static final Vector2 tmpVector = new Vector2();
    int activePointer = -1;
    private int button;
    boolean cancelTouchFocus = true;
    Actor dragActor;
    float dragActorX = 0.0f;
    float dragActorY = 0.0f;
    Source dragSource;
    int dragTime = Input.Keys.F7;
    long dragValidTime;
    boolean isValidTarget;
    boolean keepWithinStage = true;
    Payload payload;
    final ObjectMap<Source, DragListener> sourceListeners = new ObjectMap<>();
    private float tapSquareSize = 8.0f;
    Target target;
    final Array<Target> targets = new Array<>();
    float touchOffsetX;
    float touchOffsetY;

    public void addSource(final Source source) {
        DragListener listener = new DragListener() {
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                Stage stage;
                if (DragAndDrop.this.activePointer != -1) {
                    event.stop();
                    return;
                }
                DragAndDrop dragAndDrop = DragAndDrop.this;
                dragAndDrop.activePointer = pointer;
                dragAndDrop.dragValidTime = System.currentTimeMillis() + ((long) DragAndDrop.this.dragTime);
                DragAndDrop dragAndDrop2 = DragAndDrop.this;
                Source source = source;
                dragAndDrop2.dragSource = source;
                dragAndDrop2.payload = source.dragStart(event, getTouchDownX(), getTouchDownY(), pointer);
                event.stop();
                if (DragAndDrop.this.cancelTouchFocus && DragAndDrop.this.payload != null && (stage = source.getActor().getStage()) != null) {
                    stage.cancelTouchFocusExcept(this, source.getActor());
                }
            }

            /* JADX WARNING: Removed duplicated region for block: B:23:0x0096  */
            /* JADX WARNING: Removed duplicated region for block: B:28:0x00af  */
            /* JADX WARNING: Removed duplicated region for block: B:31:0x00ce  */
            /* JADX WARNING: Removed duplicated region for block: B:37:0x00e4  */
            /* JADX WARNING: Removed duplicated region for block: B:40:0x00f0 A[RETURN] */
            /* JADX WARNING: Removed duplicated region for block: B:41:0x00f1  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void drag(com.badlogic.gdx.scenes.scene2d.InputEvent r17, float r18, float r19, int r20) {
                /*
                    r16 = this;
                    r0 = r16
                    r7 = r20
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r1 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Payload r1 = r1.payload
                    if (r1 != 0) goto L_0x000b
                    return
                L_0x000b:
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r1 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    int r1 = r1.activePointer
                    if (r7 == r1) goto L_0x0012
                    return
                L_0x0012:
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Source r1 = r3
                    r8 = r17
                    r9 = r18
                    r10 = r19
                    r1.drag(r8, r9, r10, r7)
                    com.badlogic.gdx.scenes.scene2d.Stage r11 = r17.getStage()
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r1 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    com.badlogic.gdx.scenes.scene2d.Actor r1 = r1.dragActor
                    if (r1 == 0) goto L_0x0033
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r1 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    com.badlogic.gdx.scenes.scene2d.Actor r1 = r1.dragActor
                    r1.remove()
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r1 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    r2 = 0
                    r1.dragActor = r2
                L_0x0033:
                    r1 = 0
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r2 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    r3 = 0
                    r2.isValidTarget = r3
                    float r2 = r17.getStageX()
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r4 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    float r4 = r4.touchOffsetX
                    float r12 = r2 + r4
                    float r2 = r17.getStageY()
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r4 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    float r4 = r4.touchOffsetY
                    float r13 = r2 + r4
                    com.badlogic.gdx.scenes.scene2d.Stage r2 = r17.getStage()
                    r4 = 1
                    com.badlogic.gdx.scenes.scene2d.Actor r2 = r2.hit(r12, r13, r4)
                    if (r2 != 0) goto L_0x0060
                    com.badlogic.gdx.scenes.scene2d.Stage r4 = r17.getStage()
                    com.badlogic.gdx.scenes.scene2d.Actor r2 = r4.hit(r12, r13, r3)
                L_0x0060:
                    r14 = r2
                    if (r14 == 0) goto L_0x008f
                    r2 = 0
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r3 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    com.badlogic.gdx.utils.Array<com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Target> r3 = r3.targets
                    int r3 = r3.size
                L_0x006a:
                    if (r2 >= r3) goto L_0x008f
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r4 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    com.badlogic.gdx.utils.Array<com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Target> r4 = r4.targets
                    java.lang.Object r4 = r4.get(r2)
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Target r4 = (com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target) r4
                    com.badlogic.gdx.scenes.scene2d.Actor r5 = r4.actor
                    boolean r5 = r5.isAscendantOf(r14)
                    if (r5 != 0) goto L_0x0081
                    int r2 = r2 + 1
                    goto L_0x006a
                L_0x0081:
                    r1 = r4
                    com.badlogic.gdx.scenes.scene2d.Actor r5 = r4.actor
                    com.badlogic.gdx.math.Vector2 r6 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.tmpVector
                    com.badlogic.gdx.math.Vector2 r6 = r6.set(r12, r13)
                    r5.stageToLocalCoordinates(r6)
                    r15 = r1
                    goto L_0x0090
                L_0x008f:
                    r15 = r1
                L_0x0090:
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r1 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Target r1 = r1.target
                    if (r15 == r1) goto L_0x00ad
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r1 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Target r1 = r1.target
                    if (r1 == 0) goto L_0x00a9
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r1 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Target r1 = r1.target
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Source r2 = r3
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r3 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Payload r3 = r3.payload
                    r1.reset(r2, r3)
                L_0x00a9:
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r1 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    r1.target = r15
                L_0x00ad:
                    if (r15 == 0) goto L_0x00c7
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r6 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Source r2 = r3
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Payload r3 = r6.payload
                    com.badlogic.gdx.math.Vector2 r1 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.tmpVector
                    float r4 = r1.x
                    com.badlogic.gdx.math.Vector2 r1 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.tmpVector
                    float r5 = r1.y
                    r1 = r15
                    r7 = r6
                    r6 = r20
                    boolean r1 = r1.drag(r2, r3, r4, r5, r6)
                    r7.isValidTarget = r1
                L_0x00c7:
                    r1 = 0
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r2 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Target r2 = r2.target
                    if (r2 == 0) goto L_0x00e2
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r2 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    boolean r2 = r2.isValidTarget
                    if (r2 == 0) goto L_0x00db
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r2 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Payload r2 = r2.payload
                    com.badlogic.gdx.scenes.scene2d.Actor r2 = r2.validDragActor
                    goto L_0x00e1
                L_0x00db:
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r2 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Payload r2 = r2.payload
                    com.badlogic.gdx.scenes.scene2d.Actor r2 = r2.invalidDragActor
                L_0x00e1:
                    r1 = r2
                L_0x00e2:
                    if (r1 != 0) goto L_0x00ea
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r2 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop$Payload r2 = r2.payload
                    com.badlogic.gdx.scenes.scene2d.Actor r1 = r2.dragActor
                L_0x00ea:
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r2 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    r2.dragActor = r1
                    if (r1 != 0) goto L_0x00f1
                    return
                L_0x00f1:
                    r11.addActor(r1)
                    float r2 = r17.getStageX()
                    float r3 = r1.getWidth()
                    float r2 = r2 - r3
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r3 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    float r3 = r3.dragActorX
                    float r2 = r2 + r3
                    float r3 = r17.getStageY()
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r4 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    float r4 = r4.dragActorY
                    float r3 = r3 + r4
                    com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop r4 = com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.this
                    boolean r4 = r4.keepWithinStage
                    if (r4 == 0) goto L_0x014a
                    r4 = 0
                    int r5 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
                    if (r5 >= 0) goto L_0x0117
                    r2 = 0
                L_0x0117:
                    int r4 = (r3 > r4 ? 1 : (r3 == r4 ? 0 : -1))
                    if (r4 >= 0) goto L_0x011c
                    r3 = 0
                L_0x011c:
                    float r4 = r1.getWidth()
                    float r4 = r4 + r2
                    float r5 = r11.getWidth()
                    int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                    if (r4 <= 0) goto L_0x0133
                    float r4 = r11.getWidth()
                    float r5 = r1.getWidth()
                    float r4 = r4 - r5
                    r2 = r4
                L_0x0133:
                    float r4 = r1.getHeight()
                    float r4 = r4 + r3
                    float r5 = r11.getHeight()
                    int r4 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
                    if (r4 <= 0) goto L_0x014a
                    float r4 = r11.getHeight()
                    float r5 = r1.getHeight()
                    float r3 = r4 - r5
                L_0x014a:
                    r1.setPosition(r2, r3)
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.AnonymousClass1.drag(com.badlogic.gdx.scenes.scene2d.InputEvent, float, float, int):void");
            }

            public void dragStop(InputEvent event, float x, float y, int pointer) {
                if (pointer == DragAndDrop.this.activePointer) {
                    DragAndDrop dragAndDrop = DragAndDrop.this;
                    dragAndDrop.activePointer = -1;
                    if (dragAndDrop.payload != null) {
                        if (System.currentTimeMillis() < DragAndDrop.this.dragValidTime) {
                            DragAndDrop.this.isValidTarget = false;
                        }
                        if (DragAndDrop.this.dragActor != null) {
                            DragAndDrop.this.dragActor.remove();
                        }
                        if (DragAndDrop.this.isValidTarget) {
                            DragAndDrop.this.target.actor.stageToLocalCoordinates(DragAndDrop.tmpVector.set(event.getStageX() + DragAndDrop.this.touchOffsetX, event.getStageY() + DragAndDrop.this.touchOffsetY));
                            DragAndDrop.this.target.drop(source, DragAndDrop.this.payload, DragAndDrop.tmpVector.x, DragAndDrop.tmpVector.y, pointer);
                        }
                        source.dragStop(event, x, y, pointer, DragAndDrop.this.payload, DragAndDrop.this.isValidTarget ? DragAndDrop.this.target : null);
                        if (DragAndDrop.this.target != null) {
                            DragAndDrop.this.target.reset(source, DragAndDrop.this.payload);
                        }
                        DragAndDrop dragAndDrop2 = DragAndDrop.this;
                        dragAndDrop2.dragSource = null;
                        dragAndDrop2.payload = null;
                        dragAndDrop2.target = null;
                        dragAndDrop2.isValidTarget = false;
                        dragAndDrop2.dragActor = null;
                    }
                }
            }
        };
        listener.setTapSquareSize(this.tapSquareSize);
        listener.setButton(this.button);
        source.actor.addCaptureListener(listener);
        this.sourceListeners.put(source, listener);
    }

    public void removeSource(Source source) {
        source.actor.removeCaptureListener(this.sourceListeners.remove(source));
    }

    public void addTarget(Target target2) {
        this.targets.add(target2);
    }

    public void removeTarget(Target target2) {
        this.targets.removeValue(target2, true);
    }

    public void clear() {
        this.targets.clear();
        ObjectMap.Entries<Source, DragListener> it = this.sourceListeners.entries().iterator();
        while (it.hasNext()) {
            ObjectMap.Entry<Source, DragListener> entry = (ObjectMap.Entry) it.next();
            ((Source) entry.key).actor.removeCaptureListener((EventListener) entry.value);
        }
        this.sourceListeners.clear();
    }

    public void cancelTouchFocusExcept(Source except) {
        Stage stage;
        DragListener listener = this.sourceListeners.get(except);
        if (listener != null && (stage = except.getActor().getStage()) != null) {
            stage.cancelTouchFocusExcept(listener, except.getActor());
        }
    }

    public void setTapSquareSize(float halfTapSquareSize) {
        this.tapSquareSize = halfTapSquareSize;
    }

    public void setButton(int button2) {
        this.button = button2;
    }

    public void setDragActorPosition(float dragActorX2, float dragActorY2) {
        this.dragActorX = dragActorX2;
        this.dragActorY = dragActorY2;
    }

    public void setTouchOffset(float touchOffsetX2, float touchOffsetY2) {
        this.touchOffsetX = touchOffsetX2;
        this.touchOffsetY = touchOffsetY2;
    }

    public boolean isDragging() {
        return this.payload != null;
    }

    public Actor getDragActor() {
        return this.dragActor;
    }

    public Payload getDragPayload() {
        return this.payload;
    }

    public Source getDragSource() {
        return this.dragSource;
    }

    public void setDragTime(int dragMillis) {
        this.dragTime = dragMillis;
    }

    public int getDragTime() {
        return this.dragTime;
    }

    public boolean isDragValid() {
        return this.payload != null && System.currentTimeMillis() >= this.dragValidTime;
    }

    public void setCancelTouchFocus(boolean cancelTouchFocus2) {
        this.cancelTouchFocus = cancelTouchFocus2;
    }

    public void setKeepWithinStage(boolean keepWithinStage2) {
        this.keepWithinStage = keepWithinStage2;
    }

    public static abstract class Source {
        final Actor actor;

        public abstract Payload dragStart(InputEvent inputEvent, float f, float f2, int i);

        public Source(Actor actor2) {
            if (actor2 != null) {
                this.actor = actor2;
                return;
            }
            throw new IllegalArgumentException("actor cannot be null.");
        }

        public void drag(InputEvent event, float x, float y, int pointer) {
        }

        public void dragStop(InputEvent event, float x, float y, int pointer, Payload payload, Target target) {
        }

        public Actor getActor() {
            return this.actor;
        }
    }

    public static abstract class Target {
        final Actor actor;

        public abstract boolean drag(Source source, Payload payload, float f, float f2, int i);

        public abstract void drop(Source source, Payload payload, float f, float f2, int i);

        public Target(Actor actor2) {
            if (actor2 != null) {
                this.actor = actor2;
                Stage stage = actor2.getStage();
                if (stage != null && actor2 == stage.getRoot()) {
                    throw new IllegalArgumentException("The stage root cannot be a drag and drop target.");
                }
                return;
            }
            throw new IllegalArgumentException("actor cannot be null.");
        }

        public void reset(Source source, Payload payload) {
        }

        public Actor getActor() {
            return this.actor;
        }
    }

    public static class Payload {
        Actor dragActor;
        Actor invalidDragActor;
        Object object;
        Actor validDragActor;

        public void setDragActor(Actor dragActor2) {
            this.dragActor = dragActor2;
        }

        public Actor getDragActor() {
            return this.dragActor;
        }

        public void setValidDragActor(Actor validDragActor2) {
            this.validDragActor = validDragActor2;
        }

        public Actor getValidDragActor() {
            return this.validDragActor;
        }

        public void setInvalidDragActor(Actor invalidDragActor2) {
            this.invalidDragActor = invalidDragActor2;
        }

        public Actor getInvalidDragActor() {
            return this.invalidDragActor;
        }

        public Object getObject() {
            return this.object;
        }

        public void setObject(Object object2) {
            this.object = object2;
        }
    }
}

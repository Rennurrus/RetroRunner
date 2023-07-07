package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.OrderedSet;
import com.badlogic.gdx.utils.Pools;
import java.util.Iterator;

public class Selection<T> implements Disableable, Iterable<T> {
    private Actor actor;
    boolean isDisabled;
    T lastSelected;
    boolean multiple;
    private final OrderedSet<T> old = new OrderedSet<>();
    private boolean programmaticChangeEvents = true;
    boolean required;
    final OrderedSet<T> selected = new OrderedSet<>();
    private boolean toggle;

    public void setActor(Actor actor2) {
        this.actor = actor2;
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:22:0x0035=Splitter:B:22:0x0035, B:47:0x007d=Splitter:B:47:0x007d, B:36:0x005f=Splitter:B:36:0x005f} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void choose(T r4) {
        /*
            r3 = this;
            if (r4 == 0) goto L_0x0096
            boolean r0 = r3.isDisabled
            if (r0 == 0) goto L_0x0007
            return
        L_0x0007:
            r3.snapshot()
            boolean r0 = r3.toggle     // Catch:{ all -> 0x0091 }
            r1 = 1
            if (r0 == 0) goto L_0x0019
            boolean r0 = r3.required     // Catch:{ all -> 0x0091 }
            if (r0 != 0) goto L_0x0019
            com.badlogic.gdx.utils.OrderedSet<T> r0 = r3.selected     // Catch:{ all -> 0x0091 }
            int r0 = r0.size     // Catch:{ all -> 0x0091 }
            if (r0 == r1) goto L_0x001f
        L_0x0019:
            boolean r0 = com.badlogic.gdx.scenes.scene2d.utils.UIUtils.ctrl()     // Catch:{ all -> 0x0091 }
            if (r0 == 0) goto L_0x003e
        L_0x001f:
            com.badlogic.gdx.utils.OrderedSet<T> r0 = r3.selected     // Catch:{ all -> 0x0091 }
            boolean r0 = r0.contains(r4)     // Catch:{ all -> 0x0091 }
            if (r0 == 0) goto L_0x003e
            boolean r0 = r3.required     // Catch:{ all -> 0x0091 }
            if (r0 == 0) goto L_0x0035
            com.badlogic.gdx.utils.OrderedSet<T> r0 = r3.selected     // Catch:{ all -> 0x0091 }
            int r0 = r0.size     // Catch:{ all -> 0x0091 }
            if (r0 != r1) goto L_0x0035
            r3.cleanup()
            return
        L_0x0035:
            com.badlogic.gdx.utils.OrderedSet<T> r0 = r3.selected     // Catch:{ all -> 0x0091 }
            r0.remove(r4)     // Catch:{ all -> 0x0091 }
            r0 = 0
            r3.lastSelected = r0     // Catch:{ all -> 0x0091 }
            goto L_0x007f
        L_0x003e:
            r0 = 0
            boolean r2 = r3.multiple     // Catch:{ all -> 0x0091 }
            if (r2 == 0) goto L_0x004d
            boolean r2 = r3.toggle     // Catch:{ all -> 0x0091 }
            if (r2 != 0) goto L_0x006f
            boolean r2 = com.badlogic.gdx.scenes.scene2d.utils.UIUtils.ctrl()     // Catch:{ all -> 0x0091 }
            if (r2 != 0) goto L_0x006f
        L_0x004d:
            com.badlogic.gdx.utils.OrderedSet<T> r2 = r3.selected     // Catch:{ all -> 0x0091 }
            int r2 = r2.size     // Catch:{ all -> 0x0091 }
            if (r2 != r1) goto L_0x005f
            com.badlogic.gdx.utils.OrderedSet<T> r2 = r3.selected     // Catch:{ all -> 0x0091 }
            boolean r2 = r2.contains(r4)     // Catch:{ all -> 0x0091 }
            if (r2 == 0) goto L_0x005f
            r3.cleanup()
            return
        L_0x005f:
            com.badlogic.gdx.utils.OrderedSet<T> r2 = r3.selected     // Catch:{ all -> 0x0091 }
            int r2 = r2.size     // Catch:{ all -> 0x0091 }
            if (r2 <= 0) goto L_0x0066
            goto L_0x0067
        L_0x0066:
            r1 = 0
        L_0x0067:
            r0 = r1
            com.badlogic.gdx.utils.OrderedSet<T> r1 = r3.selected     // Catch:{ all -> 0x0091 }
            r2 = 8
            r1.clear(r2)     // Catch:{ all -> 0x0091 }
        L_0x006f:
            com.badlogic.gdx.utils.OrderedSet<T> r1 = r3.selected     // Catch:{ all -> 0x0091 }
            boolean r1 = r1.add(r4)     // Catch:{ all -> 0x0091 }
            if (r1 != 0) goto L_0x007d
            if (r0 != 0) goto L_0x007d
            r3.cleanup()
            return
        L_0x007d:
            r3.lastSelected = r4     // Catch:{ all -> 0x0091 }
        L_0x007f:
            boolean r0 = r3.fireChangeEvent()     // Catch:{ all -> 0x0091 }
            if (r0 == 0) goto L_0x0089
            r3.revert()     // Catch:{ all -> 0x0091 }
            goto L_0x008c
        L_0x0089:
            r3.changed()     // Catch:{ all -> 0x0091 }
        L_0x008c:
            r3.cleanup()
            return
        L_0x0091:
            r0 = move-exception
            r3.cleanup()
            throw r0
        L_0x0096:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException
            java.lang.String r1 = "item cannot be null."
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.scenes.scene2d.utils.Selection.choose(java.lang.Object):void");
    }

    @Deprecated
    public boolean hasItems() {
        return this.selected.size > 0;
    }

    public boolean notEmpty() {
        return this.selected.size > 0;
    }

    public boolean isEmpty() {
        return this.selected.size == 0;
    }

    public int size() {
        return this.selected.size;
    }

    public OrderedSet<T> items() {
        return this.selected;
    }

    public T first() {
        if (this.selected.size == 0) {
            return null;
        }
        return this.selected.first();
    }

    /* access modifiers changed from: package-private */
    public void snapshot() {
        this.old.clear(this.selected.size);
        this.old.addAll(this.selected);
    }

    /* access modifiers changed from: package-private */
    public void revert() {
        this.selected.clear(this.old.size);
        this.selected.addAll(this.old);
    }

    /* access modifiers changed from: package-private */
    public void cleanup() {
        this.old.clear(32);
    }

    public void set(T item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null.");
        } else if (this.selected.size != 1 || this.selected.first() != item) {
            snapshot();
            this.selected.clear(8);
            this.selected.add(item);
            if (!this.programmaticChangeEvents || !fireChangeEvent()) {
                this.lastSelected = item;
                changed();
            } else {
                revert();
            }
            cleanup();
        }
    }

    public void setAll(Array<T> items) {
        boolean added = false;
        snapshot();
        this.lastSelected = null;
        this.selected.clear(items.size);
        int i = 0;
        int n = items.size;
        while (i < n) {
            T item = items.get(i);
            if (item != null) {
                if (this.selected.add(item)) {
                    added = true;
                }
                i++;
            } else {
                throw new IllegalArgumentException("item cannot be null.");
            }
        }
        if (added) {
            if (this.programmaticChangeEvents && fireChangeEvent()) {
                revert();
            } else if (items.size > 0) {
                this.lastSelected = items.peek();
                changed();
            }
        }
        cleanup();
    }

    public void add(T item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null.");
        } else if (this.selected.add(item)) {
            if (!this.programmaticChangeEvents || !fireChangeEvent()) {
                this.lastSelected = item;
                changed();
                return;
            }
            this.selected.remove(item);
        }
    }

    public void addAll(Array<T> items) {
        boolean added = false;
        snapshot();
        int i = 0;
        int n = items.size;
        while (i < n) {
            T item = items.get(i);
            if (item != null) {
                if (this.selected.add(item)) {
                    added = true;
                }
                i++;
            } else {
                throw new IllegalArgumentException("item cannot be null.");
            }
        }
        if (added) {
            if (!this.programmaticChangeEvents || !fireChangeEvent()) {
                this.lastSelected = items.peek();
                changed();
            } else {
                revert();
            }
        }
        cleanup();
    }

    public void remove(T item) {
        if (item == null) {
            throw new IllegalArgumentException("item cannot be null.");
        } else if (this.selected.remove(item)) {
            if (!this.programmaticChangeEvents || !fireChangeEvent()) {
                this.lastSelected = null;
                changed();
                return;
            }
            this.selected.add(item);
        }
    }

    public void removeAll(Array<T> items) {
        boolean removed = false;
        snapshot();
        int i = 0;
        int n = items.size;
        while (i < n) {
            T item = items.get(i);
            if (item != null) {
                if (this.selected.remove(item)) {
                    removed = true;
                }
                i++;
            } else {
                throw new IllegalArgumentException("item cannot be null.");
            }
        }
        if (removed) {
            if (!this.programmaticChangeEvents || !fireChangeEvent()) {
                this.lastSelected = null;
                changed();
            } else {
                revert();
            }
        }
        cleanup();
    }

    public void clear() {
        if (this.selected.size != 0) {
            snapshot();
            this.selected.clear(8);
            if (!this.programmaticChangeEvents || !fireChangeEvent()) {
                this.lastSelected = null;
                changed();
            } else {
                revert();
            }
            cleanup();
        }
    }

    /* access modifiers changed from: protected */
    public void changed() {
    }

    public boolean fireChangeEvent() {
        if (this.actor == null) {
            return false;
        }
        ChangeListener.ChangeEvent changeEvent = (ChangeListener.ChangeEvent) Pools.obtain(ChangeListener.ChangeEvent.class);
        try {
            return this.actor.fire(changeEvent);
        } finally {
            Pools.free(changeEvent);
        }
    }

    public boolean contains(T item) {
        if (item == null) {
            return false;
        }
        return this.selected.contains(item);
    }

    public T getLastSelected() {
        T t = this.lastSelected;
        if (t != null) {
            return t;
        }
        if (this.selected.size > 0) {
            return this.selected.first();
        }
        return null;
    }

    public Iterator<T> iterator() {
        return this.selected.iterator();
    }

    public Array<T> toArray() {
        return this.selected.iterator().toArray();
    }

    public Array<T> toArray(Array<T> array) {
        return this.selected.iterator().toArray(array);
    }

    public void setDisabled(boolean isDisabled2) {
        this.isDisabled = isDisabled2;
    }

    public boolean isDisabled() {
        return this.isDisabled;
    }

    public boolean getToggle() {
        return this.toggle;
    }

    public void setToggle(boolean toggle2) {
        this.toggle = toggle2;
    }

    public boolean getMultiple() {
        return this.multiple;
    }

    public void setMultiple(boolean multiple2) {
        this.multiple = multiple2;
    }

    public boolean getRequired() {
        return this.required;
    }

    public void setRequired(boolean required2) {
        this.required = required2;
    }

    public void setProgrammaticChangeEvents(boolean programmaticChangeEvents2) {
        this.programmaticChangeEvents = programmaticChangeEvents2;
    }

    public String toString() {
        return this.selected.toString();
    }
}

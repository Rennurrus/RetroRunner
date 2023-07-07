package com.badlogic.gdx.graphics.g3d;

import com.badlogic.gdx.utils.Array;
import java.util.Iterator;

public class Material extends Attributes {
    private static int counter = 0;
    public String id;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Material() {
        /*
            r2 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "mtl"
            r0.append(r1)
            int r1 = counter
            int r1 = r1 + 1
            counter = r1
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            r2.<init>((java.lang.String) r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.badlogic.gdx.graphics.g3d.Material.<init>():void");
    }

    public Material(String id2) {
        this.id = id2;
    }

    public Material(Attribute... attributes) {
        this();
        set(attributes);
    }

    public Material(String id2, Attribute... attributes) {
        this(id2);
        set(attributes);
    }

    public Material(Array<Attribute> attributes) {
        this();
        set((Iterable<Attribute>) attributes);
    }

    public Material(String id2, Array<Attribute> attributes) {
        this(id2);
        set((Iterable<Attribute>) attributes);
    }

    public Material(Material copyFrom) {
        this(copyFrom.id, copyFrom);
    }

    public Material(String id2, Material copyFrom) {
        this(id2);
        Iterator<Attribute> it = copyFrom.iterator();
        while (it.hasNext()) {
            set(it.next().copy());
        }
    }

    public Material copy() {
        return new Material(this);
    }

    public int hashCode() {
        return super.hashCode() + (this.id.hashCode() * 3);
    }

    public boolean equals(Object other) {
        return (other instanceof Material) && (other == this || (((Material) other).id.equals(this.id) && super.equals(other)));
    }
}

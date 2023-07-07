package com.badlogic.gdx.graphics.g3d.attributes;

import com.badlogic.gdx.graphics.g3d.Attribute;

public class IntAttribute extends Attribute {
    public static final long CullFace = register(CullFaceAlias);
    public static final String CullFaceAlias = "cullface";
    public int value;

    public static IntAttribute createCullFace(int value2) {
        return new IntAttribute(CullFace, value2);
    }

    public IntAttribute(long type) {
        super(type);
    }

    public IntAttribute(long type, int value2) {
        super(type);
        this.value = value2;
    }

    public Attribute copy() {
        return new IntAttribute(this.type, this.value);
    }

    public int hashCode() {
        return (super.hashCode() * 983) + this.value;
    }

    public int compareTo(Attribute o) {
        if (this.type != o.type) {
            return (int) (this.type - o.type);
        }
        return this.value - ((IntAttribute) o).value;
    }
}

package com.badlogic.gdx.graphics.g3d.attributes;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.NumberUtils;

public class BlendingAttribute extends Attribute {
    public static final String Alias = "blended";
    public static final long Type = register(Alias);
    public boolean blended;
    public int destFunction;
    public float opacity;
    public int sourceFunction;

    public static final boolean is(long mask) {
        return (Type & mask) == mask;
    }

    public BlendingAttribute() {
        this((BlendingAttribute) null);
    }

    public BlendingAttribute(boolean blended2, int sourceFunc, int destFunc, float opacity2) {
        super(Type);
        this.opacity = 1.0f;
        this.blended = blended2;
        this.sourceFunction = sourceFunc;
        this.destFunction = destFunc;
        this.opacity = opacity2;
    }

    public BlendingAttribute(int sourceFunc, int destFunc, float opacity2) {
        this(true, sourceFunc, destFunc, opacity2);
    }

    public BlendingAttribute(int sourceFunc, int destFunc) {
        this(sourceFunc, destFunc, 1.0f);
    }

    public BlendingAttribute(boolean blended2, float opacity2) {
        this(blended2, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, opacity2);
    }

    public BlendingAttribute(float opacity2) {
        this(true, opacity2);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public BlendingAttribute(BlendingAttribute copyFrom) {
        this(copyFrom == null || copyFrom.blended, copyFrom == null ? GL20.GL_SRC_ALPHA : copyFrom.sourceFunction, copyFrom == null ? GL20.GL_ONE_MINUS_SRC_ALPHA : copyFrom.destFunction, copyFrom == null ? 1.0f : copyFrom.opacity);
    }

    public BlendingAttribute copy() {
        return new BlendingAttribute(this);
    }

    public int hashCode() {
        return (((((((super.hashCode() * 947) + (this.blended ? 1 : 0)) * 947) + this.sourceFunction) * 947) + this.destFunction) * 947) + NumberUtils.floatToRawIntBits(this.opacity);
    }

    public int compareTo(Attribute o) {
        if (this.type != o.type) {
            return (int) (this.type - o.type);
        }
        BlendingAttribute other = (BlendingAttribute) o;
        boolean z = this.blended;
        if (z != other.blended) {
            return z ? 1 : -1;
        }
        int i = this.sourceFunction;
        int i2 = other.sourceFunction;
        if (i != i2) {
            return i - i2;
        }
        int i3 = this.destFunction;
        int i4 = other.destFunction;
        if (i3 != i4) {
            return i3 - i4;
        }
        if (MathUtils.isEqual(this.opacity, other.opacity)) {
            return 0;
        }
        if (this.opacity < other.opacity) {
            return 1;
        }
        return -1;
    }
}

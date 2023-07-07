package com.badlogic.gdx.graphics.g3d.attributes;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.NumberUtils;

public class DepthTestAttribute extends Attribute {
    public static final String Alias = "depthStencil";
    protected static long Mask = Type;
    public static final long Type = register(Alias);
    public int depthFunc;
    public boolean depthMask;
    public float depthRangeFar;
    public float depthRangeNear;

    public static final boolean is(long mask) {
        return (Mask & mask) != 0;
    }

    public DepthTestAttribute() {
        this((int) GL20.GL_LEQUAL);
    }

    public DepthTestAttribute(boolean depthMask2) {
        this(GL20.GL_LEQUAL, depthMask2);
    }

    public DepthTestAttribute(int depthFunc2) {
        this(depthFunc2, true);
    }

    public DepthTestAttribute(int depthFunc2, boolean depthMask2) {
        this(depthFunc2, 0.0f, 1.0f, depthMask2);
    }

    public DepthTestAttribute(int depthFunc2, float depthRangeNear2, float depthRangeFar2) {
        this(depthFunc2, depthRangeNear2, depthRangeFar2, true);
    }

    public DepthTestAttribute(int depthFunc2, float depthRangeNear2, float depthRangeFar2, boolean depthMask2) {
        this(Type, depthFunc2, depthRangeNear2, depthRangeFar2, depthMask2);
    }

    public DepthTestAttribute(long type, int depthFunc2, float depthRangeNear2, float depthRangeFar2, boolean depthMask2) {
        super(type);
        if (is(type)) {
            this.depthFunc = depthFunc2;
            this.depthRangeNear = depthRangeNear2;
            this.depthRangeFar = depthRangeFar2;
            this.depthMask = depthMask2;
            return;
        }
        throw new GdxRuntimeException("Invalid type specified");
    }

    public DepthTestAttribute(DepthTestAttribute rhs) {
        this(rhs.type, rhs.depthFunc, rhs.depthRangeNear, rhs.depthRangeFar, rhs.depthMask);
    }

    public Attribute copy() {
        return new DepthTestAttribute(this);
    }

    public int hashCode() {
        return (((((((super.hashCode() * 971) + this.depthFunc) * 971) + NumberUtils.floatToRawIntBits(this.depthRangeNear)) * 971) + NumberUtils.floatToRawIntBits(this.depthRangeFar)) * 971) + (this.depthMask ? 1 : 0);
    }

    public int compareTo(Attribute o) {
        if (this.type != o.type) {
            return (int) (this.type - o.type);
        }
        DepthTestAttribute other = (DepthTestAttribute) o;
        int i = this.depthFunc;
        int i2 = other.depthFunc;
        if (i != i2) {
            return i - i2;
        }
        boolean z = this.depthMask;
        if (z != other.depthMask) {
            return z ? -1 : 1;
        }
        if (!MathUtils.isEqual(this.depthRangeNear, other.depthRangeNear)) {
            if (this.depthRangeNear < other.depthRangeNear) {
                return -1;
            }
            return 1;
        } else if (MathUtils.isEqual(this.depthRangeFar, other.depthRangeFar)) {
            return 0;
        } else {
            if (this.depthRangeFar < other.depthRangeFar) {
                return -1;
            }
            return 1;
        }
    }
}

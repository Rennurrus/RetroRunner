package com.badlogic.gdx.graphics;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public final class VertexAttribute {
    public String alias;
    public final boolean normalized;
    public final int numComponents;
    public int offset;
    public final int type;
    public int unit;
    public final int usage;
    private final int usageIndex;

    public VertexAttribute(int usage2, int numComponents2, String alias2) {
        this(usage2, numComponents2, alias2, 0);
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public VertexAttribute(int usage2, int numComponents2, String alias2, int unit2) {
        this(usage2, numComponents2, usage2 == 4 ? GL20.GL_UNSIGNED_BYTE : GL20.GL_FLOAT, usage2 == 4, alias2, unit2);
    }

    public VertexAttribute(int usage2, int numComponents2, int type2, boolean normalized2, String alias2) {
        this(usage2, numComponents2, type2, normalized2, alias2, 0);
    }

    public VertexAttribute(int usage2, int numComponents2, int type2, boolean normalized2, String alias2, int unit2) {
        this.usage = usage2;
        this.numComponents = numComponents2;
        this.type = type2;
        this.normalized = normalized2;
        this.alias = alias2;
        this.unit = unit2;
        this.usageIndex = Integer.numberOfTrailingZeros(usage2);
    }

    public VertexAttribute copy() {
        return new VertexAttribute(this.usage, this.numComponents, this.type, this.normalized, this.alias, this.unit);
    }

    public static VertexAttribute Position() {
        return new VertexAttribute(1, 3, ShaderProgram.POSITION_ATTRIBUTE);
    }

    public static VertexAttribute TexCoords(int unit2) {
        return new VertexAttribute(16, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + unit2, unit2);
    }

    public static VertexAttribute Normal() {
        return new VertexAttribute(8, 3, ShaderProgram.NORMAL_ATTRIBUTE);
    }

    public static VertexAttribute ColorPacked() {
        return new VertexAttribute(4, 4, GL20.GL_UNSIGNED_BYTE, true, ShaderProgram.COLOR_ATTRIBUTE);
    }

    public static VertexAttribute ColorUnpacked() {
        return new VertexAttribute(2, 4, GL20.GL_FLOAT, false, ShaderProgram.COLOR_ATTRIBUTE);
    }

    public static VertexAttribute Tangent() {
        return new VertexAttribute(128, 3, ShaderProgram.TANGENT_ATTRIBUTE);
    }

    public static VertexAttribute Binormal() {
        return new VertexAttribute(256, 3, ShaderProgram.BINORMAL_ATTRIBUTE);
    }

    public static VertexAttribute BoneWeight(int unit2) {
        return new VertexAttribute(64, 2, ShaderProgram.BONEWEIGHT_ATTRIBUTE + unit2, unit2);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof VertexAttribute)) {
            return false;
        }
        return equals((VertexAttribute) obj);
    }

    public boolean equals(VertexAttribute other) {
        return other != null && this.usage == other.usage && this.numComponents == other.numComponents && this.type == other.type && this.normalized == other.normalized && this.alias.equals(other.alias) && this.unit == other.unit;
    }

    public int getKey() {
        return (this.usageIndex << 8) + (this.unit & 255);
    }

    public int getSizeInBytes() {
        int i = this.type;
        if (i == 5126 || i == 5132) {
            return this.numComponents * 4;
        }
        switch (i) {
            case GL20.GL_BYTE:
            case GL20.GL_UNSIGNED_BYTE:
                return this.numComponents;
            case GL20.GL_SHORT:
            case GL20.GL_UNSIGNED_SHORT:
                return this.numComponents * 2;
            default:
                return 0;
        }
    }

    public int hashCode() {
        return (((getKey() * 541) + this.numComponents) * 541) + this.alias.hashCode();
    }
}

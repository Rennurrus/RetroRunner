package com.badlogic.gdx.graphics.g2d;

public class PolygonRegion {
    final TextureRegion region;
    final float[] textureCoords;
    final short[] triangles;
    final float[] vertices;

    public PolygonRegion(TextureRegion region2, float[] vertices2, short[] triangles2) {
        TextureRegion textureRegion = region2;
        float[] fArr = vertices2;
        this.region = textureRegion;
        this.vertices = fArr;
        this.triangles = triangles2;
        float[] textureCoords2 = new float[fArr.length];
        this.textureCoords = textureCoords2;
        float u = textureRegion.u;
        float v = textureRegion.v;
        float uvWidth = textureRegion.u2 - u;
        float uvHeight = textureRegion.v2 - v;
        int width = textureRegion.regionWidth;
        int height = textureRegion.regionHeight;
        int i = 0;
        int n = fArr.length;
        while (i < n) {
            textureCoords2[i] = ((fArr[i] / ((float) width)) * uvWidth) + u;
            textureCoords2[i + 1] = ((1.0f - (fArr[i + 1] / ((float) height))) * uvHeight) + v;
            i += 2;
        }
    }

    public float[] getVertices() {
        return this.vertices;
    }

    public short[] getTriangles() {
        return this.triangles;
    }

    public float[] getTextureCoords() {
        return this.textureCoords;
    }

    public TextureRegion getRegion() {
        return this.region;
    }
}

package com.badlogic.gdx.graphics.g2d;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class RepeatablePolygonSprite {
    private Color color = Color.WHITE;
    private int cols;
    private float density;
    private boolean dirty = true;
    private float gridHeight;
    private float gridWidth;
    private Array<short[]> indices = new Array<>();
    private Vector2 offset = new Vector2();
    private Array<float[]> parts = new Array<>();
    private TextureRegion region;
    private int rows;
    private Array<float[]> vertices = new Array<>();
    public float x = 0.0f;
    public float y = 0.0f;

    public void setPolygon(TextureRegion region2, float[] vertices2) {
        setPolygon(region2, vertices2, -1.0f);
    }

    public void setPolygon(TextureRegion region2, float[] vertices2, float density2) {
        this.region = region2;
        float[] vertices3 = offset(vertices2);
        Polygon polygon = new Polygon(vertices3);
        Polygon tmpPoly = new Polygon();
        Polygon intersectionPoly = new Polygon();
        EarClippingTriangulator triangulator = new EarClippingTriangulator();
        Rectangle boundRect = polygon.getBoundingRectangle();
        float density3 = density2 == -1.0f ? boundRect.getWidth() / ((float) region2.getRegionWidth()) : density2;
        float regionAspectRatio = ((float) region2.getRegionHeight()) / ((float) region2.getRegionWidth());
        this.cols = (int) Math.ceil((double) density3);
        this.gridWidth = boundRect.getWidth() / density3;
        this.gridHeight = this.gridWidth * regionAspectRatio;
        this.rows = (int) Math.ceil((double) (boundRect.getHeight() / this.gridHeight));
        int col = 0;
        while (col < this.cols) {
            int row = 0;
            while (row < this.rows) {
                float[] verts = new float[8];
                int idx = 0 + 1;
                float f = this.gridWidth;
                verts[0] = ((float) col) * f;
                int idx2 = idx + 1;
                float[] vertices4 = vertices3;
                float f2 = this.gridHeight;
                verts[idx] = ((float) row) * f2;
                int idx3 = idx2 + 1;
                verts[idx2] = ((float) col) * f;
                int idx4 = idx3 + 1;
                verts[idx3] = ((float) (row + 1)) * f2;
                int idx5 = idx4 + 1;
                verts[idx4] = ((float) (col + 1)) * f;
                int idx6 = idx5 + 1;
                verts[idx5] = ((float) (row + 1)) * f2;
                verts[idx6] = ((float) (col + 1)) * f;
                verts[idx6 + 1] = ((float) row) * f2;
                tmpPoly.setVertices(verts);
                Intersector.intersectPolygons(polygon, tmpPoly, intersectionPoly);
                float[] verts2 = intersectionPoly.getVertices();
                if (verts2.length > 0) {
                    this.parts.add(snapToGrid(verts2));
                    this.indices.add(triangulator.computeTriangles(verts2).toArray());
                } else {
                    this.parts.add(null);
                }
                row++;
                TextureRegion textureRegion = region2;
                vertices3 = vertices4;
            }
            col++;
            TextureRegion textureRegion2 = region2;
        }
        buildVertices();
    }

    private float[] snapToGrid(float[] vertices2) {
        for (int i = 0; i < vertices2.length; i += 2) {
            float numX = (vertices2[i] / this.gridWidth) % 1.0f;
            float numY = (vertices2[i + 1] / this.gridHeight) % 1.0f;
            if (numX > 0.99f || numX < 0.01f) {
                float f = this.gridWidth;
                vertices2[i] = f * ((float) Math.round(vertices2[i] / f));
            }
            if (numY > 0.99f || numY < 0.01f) {
                float f2 = this.gridHeight;
                vertices2[i + 1] = f2 * ((float) Math.round(vertices2[i + 1] / f2));
            }
        }
        return vertices2;
    }

    private float[] offset(float[] vertices2) {
        this.offset.set(vertices2[0], vertices2[1]);
        for (int i = 0; i < vertices2.length - 1; i += 2) {
            if (this.offset.x > vertices2[i]) {
                this.offset.x = vertices2[i];
            }
            if (this.offset.y > vertices2[i + 1]) {
                this.offset.y = vertices2[i + 1];
            }
        }
        for (int i2 = 0; i2 < vertices2.length; i2 += 2) {
            vertices2[i2] = vertices2[i2] - this.offset.x;
            int i3 = i2 + 1;
            vertices2[i3] = vertices2[i3] - this.offset.y;
        }
        return vertices2;
    }

    private void buildVertices() {
        this.vertices.clear();
        for (int i = 0; i < this.parts.size; i++) {
            float[] verts = this.parts.get(i);
            if (verts != null) {
                float[] fullVerts = new float[((verts.length * 5) / 2)];
                int idx = 0;
                int i2 = this.rows;
                int col = i / i2;
                int row = i % i2;
                int j = 0;
                while (j < verts.length) {
                    int idx2 = idx + 1;
                    fullVerts[idx] = verts[j] + this.offset.x + this.x;
                    int idx3 = idx2 + 1;
                    fullVerts[idx2] = verts[j + 1] + this.offset.y + this.y;
                    int idx4 = idx3 + 1;
                    fullVerts[idx3] = this.color.toFloatBits();
                    float f = verts[j];
                    float f2 = this.gridWidth;
                    float u = (f % f2) / f2;
                    float f3 = verts[j + 1];
                    float f4 = this.gridHeight;
                    float v = (f3 % f4) / f4;
                    if (verts[j] == ((float) col) * f2) {
                        u = 0.0f;
                    }
                    if (verts[j] == ((float) (col + 1)) * this.gridWidth) {
                        u = 1.0f;
                    }
                    if (verts[j + 1] == ((float) row) * this.gridHeight) {
                        v = 0.0f;
                    }
                    if (verts[j + 1] == ((float) (row + 1)) * this.gridHeight) {
                        v = 1.0f;
                    }
                    int idx5 = idx4 + 1;
                    fullVerts[idx4] = this.region.getU() + ((this.region.getU2() - this.region.getU()) * u);
                    fullVerts[idx5] = this.region.getV() + ((this.region.getV2() - this.region.getV()) * v);
                    j += 2;
                    idx = idx5 + 1;
                }
                this.vertices.add(fullVerts);
            }
        }
        this.dirty = false;
    }

    public void draw(PolygonSpriteBatch batch) {
        if (this.dirty) {
            buildVertices();
        }
        for (int i = 0; i < this.vertices.size; i++) {
            batch.draw(this.region.getTexture(), this.vertices.get(i), 0, this.vertices.get(i).length, this.indices.get(i), 0, this.indices.get(i).length);
        }
    }

    public void setColor(Color color2) {
        this.color = color2;
        this.dirty = true;
    }

    public void setPosition(float x2, float y2) {
        this.x = x2;
        this.y = y2;
        this.dirty = true;
    }
}

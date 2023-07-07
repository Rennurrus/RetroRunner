package com.badlogic.gdx.graphics.g3d.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class PointLight extends BaseLight<PointLight> {
    public float intensity;
    public final Vector3 position = new Vector3();

    public PointLight setPosition(float positionX, float positionY, float positionZ) {
        this.position.set(positionX, positionY, positionZ);
        return this;
    }

    public PointLight setPosition(Vector3 position2) {
        this.position.set(position2);
        return this;
    }

    public PointLight setIntensity(float intensity2) {
        this.intensity = intensity2;
        return this;
    }

    public PointLight set(PointLight copyFrom) {
        return set(copyFrom.color, copyFrom.position, copyFrom.intensity);
    }

    public PointLight set(Color color, Vector3 position2, float intensity2) {
        if (color != null) {
            this.color.set(color);
        }
        if (position2 != null) {
            this.position.set(position2);
        }
        this.intensity = intensity2;
        return this;
    }

    public PointLight set(float r, float g, float b, Vector3 position2, float intensity2) {
        this.color.set(r, g, b, 1.0f);
        if (position2 != null) {
            this.position.set(position2);
        }
        this.intensity = intensity2;
        return this;
    }

    public PointLight set(Color color, float x, float y, float z, float intensity2) {
        if (color != null) {
            this.color.set(color);
        }
        this.position.set(x, y, z);
        this.intensity = intensity2;
        return this;
    }

    public PointLight set(float r, float g, float b, float x, float y, float z, float intensity2) {
        this.color.set(r, g, b, 1.0f);
        this.position.set(x, y, z);
        this.intensity = intensity2;
        return this;
    }

    public boolean equals(Object obj) {
        return (obj instanceof PointLight) && equals((PointLight) obj);
    }

    public boolean equals(PointLight other) {
        return other != null && (other == this || (this.color.equals(other.color) && this.position.equals(other.position) && this.intensity == other.intensity));
    }
}

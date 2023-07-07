package com.badlogic.gdx.graphics.g3d.environment;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class SpotLight extends BaseLight<SpotLight> {
    public float cutoffAngle;
    public final Vector3 direction = new Vector3();
    public float exponent;
    public float intensity;
    public final Vector3 position = new Vector3();

    public SpotLight setPosition(float positionX, float positionY, float positionZ) {
        this.position.set(positionX, positionY, positionZ);
        return this;
    }

    public SpotLight setPosition(Vector3 position2) {
        this.position.set(position2);
        return this;
    }

    public SpotLight setDirection(float directionX, float directionY, float directionZ) {
        this.direction.set(directionX, directionY, directionZ);
        return this;
    }

    public SpotLight setDirection(Vector3 direction2) {
        this.direction.set(direction2);
        return this;
    }

    public SpotLight setIntensity(float intensity2) {
        this.intensity = intensity2;
        return this;
    }

    public SpotLight setCutoffAngle(float cutoffAngle2) {
        this.cutoffAngle = cutoffAngle2;
        return this;
    }

    public SpotLight setExponent(float exponent2) {
        this.exponent = exponent2;
        return this;
    }

    public SpotLight set(SpotLight copyFrom) {
        return set(copyFrom.color, copyFrom.position, copyFrom.direction, copyFrom.intensity, copyFrom.cutoffAngle, copyFrom.exponent);
    }

    public SpotLight set(Color color, Vector3 position2, Vector3 direction2, float intensity2, float cutoffAngle2, float exponent2) {
        if (color != null) {
            this.color.set(color);
        }
        if (position2 != null) {
            this.position.set(position2);
        }
        if (direction2 != null) {
            this.direction.set(direction2).nor();
        }
        this.intensity = intensity2;
        this.cutoffAngle = cutoffAngle2;
        this.exponent = exponent2;
        return this;
    }

    public SpotLight set(float r, float g, float b, Vector3 position2, Vector3 direction2, float intensity2, float cutoffAngle2, float exponent2) {
        this.color.set(r, g, b, 1.0f);
        if (position2 != null) {
            this.position.set(position2);
        }
        if (direction2 != null) {
            this.direction.set(direction2).nor();
        }
        this.intensity = intensity2;
        this.cutoffAngle = cutoffAngle2;
        this.exponent = exponent2;
        return this;
    }

    public SpotLight set(Color color, float posX, float posY, float posZ, float dirX, float dirY, float dirZ, float intensity2, float cutoffAngle2, float exponent2) {
        if (color != null) {
            this.color.set(color);
        }
        this.position.set(posX, posY, posZ);
        this.direction.set(dirX, dirY, dirZ).nor();
        this.intensity = intensity2;
        this.cutoffAngle = cutoffAngle2;
        this.exponent = exponent2;
        return this;
    }

    public SpotLight set(float r, float g, float b, float posX, float posY, float posZ, float dirX, float dirY, float dirZ, float intensity2, float cutoffAngle2, float exponent2) {
        this.color.set(r, g, b, 1.0f);
        this.position.set(posX, posY, posZ);
        this.direction.set(dirX, dirY, dirZ).nor();
        this.intensity = intensity2;
        this.cutoffAngle = cutoffAngle2;
        this.exponent = exponent2;
        return this;
    }

    public SpotLight setTarget(Vector3 target) {
        this.direction.set(target).sub(this.position).nor();
        return this;
    }

    public boolean equals(Object obj) {
        return (obj instanceof SpotLight) && equals((SpotLight) obj);
    }

    public boolean equals(SpotLight other) {
        return other != null && (other == this || (this.color.equals(other.color) && this.position.equals(other.position) && this.direction.equals(other.direction) && MathUtils.isEqual(this.intensity, other.intensity) && MathUtils.isEqual(this.cutoffAngle, other.cutoffAngle) && MathUtils.isEqual(this.exponent, other.exponent)));
    }
}

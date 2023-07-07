package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class RangedNumericValue extends ParticleValue {
    private float lowMax;
    private float lowMin;

    public float newLowValue() {
        float f = this.lowMin;
        return f + ((this.lowMax - f) * MathUtils.random());
    }

    public void setLow(float value) {
        this.lowMin = value;
        this.lowMax = value;
    }

    public void setLow(float min, float max) {
        this.lowMin = min;
        this.lowMax = max;
    }

    public float getLowMin() {
        return this.lowMin;
    }

    public void setLowMin(float lowMin2) {
        this.lowMin = lowMin2;
    }

    public float getLowMax() {
        return this.lowMax;
    }

    public void setLowMax(float lowMax2) {
        this.lowMax = lowMax2;
    }

    public void load(RangedNumericValue value) {
        super.load(value);
        this.lowMax = value.lowMax;
        this.lowMin = value.lowMin;
    }

    public void write(Json json) {
        super.write(json);
        json.writeValue("lowMin", (Object) Float.valueOf(this.lowMin));
        json.writeValue("lowMax", (Object) Float.valueOf(this.lowMax));
    }

    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        this.lowMin = ((Float) json.readValue("lowMin", Float.TYPE, jsonData)).floatValue();
        this.lowMax = ((Float) json.readValue("lowMax", Float.TYPE, jsonData)).floatValue();
    }
}

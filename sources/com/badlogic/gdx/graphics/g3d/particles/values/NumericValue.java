package com.badlogic.gdx.graphics.g3d.particles.values;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class NumericValue extends ParticleValue {
    private float value;

    public float getValue() {
        return this.value;
    }

    public void setValue(float value2) {
        this.value = value2;
    }

    public void load(NumericValue value2) {
        super.load(value2);
        this.value = value2.value;
    }

    public void write(Json json) {
        super.write(json);
        json.writeValue("value", (Object) Float.valueOf(this.value));
    }

    public void read(Json json, JsonValue jsonData) {
        super.read(json, jsonData);
        this.value = ((Float) json.readValue("value", Float.TYPE, jsonData)).floatValue();
    }
}

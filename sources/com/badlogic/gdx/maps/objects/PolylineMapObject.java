package com.badlogic.gdx.maps.objects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Polyline;

public class PolylineMapObject extends MapObject {
    private Polyline polyline;

    public Polyline getPolyline() {
        return this.polyline;
    }

    public void setPolyline(Polyline polyline2) {
        this.polyline = polyline2;
    }

    public PolylineMapObject() {
        this(new float[0]);
    }

    public PolylineMapObject(float[] vertices) {
        this.polyline = new Polyline(vertices);
    }

    public PolylineMapObject(Polyline polyline2) {
        this.polyline = polyline2;
    }
}

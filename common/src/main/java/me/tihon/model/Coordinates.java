package me.tihon.model;

import java.io.Serializable;

public class Coordinates implements Serializable {

    private float x;
    private Integer y;

    public Coordinates(float x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Coordinates{x=" + x + ", y=" + y + "}";
    }

}

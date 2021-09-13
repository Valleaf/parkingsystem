package com.parkit.parkingsystem.constants;

public class Fare {

    private Fare() {
        throw new IllegalStateException("Not meant to be instantiated");
    }

    public static final double BIKE_RATE_PER_HOUR = 1.0;
    public static final double CAR_RATE_PER_HOUR = 1.5;
}

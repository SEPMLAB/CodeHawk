package org.codehawk.smell;

public class ThresholdDTO {
    private int unnecessaryAbstractionFewMethods = 0;
    private int unnecessaryAbstractionFewFields = 5;

    public int getUnnecessaryAbstractionFewFields() {
        return unnecessaryAbstractionFewFields;
    }

    public int getUnnecessaryAbstractionFewMethods() {
        return unnecessaryAbstractionFewMethods;
    }
}
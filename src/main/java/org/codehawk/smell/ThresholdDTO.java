package org.codehawk.smell;

public class ThresholdDTO {
    private int unnecessaryAbstractionFewMethods = 0;
    private int unnecessaryAbstractionFewFields = 5;
    private int insufficientModularizationLargePublicInterface = 20;
	private int insufficientModularizationLargeNumOfMethods = 30;
    private int insufficientModularizationHighComplexity = 100;

    public int getUnnecessaryAbstractionFewFields() {
        return unnecessaryAbstractionFewFields;
    }

    public int getUnnecessaryAbstractionFewMethods() {
        return unnecessaryAbstractionFewMethods;
    }

    public int getInsufficientModularizationHighComplexity() {
        return insufficientModularizationHighComplexity;
    }

    public int getInsufficientModularizationLargeNumOfMethods() {
        return insufficientModularizationLargeNumOfMethods;
    }

    public int getInsufficientModularizationLargePublicInterface() {
        return insufficientModularizationLargePublicInterface;
    }
}
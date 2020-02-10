package org.codehawk.smell.smellruler;

public class DataClump implements Smell {
	private static final Smell.Type smellType = Smell.Type.DATACLUMP;

	@Override
	public boolean is(Type type) {
		return smellType == type;
	}

	@Override
	public Type type() {
		return smellType;
	}

	@Override
	public String smellDetail() {
		return "Avoid data clumps for...";
	}

}

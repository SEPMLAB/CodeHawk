package org.codehawk.smell.smellruler;

public class LargeClass implements Smell {
	private static final Smell.Type smellType = Smell.Type.LARGECLASS;

	@Override
	public boolean is(Type type) {
		;
		return smellType == type;
	}

	@Override
	public Type type() {
		return smellType;
	}

	@Override
	public String smellDetail() {
		return "Avoid large classes!";
	}
}

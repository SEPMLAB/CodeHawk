package org.codehawk.smell.smellruler;

public class LazyClass implements Smell{
	private static final Smell.Type smellType = Smell.Type.LAZYCLASS;

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
		return "Avoid lazy classes!";
	}
}

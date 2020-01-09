package org.smell.smellruler;

import org.smell.smellruler.Smell.Type;

public class LargeMethod implements Smell{
	private static final Smell.Type smellType = Smell.Type.LARGEMETHOD;

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
		return "Avoid large methods!";
	}
}
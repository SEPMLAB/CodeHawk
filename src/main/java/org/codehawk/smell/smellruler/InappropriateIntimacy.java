package org.codehawk.smell.smellruler;

public class InappropriateIntimacy implements Smell {
	private static final Smell.Type smellType = Smell.Type.INAPPROPRIATE_INTIMACY;

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
		return "this class has Inappropriate Intimacy with class asds";
	}
}

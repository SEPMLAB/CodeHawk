package org.codehawk.smell.smellruler;

public class PrimitiveObsession implements Smell{
	private static final Smell.Type smellType = Smell.Type.PRIMITIVE_OBSESSION;
	
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
		return "Using too many primitive type prameters";
	}

}

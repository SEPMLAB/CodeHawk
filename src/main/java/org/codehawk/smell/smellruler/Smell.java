package org.codehawk.smell.smellruler;

public interface Smell {
	public boolean is(Smell.Type type);
	public Smell.Type type();
	
	public String smellDetail();
	enum Type{
		DATACLUMP,
		LARGECLASS,
		LARGEMETHOD,
		LAZYCLASS,
		TOOMANYCASESINONESWITCH,
		TOOMANYPARAMETERS;
	}
}

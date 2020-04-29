package org.codehawk.smell.modler;

public class POMethodNode extends SmellableNode{

	private int[] primitiveRatio = {};
	
	public void setRatio(int[] ratio) {
		primitiveRatio = ratio;
	}
	
	public int[] getRatio() {
		return primitiveRatio;
	}
	
	@Override
	public Kind kind() {
		return Node.Kind.METHOD;
	}

	@Override
	public String getName() {
		return "Smellable method for primitive obsession detecting";
	}

}

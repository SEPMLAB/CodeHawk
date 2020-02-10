package org.codehawk.smell.modler;

public class SwitchNode extends SmellableNode{
	
	public SwitchNode(int line) {
		this.startLine = line;
	}
	
	@Override
	public Kind kind() {
		return Node.Kind.SWITCHCASE;
	}

	@Override
	public String getName() {
		return "smellable switch case statement";
	}

}

package org.codehawk.smell.modler;

public class ImperativeAbstractionNode extends SmellableNode {
	String name;

	public ImperativeAbstractionNode(int line, String className) {
		this.startLine = line;
		this.name = className;
	}

	@Override
	public Kind kind() {
		return Node.Kind.CLASS;
	}

	@Override
	public String getName() {
		return name;
	}
}

package org.codehawk.smell.modler;

import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.plugins.java.api.JavaFileScannerContext;

public class DeficientEncapsulationNode extends SmellableNode {
	String name;
	JavaFileScannerContext context;
	JavaCheck check;

	public DeficientEncapsulationNode(int line, String className, JavaFileScannerContext context, JavaCheck check) {
		this.startLine = line;
		this.name = className;
		this.context = context;
		this.check = check;
	}

	@Override
	public Kind kind() {
		return Node.Kind.CLASS;
	}

	@Override
	public String getName() {
		return name;
	}

	public JavaFileScannerContext getContext() {
		return context;
	}

	public JavaCheck getCheck() {
		return check;
	}
}

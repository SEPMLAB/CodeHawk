package org.smell.astparser;

import org.smell.astmodeler.ClassNode;

public abstract class VariableParser implements ASTTreeParser{
	protected ClassNode ownerClass;
	
	protected boolean notNull(Object object) {
		return object != null;	
	}
}	
package org.smell.astparser;

import org.smell.astmodeler.ClassNode;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;

abstract class MethodParser  implements ASTTreeParser{
	protected ClassNode ownerClass;

	protected boolean notNull(Object object) {
		return object != null;	
	}
	
	protected boolean methodNameBeginWithGetOrSet(String methodName) {
		String getterRegex = "^get.+";
		String setterRegex = "^set.+";
		return methodName.matches(getterRegex) || methodName.matches(setterRegex);
	}
	
	protected ExpressionTree getMethodSelect(ExpressionTree expressionTree) {
		ExpressionTree  methodInvocation= ((MethodInvocationTree) expressionTree).methodSelect();	
		if(notNull(methodInvocation)) {
			return methodInvocation;
		}
		return null;
	}
}
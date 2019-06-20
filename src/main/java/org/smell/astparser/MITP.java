package org.smell.astparser;

import org.smell.astmodeler.ClassNode;
import org.smell.metricruler.Laa;
import org.smell.metricruler.LaaData;
import org.smell.metricruler.Metric;
import org.sonar.plugins.java.api.semantic.Symbol;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.Tree;

/*
 *  Method Invocation Tree Processor 
 *  處理method invocation tree
 */
public class MITP extends MethodParser{
	private ClassNode ownerClass;
	
	public MITP(ClassNode node) {
		this.ownerClass = node;
	}
	
	private void callMSETParser(Metric metric , ExpressionTree expressionTree) {			
		ASTTreeParser msetp = new MSETP(this.ownerClass);
		msetp.parse(metric,expressionTree);
	}

	@Override
	public void parse(Metric metric , ExpressionTree expressionTree) {
		// 需要提供正確的binary檔才能取出對應的ExpressionTree
		// 如果binary檔不正確而且ExpressionTree中的type又是自己定義的類別的話 取出來就都會是!unknown!
		// 其他公用API則可以取出對應的type		
		if (isMethodInvocationTree(expressionTree) && metric.is( Metric.Type.ATFD) ) {		
			if (isMemberSelectExpressionTree(getMethodSelect(expressionTree))) {				
				callMSETParser(metric,expressionTree);		
			}
		}else if(isMethodInvocationTree(expressionTree) && metric.is(Metric.Type.LAA) ) {
			if (isMemberSelectExpressionTree(getMethodSelect(expressionTree))) {				
				callMSETParser(metric,expressionTree);
			}else if(notNull(getMethodSelect(expressionTree)) ) {
				//getXXX();	
				//可以從symbol的owner判斷是否為local attribute accessed
				//不會出現atfd的情形 只需要判斷是否為local attribute accessed
				detectLAA(metric,expressionTree);			
			}
		}else if(isMethodInvocationTree(expressionTree) && metric.is(Metric.Type.FDP)) {					
			ExpressionTree methodInvocation = ((MethodInvocationTree) expressionTree).methodSelect();			
			if (isMemberSelectExpressionTree(methodInvocation)) {				
				callMSETParser(metric,expressionTree);
			}					
		}	
	}
	
	private boolean isMethodInvocationTree(ExpressionTree expressionTree) {
		return notNull(expressionTree) && expressionTree.is(Tree.Kind.METHOD_INVOCATION);
	}

	private boolean isMemberSelectExpressionTree(ExpressionTree expressionTree) {
		return notNull(expressionTree) && expressionTree.is(Tree.Kind.MEMBER_SELECT);
	}
	
	private void detectLAA(Metric metric, ExpressionTree expressionTree) {
		ExpressionTree methodInvocation = ((MethodInvocationTree) expressionTree).methodSelect();
		Symbol symbol = ((MethodInvocationTree) expressionTree).symbol();
		String owner = symbol.owner().toString();
		String methodName = methodInvocation.toString();
		if(owner.equals(ownerClass.getName())  && methodNameBeginWithGetOrSet(methodName)) {
			LaaData laaData =   (LaaData)((Laa)metric).getData();
			int localAttributesAccessed = laaData.getLocalAttributesAccessed() + 1;
			laaData.setLocalAttributesAccessed(localAttributesAccessed);
			//local data accessed
		}			
	}	
}
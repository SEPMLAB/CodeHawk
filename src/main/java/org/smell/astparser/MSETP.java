package org.smell.astparser;

import java.util.List;

import org.smell.astmodeler.ClassNode;
import org.smell.metricruler.Atfd;
import org.smell.metricruler.Fdp;
import org.smell.metricruler.Laa;
import org.smell.metricruler.LaaData;
import org.smell.metricruler.Metric;
import org.sonar.plugins.java.api.semantic.Type;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MemberSelectExpressionTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.samples.java.checks.BrokenModularizationRule;

/*
 * MemberSelectExpressionTree processor
 */

public class MSETP extends MethodParser{
	private ClassNode ownerClass;

	public MSETP(ClassNode node) {
		this.ownerClass = node;
	}

	private Type getExpressionTreeType(ExpressionTree expressionTree) {
		if(expressionTree.is(Tree.Kind.MEMBER_SELECT)) {
			ExpressionTree  expression= ((MemberSelectExpressionTree) expressionTree).expression();
			IdentifierTree  identifier= ((MemberSelectExpressionTree) expressionTree).identifier();									
			if (notNull(expression) && notNull(identifier)) {
				Type  variableType = expression.symbolType();	
				return variableType;
			}
		}
		return null;
	}
	
	private boolean isLocalData(ExpressionTree expressionTree) {
		Type variableType = getExpressionTreeType(expressionTree);		
		String variableTypeName = variableType.toString();	
		return (variableTypeName.equals(this.ownerClass.getName()) ) ;					
	}
	//TODO 不同package但是同名的類別可能會搞混
	private boolean isForeignData(ExpressionTree expressionTree) {
		List<ClassNode> classes =BrokenModularizationRule.getClasses();
		Type variableType = getExpressionTreeType(expressionTree);			
		String variableTypeName = variableType.toString();			
		if( !variableTypeName.equals(this.ownerClass.getName())) {
			for(ClassNode n :classes) {
				String className = n.getName();
				if(variableTypeName.equals(className)  ) {
					return true;
				}
			}			
		}
		return false;
	}
		
	@Override
	public void parse(Metric metric , ExpressionTree expressionTree) {
		if (notNull(expressionTree) && expressionTree.is(Tree.Kind.MEMBER_SELECT)) {
			parseMemberSelectExpressionTree(metric, expressionTree);
		}else if(notNull(expressionTree) && expressionTree.is(Tree.Kind.METHOD_INVOCATION)) {
			parseMethodInvocationTree(metric,expressionTree);				
		}
	}
	private void parseMemberSelectExpressionTree(Metric metric , ExpressionTree expressionTree) {
		if ( metric.is( Metric.Type.ATFD) &&  isForeignData(expressionTree)) {	
			atfdDetected(metric);		
		}else if(metric.is(Metric.Type.LAA) ) {
			if(isForeignData(expressionTree)) {			
				laaForeignAttributeDetected(metric);
			}else if(isLocalData(expressionTree)) {		
				laaLLocalAttributeDetected(metric);
			}
		}else if(metric.is(Metric.Type.FDP)  &&  isForeignData(expressionTree)) {			
			fdpDetected(metric,expressionTree);		
		}
	}
	
	private void laaForeignAttributeDetected(Metric metric) {
		//foreign data accessed detected 
		LaaData laaData =   (LaaData)((Laa)metric).getData();
		int foreignAttributesAccessed = laaData.getForeignAttributesAccessed() + 1;
		laaData.setForeignAttributesAccessed(foreignAttributesAccessed);
	}
	
	private void laaLLocalAttributeDetected(Metric metric) {
		//local data accessed detected 				
		LaaData laaData =   (LaaData)((Laa)metric).getData();
		int localAttributesAccessed = laaData.getLocalAttributesAccessed() + 1;
		laaData.setLocalAttributesAccessed(localAttributesAccessed);		
	}
	
	private boolean methodisGetterorSetter(ExpressionTree methodInvocation) {
		IdentifierTree methodAccessed = ((MemberSelectExpressionTree) methodInvocation).identifier();
		return (notNull(methodAccessed) && methodNameBeginWithGetOrSet(methodAccessed.name()));
	}
	
	private void atfdDetected(Metric metric) {
		Atfd atfd = (Atfd)metric;
		atfd.setValue(atfd.getValue()+1);		
	}
	
	private void parseMethodInvocationTree(Metric metric , ExpressionTree expressionTree) {
		ExpressionTree methodInvocation = getMethodSelect(expressionTree);
		if ( metric.is( Metric.Type.ATFD) && methodisGetterorSetter(methodInvocation)) {				
			if (isForeignData(methodInvocation)) {	
				atfdDetected(metric);											
			}		
		}else if(metric.is(Metric.Type.LAA) && methodisGetterorSetter(methodInvocation)) {			
			if (isForeignData(methodInvocation)) {		
				laaForeignAttributeDetected(metric);		
			}else if (isLocalData(methodInvocation)) {		
				laaLLocalAttributeDetected(metric);
			}				
		}else if(metric.is(Metric.Type.FDP) && methodisGetterorSetter(methodInvocation)) {
			if (isForeignData(methodInvocation)) {
				fdpDetected(metric,methodInvocation);		
			}					
		}
	}
	
	private void fdpDetected(Metric metric, ExpressionTree expressionTree) {
		ExpressionTree  expression= ((MemberSelectExpressionTree) expressionTree).expression();
		Type  foreignDataVariableType = expression.symbolType();			
		String foreignDataProvider = foreignDataVariableType.toString();		
		//foreign data accessed detected 			
		((Fdp)metric).addForeignDataProvider(foreignDataProvider);			
	}
}
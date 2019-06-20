package org.smell.astparser;

import org.smell.astmodeler.ClassNode;
import org.smell.metricruler.Metric;
import org.sonar.plugins.java.api.tree.AssignmentExpressionTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.Tree;

/* 
 *  AssignmentExpressionTree processor
 */
//判斷方法:
//ex: RightControlPanel b221= new RightControlPanel(c2);
//b222 = b221.test1;
//比較b221的類別型態跟呼叫"b222 = b221.test1;" 這段code的方法是不是屬於同一個類別

public class ATP extends ExpressionParser{
	private ClassNode ownerClass;
	
	public ATP(ClassNode node) {
		this.ownerClass = node;
	}

	private void callLowerParser(Metric metric, ExpressionTree expressionTree)  {
		ExpressionTree assignment = ((AssignmentExpressionTree) expressionTree).expression();
		ExpressionTree variable = ((AssignmentExpressionTree) expressionTree).variable();
		//先依據variable的格式 分析variable的型別  再分析assignment
		//variable的格式: a ,  A.a ,  a[][]...通通都是某種ExpressionTree
		//if varibale is arrayAccessExpressionTree or MSET
		if(variable.is(Tree.Kind.MEMBER_SELECT)) {		
			parseATPExpressionTree(metric,variable);
			parseATPExpressionTree(metric,assignment);
			
		}else if(variable.is(Tree.Kind.METHOD_INVOCATION) ){
			//TODO
			//複雜的expressiontree 可能會包含method invocation tree
			//Not implemented yet
		}else if(variable.is(Tree.Kind.ARRAY_ACCESS_EXPRESSION) ){
			//TODO
			//Not implemented yet
		}else if(variable.is(Tree.Kind.CONDITIONAL_EXPRESSION) ){
			//ConditionalExpressionTree
			//Not implemented yet
		}else if(isUnaryExpressionTree(variable)  ){	
			//Not implemented yet
			//UnaryExpressionTree
		}else if(isBinaryExpressionTree(variable) ) {
			//BinaryExpressionTree
			//Not implemented yet
		}else if(variable.is(Tree.Kind.IDENTIFIER) ){		
			//IdentifierTree
			//parse assignment						
			//TODO
			//確定操作其他類形的區域變數算不算atfd
			//detect local data access not implement yet
			//Ex: B b = new B();
			// b= xxx;
			parseATPExpressionTree(metric,assignment);
		}else {
			parseATPExpressionTree(metric,assignment);
		}
	}
	
	private void parseATPExpressionTree(Metric metric,ExpressionTree expressionTree) {
		if (expressionTree.is(Tree.Kind.METHOD_INVOCATION)) {
			ASTTreeParser mitp = new MITP(this.ownerClass);
			mitp.parse(metric,expressionTree);			
		} else if (expressionTree.is(Tree.Kind.MEMBER_SELECT)) {
			ASTTreeParser msetp = new MSETP(this.ownerClass);
			msetp.parse(metric,expressionTree);		
		}
	}
	
	private boolean isBinaryExpressionTree(ExpressionTree variable) {
		return variable.is(Tree.Kind.AND) ||
			   variable.is(Tree.Kind.CONDITIONAL_AND) ||
			   variable.is(Tree.Kind.CONDITIONAL_OR) ||
				   variable.is(Tree.Kind.DIVIDE) ||
				   variable.is(Tree.Kind.EQUAL_TO) ||
				   variable.is(Tree.Kind.GREATER_THAN) ||
				   variable.is(Tree.Kind.GREATER_THAN_OR_EQUAL_TO) ||
				   variable.is(Tree.Kind.LEFT_SHIFT) ||
				   variable.is(Tree.Kind.LESS_THAN) ||
				   variable.is(Tree.Kind.LESS_THAN_OR_EQUAL_TO) ||
				   variable.is(Tree.Kind.MINUS) ||
				   variable.is(Tree.Kind.MULTIPLY) ||
				   variable.is(Tree.Kind.NOT_EQUAL_TO)||
				   variable.is(Tree.Kind.OR) ||
				   variable.is(Tree.Kind.PLUS) ||
				   variable.is(Tree.Kind.REMAINDER) ||				   
				   variable.is(Tree.Kind.RIGHT_SHIFT) ||
				   variable.is(Tree.Kind.UNSIGNED_RIGHT_SHIFT) ||
				   variable.is(Tree.Kind.UNSIGNED_RIGHT_SHIFT) || 				   
				   variable.is(Tree.Kind.XOR) 
				   ;
	}

	private boolean isUnaryExpressionTree(ExpressionTree variable) {
		return variable.is(Tree.Kind.PREFIX_INCREMENT) ||
			   variable.is(Tree.Kind.PREFIX_DECREMENT) ||
			   variable.is(Tree.Kind.POSTFIX_INCREMENT) ||
			   variable.is(Tree.Kind.POSTFIX_DECREMENT) ||
			   variable.is(Tree.Kind.LOGICAL_COMPLEMENT) ||
			   variable.is(Tree.Kind.BITWISE_COMPLEMENT) ||
			   variable.is(Tree.Kind.UNARY_PLUS) ||
			   variable.is(Tree.Kind.UNARY_MINUS) ;
	}

	@Override
	public void parse(Metric metric , ExpressionTree expressionTree) {
		if (isAssignmentTree(expressionTree) && isFeatureEnvyMetrics(metric) ) {	
			callLowerParser(metric,expressionTree);						
		}
	}

	private boolean isAssignmentTree(ExpressionTree expressionTree) {
		return notNull(expressionTree) && expressionTree.is(Tree.Kind.ASSIGNMENT);
	}
}
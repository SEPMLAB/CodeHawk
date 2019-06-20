package org.smell.metricruler;

import java.util.List;

import org.smell.astmodeler.ClassNode;
import org.smell.astmodeler.MethodNode;
import org.smell.astmodeler.Node;
import org.smell.astparser.ESTP;
import org.smell.astparser.ASTTreeParser;
import org.smell.astparser.VariableTreeProcess;
import org.sonar.plugins.java.api.tree.ExpressionStatementTree;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.StatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

public abstract class FeatureEnvyMetric implements Metric {
	
	public abstract void calculateMetric(Node node, MethodNode methodNode);
		
	protected void parseMethodTree(ClassNode classNode, MethodNode methNode) {	
		List<StatementTree> statements = methNode.getStatementsInMethodNode();
		for (StatementTree statementTree : statements) {			
			analysisStatements(statementTree,classNode);
		}
	}
	
	//傳入ATFD本身的參考 由parse判斷傳入物件的類型執行對應的操作
	protected void analysisStatements(StatementTree statementTree, ClassNode classNode) {
		if (statementTree.is(Tree.Kind.VARIABLE)) {			
			ExpressionTree expressionTree = ((VariableTree) statementTree).initializer();
			if(notNull(expressionTree)) {
				ASTTreeParser vtp = new VariableTreeProcess(classNode);
				vtp.parse(this,expressionTree);		
			}			
		} else if (statementTree.is(Tree.Kind.EXPRESSION_STATEMENT)) {
			ExpressionTree expressionStatementTree = ((ExpressionStatementTree) statementTree).expression();		
			if(notNull(expressionStatementTree)) {
				ASTTreeParser estp = new ESTP(classNode);				
				estp.parse(this,expressionStatementTree);
			}			
		}
	}
	
	private boolean notNull(Object object) {
		return object != null;
	}
}
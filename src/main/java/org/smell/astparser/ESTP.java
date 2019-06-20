package org.smell.astparser;

import org.smell.astmodeler.ClassNode;
import org.smell.metricruler.Metric;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.Tree;

/*
 *  Expression Statement Tree Processor 
 *  ³B²zexpression statement tree
 *  
 */
public class ESTP extends ExpressionParser{
	//String logPath =  "D:\\test\\localattributesaccessedDetected.txt";
	private ClassNode ownerClass;
	
	public ESTP(ClassNode node) {
		this.ownerClass = node;
	}
	
	private void callLowerPareser(Metric metric, ExpressionTree expressionTree) {
		if (expressionTree.is(Tree.Kind.METHOD_INVOCATION)) {
			ASTTreeParser mitp = new MITP(this.ownerClass);
			mitp.parse(metric,expressionTree);
		}else if(expressionTree.is(Tree.Kind.ASSIGNMENT)) {
			ASTTreeParser atp = new ATP(this.ownerClass);
			atp.parse(metric,expressionTree);				
		}	
	}
	
	@Override
	public void parse(Metric metric, ExpressionTree expressionTree) {
		if(notNull(expressionTree) && isFeatureEnvyMetrics(metric)) {
			callLowerPareser(metric,expressionTree);
		}
	}
}
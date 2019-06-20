package org.smell.astparser;

import org.smell.astmodeler.ClassNode;
import org.smell.metricruler.Laa;
import org.smell.metricruler.LaaData;
import org.smell.metricruler.Metric;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.Tree;

public class VariableTreeProcess extends VariableParser {
	private ClassNode ownerClass;
	
	// 由owner來判斷是否為ATFD
	public VariableTreeProcess(ClassNode node) {
		this.ownerClass = node;
	}

	private void callLowerPareser(Metric metric, ExpressionTree expressionTree) {
		if (expressionTree.is(Tree.Kind.METHOD_INVOCATION)) {
			ASTTreeParser mitp = new MITP(this.ownerClass);
			mitp.parse(metric,expressionTree);		
		} else if (expressionTree.is(Tree.Kind.MEMBER_SELECT)) {
			ASTTreeParser msetp = new MSETP(this.ownerClass);
			msetp.parse(metric,expressionTree);									
		}	
	}
	
	@Override
	public void parse(Metric metric, ExpressionTree expressionTree) {
		if(notNull(expressionTree) && metric.is( Metric.Type.ATFD)) {
			callLowerPareser(metric,  expressionTree);		
		}else if (notNull(expressionTree) && metric.is( Metric.Type.LAA)) {
			if(expressionTree.is(Tree.Kind.IDENTIFIER)) {
				//local data accessed detected 		
				LaaData laaData =   (LaaData)((Laa)metric).getData();
				int localAttributesAccessed = laaData.getLocalAttributesAccessed() + 1;
				laaData.setLocalAttributesAccessed(localAttributesAccessed);
			}else {
				callLowerPareser(metric,  expressionTree);
			}			
		}else if ( notNull(expressionTree) && metric.is( Metric.Type.FDP)) {
			callLowerPareser(metric,  expressionTree);
		}
	}
}
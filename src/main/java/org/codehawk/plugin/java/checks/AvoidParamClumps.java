package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;

import org.codehawk.smell.modler.DataClumpDetector;
import org.codehawk.smell.modler.DataMember;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

@Rule(key = "avoidDataClumps")

public class AvoidParamClumps extends IssuableSubscriptionVisitor{
	DataClumpDetector detector = new DataClumpDetector();
	
	@Override
	public List<Tree.Kind> nodesToVisit() {
		// Register to the kind of nodes you want to be called upon visit.
		return Collections.singletonList(Tree.Kind.METHOD);
	}

	@Override
	public void visitNode(Tree tree) {
		MethodTree mt = (MethodTree) tree;
		List<VariableTree> params = mt.parameters();
		DataMember methodNode = new DataMember(context, mt.openParenToken().line(), "method");
		// map of variable name to this class
		Multimap<String, DataMember> nameToNode = ArrayListMultimap.create();

		for (VariableTree vt : params) {
			String name = vt.simpleName().name();
			nameToNode.put(name, methodNode);
			
			methodNode.addmember(vt);
		}
		
		if(detector.detect(methodNode))
			detector.reportSmell(methodNode, this);
		
		detector.putAll(nameToNode);
	}
}
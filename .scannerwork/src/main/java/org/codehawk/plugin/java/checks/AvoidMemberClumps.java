package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;

import org.codehawk.smell.modler.DataClumpDetector;
import org.codehawk.smell.modler.DataMember;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.Tree.Kind;
import org.sonar.plugins.java.api.tree.VariableTree;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

@Rule(key = "avoidMemberClumps")
public class AvoidMemberClumps extends IssuableSubscriptionVisitor {
	DataClumpDetector detector = new DataClumpDetector();
	public final int clumpThresh = 10;

	@Override
	public List<Kind> nodesToVisit() {
		List<Tree.Kind> visitList = Collections.singletonList(Tree.Kind.CLASS);
		// Register to the kind of nodes you want to be called upon visit.
		return visitList;
	}

	@Override
	public void visitNode(Tree tree) {
		// instantiate class data members
		ClassTree ct = (ClassTree) tree;
		DataMember classMembers = new DataMember(context, ct.openBraceToken().line(), "class");
		//map of variable name to this class
		Multimap<String, DataMember> nameToNode = ArrayListMultimap.create();
		
		//add every member to node
		for (Tree t : ct.members()) {
			if (t.is(Tree.Kind.VARIABLE)) {
				VariableTree vt = (VariableTree) t;
				String name = vt.simpleName().name(); // name of the variable
				nameToNode.put(name, classMembers);
				
				classMembers.addmember(vt);
			}
		}
		
		if(detector.detect(classMembers))
			detector.reportSmell(classMembers, this);
		
		detector.putAll(nameToNode);
	}

}

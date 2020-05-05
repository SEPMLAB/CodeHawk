package org.codehawk.plugin.java.checks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.codehawk.smell.modler.PODetector;
import org.codehawk.smell.modler.POMethodNode;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;

import org.codehawk.smell.smellruler.Smell;

@Rule(key = "AvoidPrimitiveObsession")
public class AvoidPrimitiveObsession extends IssuableSubscriptionVisitor {

	@Override
	public List<Tree.Kind> nodesToVisit() {
		// Register to the kind of nodes you want to be called upon visit.
		return Collections.singletonList(Tree.Kind.CLASS);
	}

	@Override
	public void visitNode(Tree tree) {
		List<POMethodNode> poMethodNodes = new ArrayList<>();
		int[] primitveRatio = {0,0}; // will refactor to ratio class

		ClassTree ct = (ClassTree) tree;
		List<Tree> members = ct.members();
		// iterate through methods inside class
		for (Tree t : members) {
			if (t.is(Tree.Kind.METHOD)) {
				MethodTree mt = (MethodTree) t;
				POMethodNode poMethodNode = new POMethodNode(); // create new node
				poMethodNode.setStartLine(mt.openParenToken().line());

				int[] ratio = PODetector.getRatio(mt);
				addRatio(primitveRatio, ratio); // add on to primitve type ratio of current class

				poMethodNode.setRatio(ratio); // set primitve type ratio
				poMethodNodes.add(poMethodNode); // add to list of method nodes in current class
			}
		}

		PODetector.registerSmell(primitveRatio, poMethodNodes);

		for (POMethodNode poNode : poMethodNodes) {
			if (poNode.haveSmell(Smell.Type.PRIMITIVE_OBSESSION)) {
				PODetector.reportSmell(poNode, this, context);
			}
		}

	}

	// add ratio to
	public void addRatio(int[] classPrimitveRatio, int[] ratio) {
			classPrimitveRatio[0] += ratio[0];
			classPrimitveRatio[1] += ratio[1];
	}
}

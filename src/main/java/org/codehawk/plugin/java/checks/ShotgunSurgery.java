package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;

import org.codehawk.smell.modler.ShotgunSurgeryDetector;
import org.codehawk.smell.modler.ShotgunSurgeryNode;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key = "ShotgunSurgery")
public class ShotgunSurgery extends IssuableSubscriptionVisitor {
	private ShotgunSurgeryDetector detector = new ShotgunSurgeryDetector();

	@Override
	public List<Tree.Kind> nodesToVisit() {
		return Collections.singletonList(Tree.Kind.CLASS);// check every class
	}

	@Override
	public void visitNode(Tree tree) {
		ClassTree classTree = (ClassTree) tree;
		ShotgunSurgeryNode shotgunSurgeryNode = new ShotgunSurgeryNode(context, this);
		detector.detect(classTree, shotgunSurgeryNode);
	}
}
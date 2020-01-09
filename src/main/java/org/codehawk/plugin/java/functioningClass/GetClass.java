package org.codehawk.plugin.java.functioningClass;

import java.util.List;

import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;

public class GetClass {
	
	public static int setClassCount(List<Tree> lt) {
		int classCount = 0;
		for(Tree tree: lt) {
			if(tree.is(Tree.Kind.CLASS)) {
				classCount++;
				ClassTree ct = (ClassTree)tree;
				setClassCount(ct.members());
			}
		}
		return classCount;
	}
	
}

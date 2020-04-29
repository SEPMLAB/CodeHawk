package org.codehawk.smell.modler;

import java.util.List;

import org.codehawk.smell.smellruler.PrimitiveObsession;
import org.codehawk.smell.smellruler.Smell;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;


public class PODetector implements Detector{

	// ­pºâ¤ñ¨Ò
	public static int[] getRatio(MethodTree mt) {
		int primitives = 0;
		int size = 0;
		try {
			for (VariableTree vt : mt.parameters()) {
				if(vt.type().is(Tree.Kind.PRIMITIVE_TYPE)) {
					primitives += 1;
				}
			}
			size = mt.parameters().size();
		} catch (Exception e) {

		}
		return new int[]{primitives, size};
	}

	public static void registerSmell(int[] ratioThresh, List<? extends SmellableNode> poMethodNodes) {
		for(SmellableNode node : poMethodNodes) {
			if(node.is(Node.Kind.METHOD)) {
				POMethodNode poNode = (POMethodNode) node;
				int[] poRatio = poNode.getRatio();
				if(poRatio[1] != 0) {
					if(poRatio[0] / poRatio[1] > ratioThresh[0]/ratioThresh[1]) {
						poNode.registerSmell(new PrimitiveObsession());
					}
				}
			}
		}
		
	}
	
	@Override
	public boolean detect(Node node) {
		return false;
	}

	// report smell
	public static void reportSmell(POMethodNode poNode, JavaCheck check, JavaFileScannerContext context) {
		context.addIssue(poNode.getStartLine(), check, poNode.getRegisteredSmell(Smell.Type.PRIMITIVE_OBSESSION).smellDetail()); 
		
	}

}

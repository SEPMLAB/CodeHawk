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

	// �p������
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
		// System.out.println("thresh: " + ratioThresh[0] + ratioThresh[1]);
		for(SmellableNode node : poMethodNodes) {
			if(node.is(Node.Kind.METHOD)) {
				POMethodNode poNode = (POMethodNode) node;
				int[] poRatio = poNode.getRatio();

				if(poRatio[1] != 0) {
					double rm = (double)poRatio[0] / (double)poRatio[1];
					double rM = (double)ratioThresh[0] / (double)ratioThresh[1];
					// System.out.println("po Ratio: " + rm + " " + rM);
					if(rm > rM) {
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

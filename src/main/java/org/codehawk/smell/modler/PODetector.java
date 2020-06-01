package org.codehawk.smell.modler;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.List;

import org.codehawk.smell.smellruler.PrimitiveObsession;
import org.codehawk.smell.smellruler.Smell;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

public class PODetector implements Detector {

	public static final String[] primitveWrappers = new String[] { "Character", "Byte", "Short", "Integer", "Float",
			"Double", "Boolean" };
	public static final Set<String> wrapperTypes = new HashSet<>(Arrays.asList(primitveWrappers));


	public static int[] getRatio(MethodTree mt) {
		int primitives = 0;
		int size = 0;
		try {
			for (VariableTree vt : mt.parameters()) {
				if (isPrimitive(vt)) {
					primitives++;
				}
			}
			size = mt.parameters().size();
		} catch (Exception e) {

		}
		return new int[] { primitives, size };
	}

	public static boolean isPrimitive(VariableTree vt) {
		if (vt.type().is(Tree.Kind.PRIMITIVE_TYPE))
			return true;
		if (wrapperTypes.contains(vt.type().symbolType().name()))
			return true;

		return false;
	}

	public static void registerSmell(int[] ratioThresh, List<? extends SmellableNode> poMethodNodes) {
		// System.out.println("thresh: " + ratioThresh[0] + ratioThresh[1]);
		for (SmellableNode node : poMethodNodes) {
			if (node.is(Node.Kind.METHOD)) {
				POMethodNode poNode = (POMethodNode) node;
				int[] poRatio = poNode.getRatio();

				// register smell if exceeds threshold id parameters count is over 3
				if (poRatio[1] > 3) {
					double rm = (double) poRatio[0] / (double) poRatio[1];
					double rM = (double) ratioThresh[0] / (double) ratioThresh[1];
					// System.out.println("po Ratio: " + rm + " " + rM);
					if (rm > rM) {
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
		context.addIssue(poNode.getStartLine(), check,
				poNode.getRegisteredSmell(Smell.Type.PRIMITIVE_OBSESSION).smellDetail());

	}

}

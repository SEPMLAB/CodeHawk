package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;


import org.codehawk.smell.ThresholdDTO;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Modifier;
import org.sonar.plugins.java.api.tree.ModifierKeywordTree;
import org.sonar.plugins.java.api.tree.ModifiersTree;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key = "InsufficientModularation")
/**
 * To use subsctiption visitor, just extend the IssuableSubscriptionVisitor.
 */
public class InsufficientModularation extends IssuableSubscriptionVisitor {
	// brief InsufficientModularation to ISM
	protected Integer methodCount = 0 ;
	protected Integer publicMethodCount = 0;
	protected Integer classComplexity = 0;

	@Override
	public List<Tree.Kind> nodesToVisit() {
		return Collections.singletonList(Tree.Kind.CLASS);
	}

	@Override
	public void visitNode(Tree tree) {

		ClassTree ct = (ClassTree) tree ;

		for (Tree t : ct.members()) {
			if (t.is(Tree.Kind.METHOD)) {
				MethodTree mt = (MethodTree) t;
				ModifiersTree modt = mt.modifiers();
				List<ModifierKeywordTree> modkt = modt.modifiers();
				for (ModifierKeywordTree mtkt : modkt) {
					if (mtkt.modifier().equals(Modifier.PUBLIC)) {
						publicMethodCount ++ ;
					}else if (mtkt.modifier().equals(Modifier.PRIVATE)||mtkt.modifier().equals(Modifier.PROTECTED)){
						methodCount ++ ;
					}
				}
			}
		}
		classComplexity = context.getComplexityNodes(ct).size();

		if (hasISM(methodCount, publicMethodCount, classComplexity)) {
			addIssue(ct.openBraceToken().line(),
			"This class has InsufficientModularation, there should be over 20 public interfaces, eihther over 30 methods, or has complexity count over 100 times.");
		}
	}

	private boolean hasISM(Integer methodCount, Integer publicMethodCount, Integer classComplexity) {
		return hasLargePublicInterface(publicMethodCount) || hasTooManyMethod(methodCount) || hasHighComplexity(classComplexity);
	}

	private boolean hasLargePublicInterface(Integer publicMethodCount) {
		ThresholdDTO thresholdDTO = new ThresholdDTO();

		if (publicMethodCount >= thresholdDTO.getInsufficientModularizationLargePublicInterface()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean hasTooManyMethod(Integer methodCount) {
		ThresholdDTO thresholdDTO = new ThresholdDTO();

		if (methodCount >= thresholdDTO.getInsufficientModularizationLargeNumOfMethods()) {
			return true;
		} else {
			return false;
		}
	}

	private boolean hasHighComplexity(Integer classComplexity) {
		ThresholdDTO thresholdDTO = new ThresholdDTO();

		if (classComplexity >= thresholdDTO.getInsufficientModularizationHighComplexity()) {
			return true;
		} else {
			return false;
		}

	}
}
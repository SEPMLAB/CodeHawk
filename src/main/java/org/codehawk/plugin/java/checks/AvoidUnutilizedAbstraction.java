package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.Tree;

@Rule(key = "AvoidUnutilizedAbstraction")
/**
 * To use subsctiption visitor, just extend the IssuableSubscriptionVisitor.
 */
public class AvoidUnutilizedAbstraction extends IssuableSubscriptionVisitor {

  @Override
  public List<Tree.Kind> nodesToVisit() {
    // Register to the kind of nodes you want to be called upon visit.
    return Collections.singletonList(Tree.Kind.CLASS);
  }

  @Override
  public void visitNode(Tree tree) {

    ClassTree ct = (ClassTree) tree;
    System.out.println(ct.simpleName().name());

    if (ct.superClass() == null) {
      List<IdentifierTree> absctUse = ct.symbol().usages();
      if (absctUse.size() < 1) {
        addIssue(ct.openBraceToken().line(), "It is Unutilized Abstraction");
      }
    }

    // ModifiersTree modifiersOfVT = ct.modifiers();
    // List<ModifierKeywordTree> modifiers = modifiersOfVT.modifiers();

    // for (ModifierKeywordTree mtkt : modifiers) {
    // if (mtkt.modifier().equals(Modifier.ABSTRACT)) {
    // System.out.println(ct.simpleName().name());
    // List<IdentifierTree> absctUse = ct.symbol().usages();
    // if(absctUse.size() < 1) {
    // addIssue(ct.openBraceToken().line(),"It is Unutilized Abstraction");
    // }
    // System.out.println();
    // }
    // }

  }

}
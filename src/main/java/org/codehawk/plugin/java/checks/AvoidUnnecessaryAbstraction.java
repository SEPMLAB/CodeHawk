package org.codehawk.plugin.java.checks;

import java.util.Collections;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.ModifierKeywordTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.codehawk.smell.ThresholdDTO;
import org.codehawk.smell.metricruler.ClassMetrics;

@Rule(key = "AvoidUnnecessaryAbstraction")
public class AvoidUnnecessaryAbstraction extends IssuableSubscriptionVisitor {

  @Override
  public List<Tree.Kind> nodesToVisit() {
    // Register to the kind of nodes you want to be called upon visit.
    return Collections.singletonList(Tree.Kind.CLASS);
  }

  @Override
  public void visitNode(Tree tree) {
    ClassTree ct = (ClassTree) tree;

    if (isUnnecessary(ct)) {
      addIssue(ClassMetrics.extractStartingLine(ct), "abstract classes should at least one method and 6 fields.");
    }
  }

  // private boolean isAbstract(ClassTree ct) {
  // List<ModifierKeywordTree> modifierList = ct.modifiers().modifiers();

  // for (ModifierKeywordTree modifier : modifierList) {
  // if (modifier.modifier().equals(Modifier.ABSTRACT)) {
  // return true;
  // }
  // }

  // return false;
  // }

  private boolean isUnnecessary(ClassTree ct) {
    ThresholdDTO thresholdDTO = new ThresholdDTO();

    // get number of fields
    int numOfFields = ClassMetrics.extractNumOfFieldsMetrics(ct);
    // get number of methods
    int numOfMethods = ClassMetrics.extractNumOfMethodsMetrics(ct);

    return numOfMethods == thresholdDTO.getUnnecessaryAbstractionFewMethods()
        && numOfFields <= thresholdDTO.getUnnecessaryAbstractionFewFields();
  }
}

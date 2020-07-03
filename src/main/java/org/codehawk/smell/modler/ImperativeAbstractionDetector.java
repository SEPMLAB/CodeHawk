package org.codehawk.smell.modler;

import java.util.List;

import org.codehawk.smell.metricruler.GetLines;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Modifier;
import org.sonar.plugins.java.api.tree.ModifierKeywordTree;
import org.sonar.plugins.java.api.tree.Tree;

public class ImperativeAbstractionDetector implements Detector {
    private Boolean methodLine = false;
    private int publicMethod = 0;

    @Override
    public boolean detect(Node node) {
        return false;
    }

    public void detect(ClassTree classTree) {
        List<Tree> list = classTree.members();
        for (Tree tempTree : list) {
            if (tempTree.is(Tree.Kind.METHOD)) {// check every method of each classes
                MethodTree methodTree = (MethodTree) tempTree;
                List<ModifierKeywordTree> modifierKeywordTreeList = methodTree.modifiers().modifiers();
                for (ModifierKeywordTree modifierKeywordTree : modifierKeywordTreeList) {
                    if (modifierKeywordTree.modifier() == Modifier.PUBLIC) {// if method's modifier is public
                        publicMethod++;
                        cheeckMethodLines(methodTree);
                    }
                }
            }
        }
    }

    // check if the number of lines in this method is > 50
    private void cheeckMethodLines(MethodTree methodTree) {
        if (GetLines.getMethodTreeLines(methodTree) > GetLines.getMaxMethodLines()) {
            methodLine = true;
        }
    }

    //check if this class contains only 1 public method, and its number of lines is > 50
    public boolean check() {
        return Boolean.TRUE.equals(methodLine) && publicMethod == 1;
    }
}
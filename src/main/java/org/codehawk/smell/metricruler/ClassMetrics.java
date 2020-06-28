package org.codehawk.smell.metricruler;

import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;

public class ClassMetrics {

    // returns number of fields
    public static int extractNumOfFieldsMetrics(ClassTree tree) {
        int numOfFields = 0;
        for (Tree t : tree.members()) {
            if (t.is(Tree.Kind.VARIABLE)) {
                numOfFields++;
            }
        }
        return numOfFields;
    }

    // returns number of methods
    public static int extractNumOfMethodsMetrics(ClassTree tree) {
        int numOfMethods = 0;
        for (Tree t : tree.members()) {
            if (t.is(Tree.Kind.METHOD)) {
                numOfMethods++;
            }
        }
        return numOfMethods;
    }

    // returns the starting line
    public static int extractStartingLine(ClassTree tree) {
        return tree.openBraceToken().line();
    }
}
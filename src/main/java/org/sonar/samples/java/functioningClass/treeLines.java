package org.sonar.samples.java.functioningClass;

import java.util.List;

import org.sonar.plugins.java.api.tree.AnnotationTree;
import org.sonar.plugins.java.api.tree.Arguments;
import org.sonar.plugins.java.api.tree.BlockTree;
import org.sonar.plugins.java.api.tree.CaseGroupTree;
import org.sonar.plugins.java.api.tree.CatchTree;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.DoWhileStatementTree;
import org.sonar.plugins.java.api.tree.ForEachStatement;
import org.sonar.plugins.java.api.tree.ForStatementTree;
import org.sonar.plugins.java.api.tree.IfStatementTree;
import org.sonar.plugins.java.api.tree.LabeledStatementTree;
import org.sonar.plugins.java.api.tree.MethodInvocationTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.SwitchStatementTree;
import org.sonar.plugins.java.api.tree.SynchronizedStatementTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.TryStatementTree;
import org.sonar.plugins.java.api.tree.WhileStatementTree;

public class treeLines {
	private static int maxMethodLines = 50;
	private static int maxClassLines = 150;
	
	
	public static int getMaxMethodLines() {
		return maxMethodLines;
	}
	
	public static int getMaxClassLines() {
		return maxClassLines;
	}
	
	public static int getMethodTreeLines(MethodTree tree){
		BlockTree bt = tree.block();
		int lines = tree_Lines(bt.body());
		return lines;
	}
	
	public static int getClassTreeLines(ClassTree tree){
		int lines = tree_Lines(tree.members());
		return lines;
	}
	
	public static <E> int recursiveLines(E st) {
		int lines = 1;
		if (((Tree) st).is(Tree.Kind.ANNOTATION)) {
			lines += recursiveLines(((AnnotationTree) st).arguments());
		} else if (((Tree) st).is(Tree.Kind.ARGUMENTS)) {
			lines += ((Arguments) st).closeParenToken().line() - ((Arguments) st).openParenToken().line();
		} else if (((Tree) st).is(Tree.Kind.BLOCK)) {
			lines += tree_Lines(((BlockTree) st).body());
		} else if (((Tree) st).is(Tree.Kind.CASE_GROUP)) {
			lines += tree_Lines(((CaseGroupTree) st).body());
		} else if (((Tree) st).is(Tree.Kind.CATCH)) {
			lines += recursiveLines(((CatchTree) st).block());
		} else if (((Tree) st).is(Tree.Kind.CLASS)) {
			lines += ((ClassTree) st).closeBraceToken().line() - ((ClassTree) st).openBraceToken().line();
		} else if (((Tree) st).is(Tree.Kind.DO_STATEMENT)) {
			lines += recursiveLines((Tree)((DoWhileStatementTree) st).statement());
		} else if (((Tree) st).is(Tree.Kind.FOR_EACH_STATEMENT)) {
			lines += recursiveLines((Tree)((ForEachStatement) st).statement());
		} else if (((Tree) st).is(Tree.Kind.FOR_STATEMENT)) {
			lines += recursiveLines((Tree)((ForStatementTree) st).statement());
		} else if (((Tree) st).is(Tree.Kind.IF_STATEMENT)) {
			lines += recursiveLines((Tree)((IfStatementTree) st).thenStatement());
			if (((IfStatementTree) st).elseStatement() != null) {
				lines += recursiveLines(((IfStatementTree) st).elseStatement());
			}
		} else if (((Tree) st).is(Tree.Kind.LABELED_STATEMENT)) {
			lines += recursiveLines(((LabeledStatementTree) st).statement());
		} else if (((Tree) st).is(Tree.Kind.METHOD_INVOCATION)) {
			lines += recursiveLines(((MethodInvocationTree) st).arguments());
		} else if (((Tree) st).is(Tree.Kind.SWITCH_STATEMENT)) {
			List<CaseGroupTree> lct = ((SwitchStatementTree) st).cases();
			for (CaseGroupTree ct : lct) {
				lines += 1 + tree_Lines(ct.body());
			}
		} else if (((Tree) st).is(Tree.Kind.SYNCHRONIZED_STATEMENT)) {
			lines += tree_Lines(((SynchronizedStatementTree) st).block().body());
		}  else if (((Tree) st).is(Tree.Kind.TRY_STATEMENT)) {
			lines += tree_Lines(((TryStatementTree) st).block().body());
			lines += tree_Lines(((TryStatementTree) st).finallyBlock().body());
		} else if (((Tree) st).is(Tree.Kind.WHILE_STATEMENT)) {
			lines += recursiveLines(((WhileStatementTree) st).statement());
		} else if (((Tree) st).is(Tree.Kind.CONSTRUCTOR)) {
			lines += recursiveLines(((MethodTree) st).block());
		} else if (((Tree) st).is(Tree.Kind.METHOD)) {
			lines += recursiveLines(((MethodTree) st).block());
		}
		return lines;
	}

	public static <E> int tree_Lines(List<E> lst) {
		int lines = 0;
		for (E st : lst) {
			lines += recursiveLines(st);
		}
		return lines;
	}
}

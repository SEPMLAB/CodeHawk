package org.codehawk.plugin.java.checks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehawk.plugin.java.functioningclass.GetClass;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.IdentifierTree;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Modifier;
import org.sonar.plugins.java.api.tree.ModifierKeywordTree;
import org.sonar.plugins.java.api.tree.ModifiersTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

@Rule(key = "AvoidInappropriateIntimacy")
/**
 * To use subsctiption visitor, just extend the IssuableSubscriptionVisitor.
 */
public class AvoidInappropriateIntimacy extends IssuableSubscriptionVisitor {

	private Map<String, ArrayList<String>> relationList = new HashMap<>();
	private ArrayList<String> classList = new ArrayList<>();
	private ArrayList<Integer> classLineList = new ArrayList<>();
	private int classCount = 0;
	private int issueNum = 0;

	@Override
	public List<Tree.Kind> nodesToVisit() {
		// Register to the kind of nodes you want to be called upon visit.
		return Collections.singletonList(Tree.Kind.CLASS);
	}

	@Override
	public void visitNode(Tree tree) {

		ArrayList<MethodTree> vtUseList = new ArrayList<>();// 紀錄Class中的Private變數被哪些METHOD使用
		ArrayList<String> mtUseList = new ArrayList<>(); // 紀錄vtUseList中的方法被哪些Class使用

		// to count the number of classTrees
		if (classCount == 0) {
			classCount = GetClass.setClassCount(context.getTree().types());
		}

		ClassTree ct = (ClassTree) tree;
		String className = ct.simpleName().name();
		int classLine = ct.openBraceToken().line();

		// 取得Private變數，並執行vtuseCheck()
		for (Tree t : ct.members()) {
			if (t.is(Tree.Kind.VARIABLE)) {
				VariableTree vt = (VariableTree) t;
				ModifiersTree modifiersOfVT = vt.modifiers();
				List<ModifierKeywordTree> modifiers = modifiersOfVT.modifiers();

				for (ModifierKeywordTree mtkt : modifiers) {
					if (mtkt.modifier().equals(Modifier.PRIVATE)) {
						System.out.println(vt.simpleName().name());
						List<IdentifierTree> vtUse = vt.symbol().usages();
						vtuseCheck(vtUse, vtUseList);
					}
				}
			}
		}

		rlListUpdate(vtUseList, className, mtUseList);
		finalCheck(className, classLine, mtUseList);

		if (--classCount == 0 && !classList.isEmpty()) {
			for (String cName : classList) {
				for (String target : relationList.get(cName)) {
					if (relationList.get(target) != null) {
						if (relationList.get(target).contains(cName)) {
							addIssue(classLineList.get(issueNum),
									"this class has Inappropriate Intimacy with class " + target);
						}
					}
				}
				issueNum++;
			}
			System.out.println("close");
		}
	}

	// 紀錄Class中的Private變數被哪些METHOD使用
	public void vtuseCheck(List<IdentifierTree> trees, ArrayList<MethodTree> vtuseList) {
		for (Tree target : trees) {
			while (target.parent() != null) {

				if (target.parent().is(Tree.Kind.METHOD)) {
					MethodTree mt = (MethodTree) target.parent();
					System.out.println("use in:" + mt.simpleName().name());
					if (!vtuseList.contains(mt)) {
						vtuseList.add(mt);
					}
					break;
				} else {
					target = target.parent();
				}
			}
		}
	}

	// 紀錄vtUseList中的方法被那些Class使用到
	public void rlListUpdate(ArrayList<MethodTree> vtuseList, String className, ArrayList<String> mtUseList) {

		for (MethodTree mt : vtuseList) {
			System.out.println(mt.simpleName().name());
			List<IdentifierTree> mtUse = mt.symbol().usages();
			for (Tree target : mtUse) {
				while (target.parent() != null) {

					if (target.parent().is(Tree.Kind.CLASS)) {
						ClassTree ct = (ClassTree) target.parent();
						System.out.println("use in:" + ct.simpleName().name());
						if (!(ct.simpleName().name()).equals(className)) {
							mtUseList.add(ct.simpleName().name());
							System.out.println("成功存入Class");
						}
						break;
					} else {
						target = target.parent();
					}
				}
			}
		}
	}

	// 結算Private變數被使用的次數
	public void finalCheck(String className, int classLine, ArrayList<String> mtUseList) {

		ArrayList<String> temp = new ArrayList<>(); // 紀錄使用3次以上的CLASS

		while (mtUseList.size() > 2) {
			System.out.println("list size: " + mtUseList.size());
			ArrayList<Integer> position = new ArrayList<>();
			int num = 1;
			String tempclass = mtUseList.get(0);

			for (int i = 1; i < mtUseList.size(); i++) {
				if (mtUseList.get(i).equals(tempclass)) {
					num++;
					position.add(i);
				}
			}

			if (num >= 3) {
				temp.add(tempclass);
				if (!classList.contains(className)) {
					classList.add(className);
					classLineList.add(classLine);
				}
				System.out.println("超過3個");
			}
			if (num > 1) {
				for (int i = position.size() - 1; i >= 0; i--) {
					int tempP = position.get(i);
					mtUseList.remove(tempP);
				}
			}

			mtUseList.remove(0);

		}
		relationList.put(className, temp);
	}

}
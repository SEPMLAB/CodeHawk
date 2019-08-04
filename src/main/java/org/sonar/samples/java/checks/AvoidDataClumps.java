package org.sonar.samples.java.checks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

@Rule(key="avoidDataClumps")

public class AvoidDataClumps extends IssuableSubscriptionVisitor {
	private ArrayList<SimplifiedClassTree> classList = new ArrayList<SimplifiedClassTree>();
	int classAmount = 0;
	private int alikeThreshold = 2;
	private int classThreshold = 10;
	
	@Override
	public List<Tree.Kind> nodesToVisit() {
		List<Tree.Kind> visitList = Collections.singletonList(Tree.Kind.CLASS);
		// Register to the kind of nodes you want to be called upon visit.
		return visitList;
	}

	@Override
	public void visitNode(Tree tree) {
		/*ver2*/
		setClassAmount();
		
		ClassTree ct = (ClassTree)tree;
		SimplifiedClassTree simpTree = new SimplifiedClassTree(ct);
		
		for(Tree t: ct.members()) {
			if(t.is(Tree.Kind.VARIABLE)) {
				VariableTree vt = (VariableTree)t;
				simpTree.addMember(vt);
				checkMembers(simpTree, vt);
			}
		}
		classList.add(simpTree);
		
		if(classList.size() == classAmount) {
			startReport();
		}
	}
	
	//report every class exceeding treshold
	public void startReport() {
		for(SimplifiedClassTree sct: classList) {
			if(sct.getOverThresh().size() >= classThreshold) {
				String message = "avoid data clump on line ";
				//add every line number of the variable exceeding threshold
				for(Integer i: sct.getOverThresh()) {
					message += sct.getMembers().get(i).endToken().line() + " ";
				}
				addIssue(sct.getLine(),message);
			}
		}
	}
	
	public void setClassAmount() {
		int amount = 0;
		if(classAmount == 0 && context != null) {
			List<Tree> lt = context.getTree().types();
			for(Tree tree: lt) {
				if(tree.is(Tree.Kind.CLASS)) {
					amount ++;
				}
			}
			classAmount = amount;
		}
	}
	
	/* set alikes of the SimplifiedClassTree according to the number of times it appears
	 * in other classes
	 * increase alikes for the previous classes which it appeared in*/
	public void checkMembers(SimplifiedClassTree simpTree, VariableTree vt) {
		if(!classList.isEmpty()) {
			for(SimplifiedClassTree sct: classList) {
				ArrayList<VariableTree> stMembers = sct.getMembers();
				int index = findSame(stMembers, vt);
				
				//when found same data members
				if(index != -1) {
					//put the member in overThresh list if it has exceeded threshold after adding alike
					if(sct.addAlikes(index) >= alikeThreshold) {
						sct.addOverThresh(index);
					}
					
					index = simpTree.getMembers().indexOf(vt);
					if(simpTree.addAlikes(index) >= alikeThreshold) {
						simpTree.addOverThresh(index);
					}
				}
			}
		}
		
	}
	
	//return the index of the variable tree in vtList which vt1 is the same as
	public int findSame(ArrayList<VariableTree> vtList, VariableTree vt1) {
		for(VariableTree vt2: vtList) {
			if(vt1.simpleName().name().equals(vt2.simpleName().name()) && vt1.type().symbolType().fullyQualifiedName().equals(vt2.type().symbolType().fullyQualifiedName())) {
				return vtList.indexOf(vt2);
			}
		}
		return -1;
	}
}

class SimplifiedClassTree{
	private int startingLine;
	private ArrayList<VariableTree> dataMembers = new ArrayList<VariableTree>();
	private ArrayList<Integer> alikes = new ArrayList<Integer>();
	//indicates which vriable tree in dataMembers exceeded threshold
	private TreeSet<Integer> overThreshMembers = new TreeSet<Integer>();
	
	SimplifiedClassTree(ClassTree tree){
		startingLine = tree.openBraceToken().line();
	}
	
	public int getLine() {
		return startingLine;
	}
	
	public void addMember(VariableTree vt) {
		dataMembers.add(vt);
		alikes.add(0);
	}
	
	public ArrayList<VariableTree> getMembers() {
		return dataMembers;
	}
	
	public int addAlikes(int index) {
		alikes.set(index, alikes.get(index)+1);
		return alikes.get(index);
	}
	
	public void addOverThresh(int index) {
		if(!overThreshMembers.contains(index)) {
			overThreshMembers.add(index);
		}
	}
	
	public TreeSet<Integer> getOverThresh(){
		return overThreshMembers;
	}
	
}

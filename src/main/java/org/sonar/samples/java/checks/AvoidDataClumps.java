package org.sonar.samples.java.checks;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.JavaFileScannerContext.Location;
import org.sonar.plugins.java.api.tree.ClassTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

@Rule(key="avoidDataClumps")

public class AvoidDataClumps extends IssuableSubscriptionVisitor {
	private ArrayList<SimplifiedClassTree> classList = new ArrayList<>();
	private HashMap<SimplifiedClassTree, JavaFileScannerContext> fileMap = new HashMap<>();
	private int classCount = 0;
	private int fileCount = 0;
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
		/*statistic setting sequence*/
		//set the fileCount for the project
		if(fileCount == 0) {
			File sourceFolder;
			//find the file count in the src folder
			do {
				sourceFolder = context.getFile().getParentFile();
			}while(!(sourceFolder.getName().equals("src")));
			setFileCount(sourceFolder);
			System.out.println("fileCount: " + fileCount);
		}
		//set the classCount for every file
		if(classCount == 0) {
			setClassCount(context.getTree().types());
			System.out.println("classCount: " + classCount);
		}
		
		/*data input sequence*/
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
		fileMap.put(simpTree, context);
		System.out.println("visit" + classCount);
		
		/*issue report sequence*/
		if(--classCount == 0) {
			if(--fileCount == 0) {
				startReport();
			}
		}
	}
	
	//count amount of classes in a context
	public void setClassCount(List<Tree> lt) {
		for(Tree tree: lt) {
			if(tree.is(Tree.Kind.CLASS)) {
				classCount++;
				ClassTree ct = (ClassTree)tree;
				setClassCount(ct.members());
			}
		}
	}
	
	//count the java files in the denoted directory
	public void setFileCount(File dir) {
		File[] fileList = dir.listFiles();
		for(File f: fileList) {
			if(f.isDirectory()) {
				setFileCount(f);
			}
			else if(f.isFile()) {
				if(f.getName().endsWith(".java")) {
					fileCount ++;
				}
			}
		}
	}
	
	//report every class exceeding treshold
	public void startReport() {
		System.out.println("report");
		for(SimplifiedClassTree sct: classList) {
			if(sct.getOverThresh().size() >= classThreshold) {
				String message = "avoid data clump on line ";
				//add every line number of the variable exceeding threshold
				for(Integer i: sct.getOverThresh()) {
					message += sct.getMembers().get(i).endToken().line() + " ";
				}
				fileMap.get(sct).addIssue(sct.getLine(),this, message);
			}
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

/* for every class tree,simplify it by only storing the vairable members
 * and the first line of the class*/
class SimplifiedClassTree{
	private int startingLine;
	private ArrayList<VariableTree> dataMembers = new ArrayList<>();
	private ArrayList<Integer> alikes = new ArrayList<>();
	//indicates which vriable tree in dataMembers exceeded threshold
	private TreeSet<Integer> overThreshMembers = new TreeSet<>();
	
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

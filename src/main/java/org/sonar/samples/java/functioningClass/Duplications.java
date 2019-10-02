package org.sonar.samples.java.functioningClass;

import java.util.ArrayList;

public interface Duplications {
	public int size();

	public String type();
}

class DuplicationChain implements Duplications {
	int chainId;
	SmellMetrics m = new SmellMetrics();
	private ArrayList<DuplicateClones> cloneChain = new ArrayList<>();

	public DuplicationChain(DuplicateClones dc) {
		chainId = dc.cloneId;
		cloneChain.add(dc);
	}

	public ArrayList<DuplicateClones> chain() {
		return cloneChain;
	}

	public boolean addChain(DuplicateClones dc) {
		if (dc.size() > m.FEW) {
			if (dc.startingLine - cloneChain.get(cloneChain.size() - 1).endingLine <= m.FEW) {
				cloneChain.add(dc);
				return true;
			}
		}

		return false;
	}

	public int size() {

		return cloneChain.get(cloneChain.size() - 1).endingLine - cloneChain.get(0).startingLine;
	}

	public String type() {
		return "chain";
	}
}

//A class to save details of exact clones
class DuplicateClones implements Duplications {
	// an id for exact duplicates(same duplicates share the same id)
	int cloneId;
	int startingLine;
	int endingLine;

	public DuplicateClones() {

	}

	public int size() {
		return endingLine - startingLine;
	}

	public String type() {
		return "clone";
	}
}

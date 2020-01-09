package org.sonar.samples.java.functioningClass;

import java.util.ArrayList;

public interface Duplications {
	public int size();

	public String type();
}

//chain of exact clones
class DuplicationChain implements Duplications {
	//id of an individual chain, same as the id of the first clone in this chain
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
	
	//try to add a duplicate clone into this chain, do so if the clone meets the condidtion
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

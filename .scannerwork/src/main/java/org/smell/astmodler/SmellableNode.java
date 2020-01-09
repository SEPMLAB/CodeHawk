package org.smell.astmodler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.smell.smellruler.Smell;

public abstract class SmellableNode implements Smellable, Node {
	List<Smell> smellLists;
	int startLine;
	private File file;

	@Override
	public int getStartLine() {
		return startLine;
	}

	@Override
	public boolean haveSmell(Smell.Type type) {
		if (smellLists != null) {
			for (Smell registeredSmell : smellLists) {
				if (registeredSmell.is(type)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public Smell getRegisteredSmell(Smell.Type type) {
		for (Smell registeredSmell : smellLists) {
			if (registeredSmell.is(type)) {
				return registeredSmell;
			}
		}
		return null;
	}

	@Override
	public void registerSmell(Smell smelltoAdd) {
		if (smellLists != null) {
			for (Smell smellAlreadyDetected : smellLists) {
				if (smellAlreadyDetected.is(smelltoAdd.type())) {
					// detected same smell
					return;
				}
			}
			smellLists.add(smelltoAdd);
		} else {
			smellLists = new ArrayList<>();
			smellLists.add(smelltoAdd);
		}
	}

	@Override
	public boolean is(Node.Kind... kinds) {
		Node.Kind treeKind = kind();
		for (Kind kindIter : kinds) {
			if (treeKind == kindIter) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}
}

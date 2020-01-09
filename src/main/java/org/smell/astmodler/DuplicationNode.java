package org.smell.astmodler;

import org.sonar.plugins.java.api.tree.LiteralTree;
import org.sonar.plugins.java.api.tree.Tree;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class DuplicationNode extends SmellableNode{
	private String nodeName;
	private String nodeId;
	
	private final Multimap<String, Tree> occurrences = ArrayListMultimap.create();
	
	@Override
	public Kind kind() {
		return Node.Kind.DUPLICATE;
	}

	@Override
	public String getName() {
		return nodeName;
	}

}

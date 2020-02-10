package org.codehawk.smell.modler;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sonar.plugins.java.api.JavaFileScannerContext;
import org.sonar.plugins.java.api.tree.VariableTree;

public class DataMember extends SmellableNode {

	private Map<String, VariableTree> params = new HashMap<>();
	private Set<StringGroup> groups = new HashSet<>();
	private JavaFileScannerContext locatedFile;
	String type;

	public DataMember(JavaFileScannerContext context, int line, String type) {
		this.locatedFile = context;
		this.startLine = line;
		this.type = type;
	}

	public boolean addmember(VariableTree vt) {
		try {
			params.put(vt.simpleName().name(), vt);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public Collection<String> getNames() {
		return params.keySet();
	}

	public int size() {
		return params.size();
	}

	public boolean addGroup(StringGroup g) {
		if(groups.contains(g)) {
			return false;
		}
		else {
			groups.add(g);
			return true;
		}
	}
	
	public JavaFileScannerContext getContext() {
		return locatedFile;
	}
	
	public Set<StringGroup> getGroups(){
		return groups;
	}
	
	@Override
	public Kind kind() {
		return Node.Kind.DataMember;
	}

	@Override
	public String getName() {
		return "smellable class or method members";
	}

}

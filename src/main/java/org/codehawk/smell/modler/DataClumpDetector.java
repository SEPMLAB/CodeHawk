package org.codehawk.smell.modler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.codehawk.smell.metricruler.ClumpMetrics;
import org.sonar.plugins.java.api.JavaCheck;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class DataClumpDetector implements Detector{
	private Multimap<String, DataMember> nameToNode = ArrayListMultimap.create();
	private Map<DataMember, StringGroup> clumpMap; // holds most recent map of nodes with duplicated variable names
	private DataMember node;
	@Override
	public boolean detect(Node node) {
		DataMember dm = (DataMember) node;
		this.node = dm;

		// find nodes which at least one name of this node appered in,
		// map them to the according name
		Multimap<DataMember, String> memberToName = ArrayListMultimap.create();
		for(String name: dm.getNames()) {
			for(DataMember m: nameToNode.get(name)) {
				memberToName.put(m, name);
			}
		}
		
		return registerClumps(dm, memberToName, dm.type);
	}
	
	public boolean registerClumps(DataMember dm, Multimap<DataMember, String> memberToName, String type) {
		clumpMap = new HashMap<>();
		
		int thresh = 3;
		if(type.equals("class"))
			thresh = ClumpMetrics.classMemberThresh;
		else if(type.equals("method"))
			thresh = ClumpMetrics.paramThresh;
		
		// get duplicated names for each detected node
		Collection<String> clumpGroup;
		boolean found = false;
		for(DataMember m: memberToName.keySet()) {
			clumpGroup = memberToName.get(m);
			if( clumpGroup.size() >= thresh) {
				found = true;
				StringGroup group = new StringGroup(new ArrayList<String>(clumpGroup));
				// add string group to node, register for report it if not added before
				if(m.addGroup(group)) {
					clumpMap.put(m, group);
				}
				dm.addGroup(group);
			}
		}
		
		return found;
	}
	
	public void reportSmell(DataMember node, JavaCheck check) {
		if(node.equals(this.node)) {
			String message = "Avoid dataClump for group: ";
			// report clump of others
			for(DataMember dm: clumpMap.keySet()) {
				dm.getContext().addIssue(dm.getStartLine(), check, message+clumpMap.get(dm).getString());
			}
			
			// report clumps of this node
			for(StringGroup group: node.getGroups()) {
				node.getContext().addIssue(node.getStartLine(), check, message+group.getString());
			}
		}
	}
	
	public void putAll(Multimap<String, DataMember> map) {
		nameToNode.putAll(map);
	}
}

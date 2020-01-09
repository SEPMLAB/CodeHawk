package org.codehawk.plugin.java.checks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.codehawk.plugin.java.MyJavaRulesDefinition;
import org.smell.astmodler.DataMember;
import org.smell.astmodler.StringGroup;
import org.smell.smellruler.DataClump;
import org.smell.smellruler.Smell;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.check.Rule;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.plugins.java.api.tree.MethodTree;
import org.sonar.plugins.java.api.tree.Tree;
import org.sonar.plugins.java.api.tree.VariableTree;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

@Rule(key = "avoidDataClumps")

public class AvoidDataClumps extends IssuableSubscriptionVisitor implements JavaCheck, Sensor {
	// public static DataMember dm = new DataMember();
	public static Multimap<String, DataMember> paramNameMap = ArrayListMultimap.create();
	private final int clumpThresh = 3;

	@Override
	public List<Tree.Kind> nodesToVisit() {
		List<Tree.Kind> visitList = Collections.singletonList(Tree.Kind.METHOD);
		// Register to the kind of nodes you want to be called upon visit.
		return visitList;
	}

	@Override
	public void visitNode(Tree tree) {
		MethodTree mt = (MethodTree) tree;
		List<VariableTree> params = mt.parameters();

		/* map data members and check parameters for duplicates */
		if (params.size() >= clumpThresh) {
			DataMember currentMembers = new DataMember(context, mt.openParenToken().line(), "method");
			Set<DataMember> shouldCheck = new HashSet<>();

			for (VariableTree vt : params) {
				String name = vt.simpleName().name(); // name of the current parameter
				// add all datamembers that should be checked for duplicates later to a set
				shouldCheck.addAll(paramNameMap.get(name));

				currentMembers.addParam(vt);

				paramNameMap.put(name, currentMembers); // map current param name to current method
			}

			// register smell to all data members that have duplicate string groups
			registerClumps(shouldCheck, currentMembers);
		}
	}

	/*
	 * find common parameter names between previous datamembers and register them in
	 * the data member object as word groups
	 */
	private void registerClumps(Set<DataMember> shouldCheck, DataMember methodParams) {
		Collection<String> thisNames = methodParams.getNames();

		for (DataMember dm : shouldCheck) {
			// no need to check for duplicates if the number of parameters is under clump threshold
			if (dm.size() < clumpThresh) {
				continue;
			}

			// report the duplicated name to the DMs as a group if the size is over clump threshold
			List<String> duplicates = getClumps(dm.getNames(), thisNames);
			if (duplicates.size() >= clumpThresh) {
				StringGroup nameGroup = new StringGroup(duplicates);
				reportGroup(dm, nameGroup);
				reportGroup(methodParams, nameGroup);
			}
		}

	}

	//add string group to data member and report smell if the group has not been added before
	private void reportGroup(DataMember dm, StringGroup group) {
		if (dm.addGroup(group)) {
			dm.registerSmell(new DataClump());
			reportSmell(dm, group);
		}
	}
	
	// get the duplicated names between two collections
	private List<String> getClumps(Collection<String> names1, Collection<String> names2) {
		Set<String> baseNames = new HashSet<>(names1);
		List<String> duplicates = new ArrayList<>();
		for (String name : names2) {
			if (baseNames.contains(name)) {
				duplicates.add(name);
			}
		}
		return duplicates;
	}

	//report smell with the according string group names
	public void reportSmell(DataMember dm, StringGroup g) {
		String message = dm.getRegisteredSmell(Smell.Type.DATACLUMP).smellDetail();
		message += "Group: " + g.getString();
		dm.getContext().addIssue(dm.getStartLine(), this, message);
	}

	@Override
	public void describe(SensorDescriptor descriptor) {
		descriptor.name("Avoid data clumps in method");
		descriptor.onlyOnLanguage("java");
		descriptor.createIssuesForRuleRepositories(MyJavaRulesDefinition.REPOSITORY_KEY);
	}

	@Override
	public void execute(SensorContext context) {
		System.out.println("execute");
		// iterate through data members
		Set<DataMember> dataMembers = Sets.newHashSet(paramNameMap.values());
		System.out.println(dataMembers.size());
		/* reporting sequence */
		for (DataMember dm : dataMembers) {
			System.out.println(dm.getStartLine());
			if (dm.haveSmell(Smell.Type.DATACLUMP)) {
				System.out.println("have smell");
				String message = dm.getRegisteredSmell(Smell.Type.DATACLUMP).smellDetail();
				// report duplicated names as groups
				for (StringGroup sg : dm.getGroups()) {
					message += "\nGroup: " + sg.getString();
				}
				System.out.println(message);
				dm.getContext().addIssue(dm.getStartLine(), this, message);
				System.out.println(dm.getContext().getFileKey() + "\n");
			}
		}
	}

}
package org.smell.metricruler;

import java.util.List;

import org.smell.astmodeler.ClassNode;
import org.smell.astmodeler.Node;
import org.sonar.plugins.java.api.tree.Tree;

public class NopaAndNoam implements Metric {
	private static final int ACCESSOR_OR_FIELD_FEW_LEVEL = 3;
	private static final int ACCESSOR_OR_FIELD_MANY_LEVEL = 5;
	private final Type type  = Metric.Type.NOPA_AND_NOAM;
	private MetricData  nopaAndNoamData;
	
	public NopaAndNoam() {
		this.nopaAndNoamData = new NopaAndNoamData();
	}
	
	public void setNopaValue(int nopa) {
		((NopaAndNoamData)nopaAndNoamData).setNopaValue(nopa);
	}
	
	public void setNoamValue(int noam) {
		((NopaAndNoamData)nopaAndNoamData).setNoamValue(noam);
	}
	
	public int getNoamValue() {
		return ((NopaAndNoamData)nopaAndNoamData).getNoamValue();
	}
	
	public int getNopaValue() {
		return ((NopaAndNoamData)nopaAndNoamData).getNopaValue();
	}
	
	public boolean greaterThanFew() {
		return (getNoamValue() + getNopaValue() ) > ACCESSOR_OR_FIELD_FEW_LEVEL;
	}
	
	public boolean greaterThanMany() {
		return (getNoamValue() + getNopaValue() ) > ACCESSOR_OR_FIELD_MANY_LEVEL;
	}

	@Override
	public boolean is(Type type) {
		return this.type.equals(type);
	}
	
	public void calculateMetric(Node node) {
		configureNopaAndNoamOfClass(node);
	}

	private void configureNopaAndNoamOfClass(Node node) {
		ClassNode classNode = (ClassNode)node;
		List<Tree> publicAttributes = classNode.getPublicVariables();
		int nopa = publicAttributes.size();
		setNopaValue(nopa);
		List<Tree> getterAndSetterMethods = classNode.getGetterAndSetterMethods();
		int noam = getterAndSetterMethods.size();
		setNoamValue(noam);
	}

	public int getValue() {
		return getNoamValue() + getNopaValue();
	}
}
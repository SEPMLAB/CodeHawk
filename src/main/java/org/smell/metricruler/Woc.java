package org.smell.metricruler;

import org.smell.astmodeler.ClassNode;
import org.smell.astmodeler.Node;

/*
 * Weight of Class
 */
public class Woc implements Metric {
	private static final float WOC_THRESHOLD = (float) 1 / 3;
	private final Type type  = Metric.Type.WOC;
	private MetricData wocData;
	
	public Woc() {
		this.wocData = new WocData();
	}
	
	public boolean lessThanThreshold() {
		return(((WocData)wocData).getValue() < WOC_THRESHOLD);
	}
	
	public void setValue(float value) {
		((WocData)wocData).setValue(value);
	}

	public float getValue() {
		return ((WocData)wocData).getValue();
	}
	
	public void calculateMetric(Node node) {
		configureWeightOfClass(node);
	}

	public void configureWeightOfClass(Node node) {
		ClassNode classNode = (ClassNode)node;
		int publicMethodsCounts = classNode.getPublicMethods().size();
		int publicMembersCounts = classNode.getPublicMembers().size();
		float weightOfClass = (float) publicMethodsCounts / publicMembersCounts;
		setValue(weightOfClass);
	}

	@Override
	public boolean is(Type type) {
		return this.type.equals(type);
	}
}
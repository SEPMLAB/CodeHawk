package org.smell.metricruler;

import org.smell.astmodeler.ClassNode;
import org.smell.astmodeler.MethodNode;
import org.smell.astmodeler.Node;

public class Atfd extends FeatureEnvyMetric {
	private final Type type  = Metric.Type.ATFD;
	private static final int FEW_ATFD_THRESHOLD = 5;
	private MetricData atfdData;
	
	public Atfd() {
		this.atfdData = new AtfdData();
	}
	
	public int getMetricValue() {
		return ((AtfdData)atfdData).getATFDValue() ;
	}
	
	public boolean greaterThanFew() {		
		return ((AtfdData)atfdData).getATFDValue() > FEW_ATFD_THRESHOLD;
	}
		
	public boolean atfdAlwaysTrue() {	
		return true;
	}
	
	@Override
	public void calculateMetric(Node node,MethodNode methodNode) {
		calculateATFDofMethod(node, methodNode);
	}
	
	private int calculateATFDofMethod(Node node,MethodNode methodNode) {
		ClassNode classNode = (ClassNode) node;
		parseMethodTree(classNode,methodNode);		
		return getMetricValue();
	}
	
	@Override
	public boolean is(Type type) {
		return this.type.equals(type);
	}
	
	public void setValue(int value) {
		((AtfdData)atfdData).setATFDValue(value);
	}
	
	public int getValue() {
		return ((AtfdData)atfdData).getATFDValue();
	}
}
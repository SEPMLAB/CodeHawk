package org.smell.metricruler;

import org.smell.astmodeler.ClassNode;
import org.smell.astmodeler.Node;

/*
 * Weighted Methods per Class
 */
public class Wmc implements Metric {
	private MetricData wmcData;
	private final Type type  = Metric.Type.WMC;
	private static final int WMC_HIGH_LEVEL = 31;
	private static final int WMC_VERY_HIGH_LEVEL = 47;
	
	public Wmc() {
		this.wmcData = new WmcData();
	}
	
	public void calculateMetric(Node node) {
		configureWeightedMethodsperClass(node);
	}
	
	private void configureWeightedMethodsperClass(Node node) {
		ClassNode classNode = (ClassNode)node;
		int wmc = classNode.getWMC();	
		setValue(wmc);		
	}

	public boolean lessThanHigh() {
		return(((WmcData)wmcData).getValue() < WMC_HIGH_LEVEL);
	}

	public boolean lessThanSuperSuperHigh() {
		return(((WmcData)wmcData).getValue() < 1000);
	}
	
	public boolean lessThanVeryHigh() {
		return(((WmcData)wmcData).getValue() < WMC_VERY_HIGH_LEVEL);
	}
	
	public void setValue(int value) {
		((WmcData)wmcData).setValue(value);

	}

	public int getValue() {
		return ((WmcData)wmcData).getValue();
	}

	@Override
	public boolean is(Type type) {
		return this.type.equals(type);
	}
}
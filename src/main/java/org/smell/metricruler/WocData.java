package org.smell.metricruler;

public class WocData implements MetricData {
	private float wocValue;
	
	public WocData() {		 
		 this.wocValue = 0.0f;
	}	
	
	public float getValue() {
		return this.wocValue;
	}
	
	public void setValue(float woc) {
		this.wocValue = woc;
	}
}
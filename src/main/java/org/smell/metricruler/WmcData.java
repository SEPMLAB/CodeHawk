package org.smell.metricruler;

public class WmcData implements MetricData {
	private int wmcValue;
	
	public WmcData() {		 
		 this.wmcValue = 0;
	}	
	
	public int getValue() {
		return this.wmcValue;
	}
	
	public void setValue(int value) {
		this.wmcValue = value;
	}
}
package org.smell.metricruler;

public class NopaAndNoamData implements MetricData {
	private int nopaValue;	
	private int noamValue;
	
	public NopaAndNoamData() {
		this.nopaValue = 0;
		this.noamValue = 0;
	}
	
	public int getNopaValue() {
		return this.nopaValue;
	}
	
	public int getNoamValue() {
		return this.noamValue;
	}
	
	public void setNoamValue(int value) {
		this.noamValue = value;
	}
	
	public void setNopaValue(int value) {
		this.nopaValue = value;
	}
}
package org.smell.metricruler;

public class AtfdData implements MetricData{
	private int atfdCounts;
	
	public AtfdData() {	 
		 this.atfdCounts = 0;
	}
	
	public void setATFDValue(int atfd) {
		this.atfdCounts = atfd;
	}
	
	public int getATFDValue() {
		return this.atfdCounts;
	}	
}
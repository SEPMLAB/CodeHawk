package org.smell.metricruler;

import java.util.HashSet;
import java.util.Set;

public class FdpData implements MetricData {
private Set<String> fdp;
	
	public FdpData() {
		 this.fdp = new HashSet<String>();
	}
	
	public void addFDP(String className) {
		 this.fdp.add(className);
	}
	
	public Set<String> getSet(){
		return this.fdp;
	}
	
	public int getMetricValue() {
		return this.fdp.size();
	}
}
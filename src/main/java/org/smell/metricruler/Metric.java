package org.smell.metricruler;

public interface Metric {
	public boolean is(Type type) ;
	
	public enum Type{
		ATFD, LAA, FDP,NOPA_AND_NOAM,WMC,WOC,FEATURE_ENVY;
	}
}
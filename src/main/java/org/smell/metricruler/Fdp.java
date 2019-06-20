package org.smell.metricruler;

import java.util.Set;

import org.smell.astmodeler.ClassNode;
import org.smell.astmodeler.MethodNode;
import org.smell.astmodeler.Node;
import org.sonar.samples.java.checks.BrokenModularizationRule;

public class Fdp extends FeatureEnvyMetric {

	private static final int FDP_THRESHOLD =  3;
	private final Type type  = Metric.Type.FDP;
	private String logPath = "D:\\test\\fdpSmell.txt";
	private MetricData fdpData;
	
	public Fdp() {
		this.fdpData = new FdpData();
	}
	
	public void addForeignDataProvider(String className) {
		((FdpData)fdpData).addFDP(className);
	}

	public int getMetricValue() {
		return ((FdpData)fdpData).getMetricValue();
	}
	
	public boolean lessThanFew() {		
		return ((FdpData)fdpData).getMetricValue()  < FDP_THRESHOLD;
	}
	
	public Set<String> getFDP() {		
		return ((FdpData)this.fdpData).getSet();
	}
		
	@Override
	public void calculateMetric(Node node, MethodNode methodNode) {
		calculateFDPofMethod(node, methodNode);
		
	}

	private void calculateFDPofMethod(Node node, MethodNode methodNode) {
		ClassNode classNode = (ClassNode) node;
		parseMethodTree(classNode,methodNode);	
		if(((FdpData)fdpData).getMetricValue() > 0) {
			String log="";
			Set<String> fdp = ((FdpData)fdpData).getSet();
			for(String classes : fdp) {
				log = log + "FDP of "+ classNode.getName() + " : " +classes + "\r\n";				
			}
			logInformation(log);
		}
	}

	private void logInformation( String log) {
		try {
			BrokenModularizationRule.logOnFile(logPath, log);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean is(Type type) {
		return this.type.equals(type);
	}
}
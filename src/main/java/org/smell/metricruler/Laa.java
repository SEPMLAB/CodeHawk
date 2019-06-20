package org.smell.metricruler;

import org.smell.astmodeler.ClassNode;
import org.smell.astmodeler.MethodNode;
import org.smell.astmodeler.Node;
import org.sonar.samples.java.checks.BrokenModularizationRule;

public class Laa extends FeatureEnvyMetric {
	private static final float lAA_THRESHOLD = (float) 1 / 3;
	private final Type type  = Metric.Type.LAA;
	private String logPath = "D:\\test\\laaSmell.txt";
	private MetricData laaData;
	
	public Laa() {
		this.laaData = new LaaData();
	}
	
	public MetricData getData() {
		return this.laaData;
	}
	
	public boolean lessThanThreshold() {
		return(((LaaData)laaData).getMetricValue() < lAA_THRESHOLD);
	}
	
	public void setMetricValue(float value) {
		((LaaData)laaData).setValue(value);
	}

	public float getMetricValue() {
		return ((LaaData)laaData).getValue();
	}
	
	private void calculateLAA(Node node,MethodNode methodNode) {
		ClassNode classNode = (ClassNode) node;		
		//clear laa parse每個方法前要先清掉之前的laa value
		((LaaData)laaData).initialize();
		 parseMethodTree(classNode,methodNode);	
		 calculateLAAofMethod();
	}
	
	private void calculateLAAofMethod() {
		int localAttributesAccessed = ((LaaData)laaData).getLocalAttributesAccessed();
		int foreignAttributesAccessed =  ((LaaData)laaData).getForeignAttributesAccessed();
		if(localAttributesAccessed > 0 || foreignAttributesAccessed > 0) {
			//轉型成float 
			float laa = ((float)localAttributesAccessed)/(localAttributesAccessed +  foreignAttributesAccessed);	
			if (laa < lAA_THRESHOLD) {
				setMetricValue(laa);		
				String log ="";
				log = log 
						//+ "LAA of "+classNode.getName() +  " : " + getMetricValue() +"\r\n"
						+ "localAttributesAccessed :  " + localAttributesAccessed +"\r\n"
						 + "foreignAttributesAccessed :  " + foreignAttributesAccessed +"\r\n"
						;
				logInformation(log);
				// 直接計算有沒有LAA小於1/3的method 如果有的話就終止迴圈 回傳true
				return;
			}	
		}		
	}
	
	@Override
	public void calculateMetric(Node node,MethodNode methodNode) {
		calculateLAA(node, methodNode);
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
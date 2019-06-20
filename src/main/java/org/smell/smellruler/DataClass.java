package org.smell.smellruler;

import org.smell.astmodeler.ClassNode;
import org.smell.astmodeler.Node;
import org.smell.metricruler.Metric;
import org.smell.metricruler.NopaAndNoam;
import org.smell.metricruler.Wmc;
import org.smell.metricruler.Woc;
import org.sonar.samples.java.checks.BrokenModularizationRule;

public class DataClass implements Smell {

	private Metric woc;
	private Metric wmc;
	private Metric nopaAndNoam;
	private static String logDataClass = "D:\\test\\DataClassSmell.txt";
	
	public DataClass() {
		initializeMetrics();
	}
	
	private void initializeMetrics() {
		this.woc= new Woc();
		this.wmc= new Wmc();
		this.nopaAndNoam= new NopaAndNoam();
	}
	
	@Override
	public boolean detected(Node node) {
		return haveDataClassSmell(node);
	}
		
	private boolean haveDataClassSmell(Node node) {
		configureMetrics(node);		
		if ( ((Woc) woc).lessThanThreshold() && 
			  ( (((NopaAndNoam)nopaAndNoam).greaterThanFew()  && ((Wmc) wmc).lessThanSuperSuperHigh()) ||
			    (((NopaAndNoam)nopaAndNoam).greaterThanMany() && ((Wmc) wmc).lessThanSuperSuperHigh()) 
			  )
		   ){
			try {
				String info = "wocLessThanThreshold : " + ((ClassNode)node).getName() + "\r\n";
				info = info +"woc : "+  ((Woc)woc).getValue() + "\r\n";
				info = info +"wmc : "+  ((Wmc)wmc).getValue()+ "\r\n";
				info = info +"NOPA : "+  ((NopaAndNoam)nopaAndNoam).getNopaValue() + "\r\n";
				info = info +"NOAM : "+  ((NopaAndNoam)nopaAndNoam).getNoamValue() + "\r\n";
				info = info +" File owner : " +((ClassNode)node).getFile() + "\r\n";
				BrokenModularizationRule.logOnFile(logDataClass, info);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			((ClassNode)node).setDataClass(this);
			return true;
		} else {
			return false;
		}
	}
		
	private void configureMetrics(Node node) {
		((Woc)this.woc).calculateMetric(node);
		((Wmc)this.wmc).calculateMetric(node);		
		((NopaAndNoam)this.nopaAndNoam).calculateMetric(node);	
	}

	public Metric getWoc() {
		return woc;
	}

	public void setWoc(Metric woc) {
		this.woc = woc;
	}

	public Metric getWmc() {
		return wmc;
	}

	public void setWmc(Metric wmc) {
		this.wmc = wmc;
	}

	public Metric getNopaAndNoam() {
		return nopaAndNoam;
	}

	public void setNopaAndNoam(Metric nopaAndNoam) {
		this.nopaAndNoam = nopaAndNoam;
	}
}
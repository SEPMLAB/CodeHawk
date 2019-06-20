package org.smell.smellruler;

import org.smell.astmodeler.Node;

public class BrokenModularization implements Smell {
	private Smell dataClass;
	private Smell featureEnvy;
	
	public BrokenModularization() {
		dataClass = new DataClass();
		featureEnvy = new FeatureEnvy();
	}
	
	@Override
	public boolean detected(Node node) {
		return haveBrokenModularization(node);		
	}
	
	private boolean haveBrokenModularization(Node node) {
		return(this.dataClass.detected(node) | this.featureEnvy.detected(node)) ;
	}
}
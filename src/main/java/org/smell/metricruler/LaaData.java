package org.smell.metricruler;

public class LaaData implements MetricData {
	private int localAttributesAccessed;
	private int foreignAttributesAccessed;
	private float value;
	
	public LaaData() {
		 this.localAttributesAccessed = 0;
		 this.foreignAttributesAccessed = 0;
		 this.value =  1.0f;
	}
		
	public void initialize() {
		 this.localAttributesAccessed = 0;
		 this.foreignAttributesAccessed = 0;
	}	
	
	//TODO for parsing used
	public float getValue() {
		return ((float)localAttributesAccessed)/(localAttributesAccessed +  foreignAttributesAccessed);	
	}
	
	//TODO for detect used
	public float getMetricValue() {
		return  this.value;	
	}
	
	public int getLocalAttributesAccessed() {
		return this.localAttributesAccessed;			
	}
	
	public int getForeignAttributesAccessed() {
		return this.foreignAttributesAccessed;			
	}
	
	public void setLocalAttributesAccessed(int i ) {
		this.localAttributesAccessed = i;
	}
	
	public void setForeignAttributesAccessed(int i ) {
		this.foreignAttributesAccessed = i;
	}

	public void localAttributesAccessedAddOne() {
		this.localAttributesAccessed = this.localAttributesAccessed +1 ;
	}

	public void foreignAttributesAccessedAddOne() {
		this.foreignAttributesAccessed = this.foreignAttributesAccessed + 1;
	}
	public void setValue(float value) {
		this.value = value;
	}
}
package org.sonar.samples.java.functioningClass;

public class DuplicationJuding {
	SmellMetrics m = new SmellMetrics();
	
	public boolean isSignificant(Duplications d) {
		if(d.type().equals("chain") && d.size() >= m.SDC)
			return true;
		else if (d.type().equals("clone") && d.size() > m.AVG)
			return true;
		
		return false;
	}
	
}

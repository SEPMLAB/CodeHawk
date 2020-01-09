package org.codehawk.plugin.java.functioningClass;

public class DuplicationJuding {
	SmellMetrics m = new SmellMetrics();
	
	public boolean isSignificant(Duplications d) {
		if(d.type().equals("chain") && d.size() >= m.maxSDC)
			return true;
		else if (d.type().equals("clone") && d.size() > m.AVG)
			return true;
		
		return false;
	}
	
}

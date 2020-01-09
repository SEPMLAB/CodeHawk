package org.smell.astmodler;

import org.smell.smellruler.Smell;
import org.smell.smellruler.Smell.Type;

public interface Smellable {
	
	public void registerSmell(Smell smell);
	
	public int getStartLine();
	
	public void setStartLine(int startLine);
	
	public Smell getRegisteredSmell(Smell.Type type);
	
	public  boolean haveSmell(Type type);
}

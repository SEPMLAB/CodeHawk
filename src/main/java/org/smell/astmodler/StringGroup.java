package org.smell.astmodler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StringGroup{
	
	private List<String> names = new ArrayList<>();
	
	public StringGroup(List<String> names) {
		this.names = names;
	}
	
	public String getString() {
		return String.join(", ", names);
	}
	
	public List<String> getNames(){
		return names;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == this)
			return true;
		
		if(!(o instanceof StringGroup))
			return false;
		
		StringGroup g = (StringGroup) o;
		
		return g.names.equals(names);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(names);
	}
}
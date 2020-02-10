package org.codehawk.smell.modler;

import java.io.File;

public interface Node {
	
	public File getFile();
	public void setFile(File file);
	public Node.Kind kind();
	boolean is(Node.Kind... kinds);
	public String getName();
	enum Kind {
		 CLASS,
		 METHOD,
		 SWITCHCASE,
		 DataMember;
	}
}

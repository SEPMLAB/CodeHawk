/**
 * This file is the sample code against we run our unit test. It is placed
 * src/test/files in order to not be part of the maven compilation.
 **/
public class AvoidUnutilizedAbstractionCheck { // Noncompliant {{It is Unutilized Abstraction}}
	int a = 0;
}

class testUnutilizedAbstraction extends A{ // compliant
	int a = 0;
	String AName;
	
	testUnutilizedAbstraction(String testA){
		super(AName);
	}
	
	public int a() {
		return 0;
	}
	
}

class A { // compliant
	int a = 0;

	public int a() {
		return 0;
	}
}
/**
 *This file is the sample code against we run our unit test.
 *It is placed src/test/files in order to not be part of the maven compilation.
 **/
public class AvoidLazyClassCheck { // Noncompliant {{Class has 0 method}}
	int a = 0;
}

class testLazyClass { // compliant
	int a = 0;
	
	public int a () {
		return 0;
	}
}

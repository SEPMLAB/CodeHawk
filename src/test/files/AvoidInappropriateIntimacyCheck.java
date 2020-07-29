/**
 * This file is the sample code against we run our unit test. It is placed
 * src/test/files in order to not be part of the maven compilation.
 **/
class AvoidInappropriateIntimacyCheck { // Noncompliant {{this class has Inappropriate Intimacy with class A}}
	private int a = 1;
	private int b = 2;
	public int c = 3;
	private static String aa = "a";
	private String bb = "b";
	public String cc = "c";
	

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		
		sc.close();
	}
	
	public int test1 () {
		test123.test1231();
		A.testA();
		return a;
	}
	
	public static String test2 () {
		test123.test1231();
		A.testA();
		return aa;
	}
	
	public String test3 () {
		test123.test1231();
		A.testA();
		return bb;
	}
}

class A { // Noncompliant {{this class has Inappropriate Intimacy with class AvoidInappropriateIntimacyCheck}}
	private static int aA = 1;
	private int bA = 2;
	public int cA = 3;
	private String aaA = "a";
	private String bbA = "b";
	public String ccA = "c";
	
	public static int testA () {
		return aA;
	}
	
	void aaa(){
		AvoidInappropriateIntimacyCheck.test2();
	}
	
	void vvv(){
		AvoidInappropriateIntimacyCheck.test2();
	}
	
	void ccc(){
		AvoidInappropriateIntimacyCheck.test2();
	}
}

class testInappropriateIntimacy { // compliant
	int a = 0;

	public int a() {
		return 0;
	}
}

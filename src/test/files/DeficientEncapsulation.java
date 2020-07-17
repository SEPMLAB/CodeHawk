/**
 * This file is the sample code against we run our unit test. It is placed
 * src/test/files in order to not be part of the maven compilation.
 **/

public class DeficientEncapsulation {// Noncompliant {{Code smell "Deficient Encapsulation" occurred in Class
										// "DeficientEncapsulation" !}}

	public int b;

	class c {
		public int c = 0;
	}

	public int jk() {
		return b;
	}

	private int a4 = 10;

}
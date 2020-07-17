public class DeficientEncapsulationCheck {// Noncompliant {{Code smell "Deficient Encapsulation" occurred in Class
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
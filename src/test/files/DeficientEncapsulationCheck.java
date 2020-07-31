public class DeficientEncapsulationCheck {// Noncompliant {{Code smell "Deficient Encapsulation" occurred in Class
											// "DeficientEncapsulation" !}}

	public int a = 1;

	public int a() {
		return a;
	}

	private int b = 10;

	public int b() {
		return b;
	}
}
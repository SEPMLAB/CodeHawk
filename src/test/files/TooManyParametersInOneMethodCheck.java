public class TooManyParametersInOneMethodCheck {

	public int method(int a1, int a2, int a3, int a4, int a5, int b1, int b2, int b3, int b4, int b5) {// Noncompliant {{There are too many parameters in this method !}}
		return a1 + a2 + a3 + a4 + a5 - b1 - b2 - b3 - b4 - b5;
	}
}
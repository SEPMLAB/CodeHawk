/**
 * This file is the sample code against we run our unit test. It is placed
 * src/test/files in order to not be part of the maven compilation.
 **/
class RefusedBequestCheck extends B{ // Noncompliant {{this class refuse bequest}}
	protected int aa = 0;

	RefusedBequestCheck(String n, int c) {
		super(n);
		super.aa = c;
	}

	public void enjoy(int aa) {
		if (true) {
			aa++;
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
			enjoy5();
		}
	}

	public void enjoy222() {
		if (true) {
			enjoy(aa);
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy21() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy4() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy5() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy621() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy72() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy58() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}
}

class C extends B { // compliant

	C(String n, String c) {
		super(n);
	}

	public void enjoy2() {
		if (true) {
			System.out.println("�s�n...e5re");
		}
		while (true) {
			System.out.println("�s�n...123");
		}
	}

}


class B { // compliant

	protected int aa = 1000000000;
	protected int bb;
	protected int cc;
	protected String name;
	protected String name2;
	protected String name3;
	protected String name4;

	B(String name) {
		this.name = name;
	}

	public void enjoy() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy2() {
		if (true) {
			System.out.println("test");
			if (true) {
				System.out.println("test");
				if (true) {
					System.out.println("test");
				}
			}
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy3() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy4() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy5() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy6() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy7() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy8() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy9() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy10() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy11() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy12() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy13() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy14() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}

	public void enjoy15() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}
	
	public void enjoy16() {
		if (true) {
			System.out.println("test");
		}
		while (true) {
			System.out.println("test");
		}
	}
}
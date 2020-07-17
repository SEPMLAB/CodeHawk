public class ShotgunSurgeryCheck {
	public static int method() {// Noncompliant {{Code smell "Shotgun Surgery" occurred in "method" !}}
		return 1;
	}

	public static int variable = 0;// Noncompliant {{Code smell "Shotgun Surgery" occurred in "variable" !}}
}

class ShotgunSurgery_A extends ShotgunSurgeryCheck {
	public static void main(String[] args) {
		int a1 = ShotgunSurgeryCheck.variable + 1;

		ShotgunSurgeryCheck.method();
	}
}

class ShotgunSurgery_B {
	public static void main(String[] args) {
		int b1 = ShotgunSurgeryCheck.variable + 1;

		ShotgunSurgeryCheck.method();
	}
}

class ShotgunSurgery_C {
	static ShotgunSurgeryCheck object = new ShotgunSurgeryCheck();

	public static void main(String[] args) {
		int c1 = object.variable + 1;

		object.method();
	}
}

class ShotgunSurgery_D {
	public static void main(String[] args) {
		int d1 = ShotgunSurgeryCheck.variable + 1;

		ShotgunSurgeryCheck.method();
	}
}

class ShotgunSurgery_E {
	public static void main(String[] args) {
		int e1 = ShotgunSurgeryCheck.variable + 1;

		ShotgunSurgeryCheck.method();
	}
}

class ShotgunSurgery_F {
	public static void main(String[] args) {
		int f1 = ShotgunSurgeryCheck.variable + 1;

		ShotgunSurgeryCheck.method();
	}
}

class ShotgunSurgery_G {
	public static void main(String[] args) {
		int g1 = ShotgunSurgeryCheck.variable + 1;

		ShotgunSurgeryCheck.method();
	}
}

class ShotgunSurgery_H {
	public static void main(String[] args) {
		int h1 = ShotgunSurgeryCheck.variable + 1;

		ShotgunSurgeryCheck.method();
	}
}

class ShotgunSurgery_I {
	public static void main(String[] args) {
		int i1 = ShotgunSurgeryCheck.variable + 1;

		ShotgunSurgeryCheck.method();
	}
}

class ShotgunSurgery_J {
	int j1 = ShotgunSurgeryCheck.variable + 1;

	int j2 = ShotgunSurgeryCheck.method() + 1;
}

class ShotgunSurgery_K {
	int k1 = ShotgunSurgeryCheck.variable + 1;

	int k2 = ShotgunSurgeryCheck.method() + 1;
}

class ShotgunSurgery_L {
	int l1 = ShotgunSurgeryCheck.variable + 1;

	int l2 = ShotgunSurgeryCheck.method() + 1;
}
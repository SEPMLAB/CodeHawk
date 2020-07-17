public class ShotgunSurgery {
	public static int method() {// Noncompliant {{Code smell "Shotgun Surgery" occurred in "method" !}}
		return 1;
	}

	public static int variable = 0;// Noncompliant {{Code smell "Shotgun Surgery" occurred in "variable" !}}
}

class ShotgunSurgery_A extends ShotgunSurgery {
	public static void main(String[] args) {
		int a1 = ShotgunSurgery.variable + 1;

		ShotgunSurgery.method();
	}
}

class ShotgunSurgery_B {
	public static void main(String[] args) {
		int b1 = ShotgunSurgery.variable + 1;

		ShotgunSurgery.method();
	}
}

class ShotgunSurgery_C {
	static ShotgunSurgery object = new ShotgunSurgery();

	public static void main(String[] args) {
		int c1 = object.variable + 1;

		object.method();
	}
}

class ShotgunSurgery_D {
	public static void main(String[] args) {
		int d1 = ShotgunSurgery.variable + 1;

		ShotgunSurgery.method();
	}
}

class ShotgunSurgery_E {
	public static void main(String[] args) {
		int e1 = ShotgunSurgery.variable + 1;

		ShotgunSurgery.method();
	}
}

class ShotgunSurgery_F {
	public static void main(String[] args) {
		int f1 = ShotgunSurgery.variable + 1;

		ShotgunSurgery.method();
	}
}

class ShotgunSurgery_G {
	public static void main(String[] args) {
		int g1 = ShotgunSurgery.variable + 1;

		ShotgunSurgery.method();
	}
}

class ShotgunSurgery_H {
	public static void main(String[] args) {
		int h1 = ShotgunSurgery.variable + 1;

		ShotgunSurgery.method();
	}
}

class ShotgunSurgery_I {
	public static void main(String[] args) {
		int i1 = ShotgunSurgery.variable + 1;

		ShotgunSurgery.method();
	}
}

class ShotgunSurgery_J {
	int j1 = ShotgunSurgery.variable + 1;

	int j2 = ShotgunSurgery.method() + 1;
}

class ShotgunSurgery_K {
	int k1 = ShotgunSurgery.variable + 1;

	int k2 = ShotgunSurgery.method() + 1;
}

class ShotgunSurgery_L {
	int l1 = ShotgunSurgery.variable + 1;

	int l2 = ShotgunSurgery.method() + 1;
}
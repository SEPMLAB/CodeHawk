public class TooManyCasesInOneSwitchCheck {
	private int level = 10;

	public TooManyCasesInOneSwitchCheck() {
		switch (level) {// Noncompliant {{Too many cases in this switch statement!(over 10)}}
		case 10:
			System.out.println("10");
			break;
		case 9:
			System.out.println("9");
			break;
		case 8:
			System.out.println("8");
			break;
		case 7:
			System.out.println("7");
			break;
		case 6:
			System.out.println("6");
			break;
		case 5:
			System.out.println("5");
			break;
		case 4:
			System.out.println("4");
			break;
		case 3:
			System.out.println("3");
			break;
		case 2:
			System.out.println("2");
			break;
		case 1:
			System.out.println("1");
			break;
		case 0:
			System.out.println("0");
			break;
		}
	}
}
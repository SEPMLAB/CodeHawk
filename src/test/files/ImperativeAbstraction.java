/**
 * This file is the sample code against we run our unit test. It is placed
 * src/test/files in order to not be part of the maven compilation.
 **/

public class ImperativeAbstraction {// Noncompliant {{Code smell "Imperative Abstraction" occurred in class
									// "ImperativeAbstraction" !}}

	public static void main(String[] args) {

		int level = 10;
		do {
			A: for (int i = 0; i < 1; i++) {
				while (level > 8) {
					if (level > 0) {
						switch (level) {
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
					} else if (level == 0) {
						switch (level) {
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
					} else {
						try {
							switch (level) {
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
						} catch (java.lang.NumberFormatException e) {
							switch (level) {
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
						} finally {
							int arr[] = { 1, 2 };
							for (@SuppressWarnings("unused")
							int element : arr) {
								switch (level) {
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
					}
					level--;
					break A;
				}
			}
		} while (level == 10);
	}
}
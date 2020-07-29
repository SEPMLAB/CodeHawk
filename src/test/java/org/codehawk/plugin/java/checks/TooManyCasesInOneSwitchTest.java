package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class TooManyCasesInOneSwitchTest {
	private static final String FILENAME = "src/test/files/TooManyCasesInOneSwitchCheck.java";
	private TooManyCasesInOneSwitch check = new TooManyCasesInOneSwitch();

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile(FILENAME).withCheck(check).verifyIssues();
	}
}
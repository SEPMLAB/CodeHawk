package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class ImperativeAbstractionRuleTest {

	private static final String FILENAME = "src/test/files/ImperativeAbstractionCheck.java";
	private ImperativeAbstraction check = new ImperativeAbstraction();

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile(FILENAME).withCheck(check).verifyIssues();
	}
}

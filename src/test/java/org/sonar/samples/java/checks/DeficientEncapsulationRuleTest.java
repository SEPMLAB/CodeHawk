package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class DeficientEncapsulationRuleTest {
	private static final String FILENAME = "src/test/files/DeficientEncapsulationCheck.java";
	private DeficientEncapsulation check = new DeficientEncapsulation();

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile(FILENAME).withCheck(check).verifyIssues();
	}
}
package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class TooManyParametersInOneMethodTest {
	private static final String FILENAME = "src/test/files/TooManyParametersInOneMethodCheck.java";
	private TooManyParametersInOneMethod check = new TooManyParametersInOneMethod();

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile(FILENAME).withCheck(check).verifyIssues();
	}
}
package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class AvoidInappropriateIntimacyCheckTest {
	private static final String FILENAME = "src/test/files/AvoidInappropriateIntimacyCheck.java";
	private AvoidInappropriateIntimacy check = new AvoidInappropriateIntimacy();

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile(FILENAME).withCheck(check).verifyIssues();
	}
	
}
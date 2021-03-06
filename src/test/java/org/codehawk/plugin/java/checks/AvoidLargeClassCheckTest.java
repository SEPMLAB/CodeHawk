package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class AvoidLargeClassCheckTest {
	private static final String FILENAME = "src/test/files/AvoidLargeClassCheck.java";
	private AvoidLargeClass check = new AvoidLargeClass();

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile(FILENAME).withCheck(check).verifyIssues();
	}
	
}
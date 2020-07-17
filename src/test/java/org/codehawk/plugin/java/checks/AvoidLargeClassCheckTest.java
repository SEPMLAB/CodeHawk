package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class AvoidLargeClassCheckTest {

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile("src/test/files/AvoidLargeClassCheck.java")
				.withCheck(new AvoidLargeClass()).verifyIssues();
	}
}
package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class AvoidLazyClassCheckTest {

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile("src/test/files/AvoidLazyClassCheck.java")
				.withCheck(new AvoidLazyClass()).verifyIssues();
	}
}
package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class AvoidMemberClumpsCheckTest {

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile("src/test/files/AvoidMemberClumpsCheck.java")
				.withCheck(new AvoidMemberClumps()).verifyIssues();
	}
}
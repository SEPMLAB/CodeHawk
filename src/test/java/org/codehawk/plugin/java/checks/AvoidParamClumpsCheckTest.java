package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class AvoidParamClumpsCheckTest {

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile("src/test/files/AvoidParamClumpsCheck.java")
				.withCheck(new AvoidParamClumps()).verifyIssues();
	}
}
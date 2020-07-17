package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class AvoidUnutilizedAbstractionCheckTest {

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile("src/test/files/AvoidUnutilizedAbstractionCheck.java")
				.withCheck(new AvoidUnutilizedAbstraction()).verifyIssues();
	}
}
package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class AvoidUnnecessaryAbstractionCheckTest {

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile("src/test/files/AvoidUnnecessaryAbstractionCheck.java")
				.withCheck(new AvoidUnnecessaryAbstraction()).verifyIssues();
	}
}
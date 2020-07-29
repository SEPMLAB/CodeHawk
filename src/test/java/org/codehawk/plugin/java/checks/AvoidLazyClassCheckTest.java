package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class AvoidLazyClassCheckTest {
	private static final String FILENAME = "src/test/files/AvoidLazyClassCheck.java";
	private AvoidLazyClass check = new AvoidLazyClass();

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile(FILENAME).withCheck(check).verifyIssues();
	}
}
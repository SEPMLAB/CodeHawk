package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class ShotgunSurgeryRuleTest {
	private static final String FILENAME = "src/test/files/ShotgunSurgeryCheck.java";
	private ShotgunSurgery check = new ShotgunSurgery();

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile(FILENAME).withCheck(check).verifyIssues();
	}
}
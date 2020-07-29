package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class RefusedBequestCheckTest {
	private static final String FILENAME = "src/test/files/RefusedBequestCheck.java";
	private RefusedBequest check = new RefusedBequest();

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile(FILENAME).withCheck(check).verifyIssues();
	}
	
}
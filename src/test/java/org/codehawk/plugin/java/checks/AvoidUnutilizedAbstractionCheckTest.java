package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class AvoidUnutilizedAbstractionCheckTest {
	private static final String FILENAME = "src/test/files/AvoidUnutilizedAbstractionCheck.java";
	private AvoidUnutilizedAbstraction check = new AvoidUnutilizedAbstraction();

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile(FILENAME).withCheck(check).verifyIssues();
	}
	
}
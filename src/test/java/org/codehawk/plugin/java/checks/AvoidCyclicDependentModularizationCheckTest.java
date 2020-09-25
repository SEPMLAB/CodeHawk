package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class AvoidCyclicDependentModularizationCheckTest {
	private static final String FILENAME = "src/test/files/AvoidCyclicDependentModularizationCheck.java";
	private AvoidCyclicDependentModularization check = new AvoidCyclicDependentModularization();

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile(FILENAME).withCheck(check).verifyIssues();
	}
	

}

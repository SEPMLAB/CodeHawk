package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class DetectSimplifiedChineseCheckTest {
	private static final String FILENAME = "src/test/files/DetectSimplifiedChineseCheck.java";
	private DetectSimplifiedChinese check = new DetectSimplifiedChinese();

	@Test
	public void detected() {
		JavaCheckVerifier.newVerifier().onFile(FILENAME).withCheck(check).verifyIssues();
	}
	
}
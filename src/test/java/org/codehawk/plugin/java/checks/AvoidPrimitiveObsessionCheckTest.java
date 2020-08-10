package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class AvoidPrimitiveObsessionCheckTest {

  @Test
  public void detected() {
    JavaCheckVerifier.newVerifier().onFile("src/test/files/AvoidPrimitveObsessionCheck.java")
        .withCheck(new AvoidPrimitiveObsession()).verifyIssues();
  }
}
package org.codehawk.plugin.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class InsufficientModularationCheckTest {
    private static final String filename = "src/test/files/InsufficientModularationCheck.java";
    private InsufficientModularation check = new InsufficientModularation();

    @Test
    public void detected() {
        JavaCheckVerifier.newVerifier().onFile(filename).withCheck(check).verifyIssues();
    }
}
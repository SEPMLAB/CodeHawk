package org.codehawk.smell.modler;

import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.plugins.java.api.JavaFileScannerContext;

public class ShotgunSurgeryNode extends SmellableNode {
    JavaFileScannerContext context;
    JavaCheck check;

    public ShotgunSurgeryNode(JavaFileScannerContext context, JavaCheck check) {
        this.context = context;
        this.check = check;
    }

    @Override
    public Kind kind() {
        return Node.Kind.CLASS;
    }

    @Override
    public String getName() {
        return null;
    }

    public JavaFileScannerContext getContext() {
        return context;
    }

    public JavaCheck getCheck() {
        return check;
    }
}

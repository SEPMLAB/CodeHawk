package org.codehawk.plugin.java;

import org.sonar.api.Plugin;

/**
 * Entry point of rules
 */
public class MyJavaRulesPlugin implements Plugin {

  @Override
  public void define(Context context) {

    // server extensions -> objects are instantiated during server startup
    context.addExtension(MyJavaRulesDefinition.class);

    // batch extensions -> objects are instantiated during code analysis
    context.addExtension(MyJavaFileCheckRegistrar.class);
  }

}

package org.smell.rule.pluginregister;

import org.sonar.api.Plugin;
import org.sonar.samples.java.checks.BrokenModularizationRule;

/**
 * Entry point of your plugin containing your custom rules
 */
public class MyJavaRulesPlugin implements Plugin {

	@Override
	public void define(Context context) {
		// server extensions -> objects are instantiated during server startup
		context.addExtension(MyJavaRulesDefinition.class);
		context.addExtension(BrokenModularizationRule.class);
		// batch extensions -> objects are instantiated during code analysis
		context.addExtension(MyJavaFileCheckRegistrar.class);
		// context.addExtension(FileSensor.class);
	}
}
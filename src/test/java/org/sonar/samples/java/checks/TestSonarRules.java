package org.sonar.samples.java.checks;

import static org.junit.Assert.*;

import org.smell.rule.pluginregister.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(value=Suite.class)
@SuiteClasses(value= {TestDataClassRule.class})
public class TestSonarRules {

	@Test
	public void test() {
		//fail("Not yet implemented");
	}

}
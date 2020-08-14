package org.codehawk.plugin.java;

import org.junit.Test;
import org.sonar.api.rules.RuleType;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.server.rule.RulesDefinition.Repository;
import org.sonar.api.server.rule.RulesDefinition.Rule;

import static org.fest.assertions.Assertions.assertThat;

public class MyJavaRulesDefinitionTest {

  @Test
  public void test() {
    MyJavaRulesDefinition rulesDefinition = new MyJavaRulesDefinition();
    RulesDefinition.Context context = new RulesDefinition.Context();
    rulesDefinition.define(context);
    RulesDefinition.Repository repository = context.repository(MyJavaRulesDefinition.REPOSITORY_KEY);

    assertThat(repository.name()).isEqualTo("Codehawk plugin Repository");
    assertThat(repository.language()).isEqualTo("java");
    assertThat(repository.rules()).hasSize(RulesList.getChecks().size());

    assertRuleProperties(repository);
    //assertParameterProperties(repository);
    //assertAllRuleParametersHaveDescription1(repository);
  }

  //private void assertParameterProperties(Repository repository) {// TooManyLinesInFunctionCheck
  //Param max = repository.rule("DeficientEncapsulation").param("name");
  //assertThat(max).isNotNull();
  //assertThat(max.type()).isEqualTo(RuleParamType.STRING);
  //}

  private void assertRuleProperties(Repository repository) {
    Rule rule = repository.rule("DeficientEncapsulation");
    assertThat(rule).isNotNull();
    assertThat(rule.name()).isEqualTo("Deficient Encapsulation");
    assertThat(rule.type()).isEqualTo(RuleType.CODE_SMELL);
  }

  //private void assertAllRuleParametersHaveDescription1(Repository repository) {
  //for (Rule rule : repository.rules()) {
  //for (Param param : rule.params()) {
  //assertThat(param.description()).as("description for " + param.key()).isNotEmpty();
  //}
  //}
  //}
}

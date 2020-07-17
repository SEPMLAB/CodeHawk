package org.codehawk.plugin.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.codehawk.plugin.java.checks.AvoidParamClumps;
import org.codehawk.plugin.java.checks.ShotgunSurgery;
import org.codehawk.plugin.java.checks.AvoidInappropriateIntimacy;
import org.codehawk.plugin.java.checks.AvoidLargeClass;
import org.codehawk.plugin.java.checks.AvoidLazyClass;
import org.codehawk.plugin.java.checks.AvoidMemberClumps;
import org.codehawk.plugin.java.checks.AvoidPrimitiveObsession;
import org.codehawk.plugin.java.checks.AvoidUnnecessaryAbstraction;
import org.codehawk.plugin.java.checks.AvoidUnutilizedAbstraction;
import org.codehawk.plugin.java.checks.DeficientEncapsulation;
import org.codehawk.plugin.java.checks.ImperativeAbstraction;
import org.sonar.plugins.java.api.JavaCheck;
import org.codehawk.plugin.java.checks.RefusedBequest;

public final class RulesList {

  private RulesList() {
  }

  public static List<Class<? extends JavaCheck>> getChecks() {
    List<Class<? extends JavaCheck>> checks = new ArrayList<>();
    checks.addAll(getJavaChecks());
    checks.addAll(getJavaTestChecks());
    return Collections.unmodifiableList(checks);
  }

  public static List<Class<? extends JavaCheck>> getJavaChecks() {
    return Collections.unmodifiableList(Arrays.asList(AvoidLargeClass.class, RefusedBequest.class, AvoidLazyClass.class,
        AvoidParamClumps.class, AvoidMemberClumps.class, ShotgunSurgery.class, AvoidInappropriateIntimacy.class,
        AvoidPrimitiveObsession.class, AvoidUnnecessaryAbstraction.class, AvoidUnutilizedAbstraction.class,
        ImperativeAbstraction.class, DeficientEncapsulation.class));
  }

  public static List<Class<? extends JavaCheck>> getJavaTestChecks() {
    return Collections.emptyList();
  }
}

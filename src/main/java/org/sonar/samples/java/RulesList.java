/*
 * SonarQube Java Custom Rules Example
 * Copyright (C) 2016-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.samples.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.sonar.plugins.java.api.JavaCheck;
import org.sonar.samples.java.checks.AvoidDataClumps;
import org.sonar.samples.java.checks.AvoidLargeClass;
import org.sonar.samples.java.checks.AvoidLazyClass;
import org.sonar.samples.java.checks.AvoidTooManyCasesInOneSwitch;
import org.sonar.samples.java.checks.AvoidTooManyParametersInOneMethod;
import org.sonar.samples.java.checks.AvoidShotgunSurgery;
<<<<<<< HEAD
import org.sonar.samples.java.checks.TestRule;
=======
>>>>>>> 2aa38c9e29a232c4ac23effe890c312120239d07
import org.sonar.samples.java.checks.AvoidLargeMethod;
import org.sonar.samples.java.checks.RefusedBequest;

public final class RulesList {

	private RulesList() {
	}

	public static List<Class<? extends JavaCheck>> getChecks() {
		List<Class<? extends JavaCheck>> checks = new ArrayList<>();
		checks.addAll(getJavaChecks());
		checks.addAll(getJavaTestChecks());
		return Collections.unmodifiableList(checks);
	}

<<<<<<< HEAD
  public static List<Class<? extends JavaCheck>> getJavaChecks() {
    return Collections.unmodifiableList(Arrays.asList(
      AvoidLargeClass.class,AvoidLazyClass.class,AvoidLargeMethod.class,AvoidDataClumps.class,AvoidTooManyParametersInOneMethod.class,AvoidTooManyCasesInOneSwitch.class,TestRule.class));
  }
=======
	public static List<Class<? extends JavaCheck>> getJavaChecks() {
		return Collections.unmodifiableList(Arrays.asList(AvoidLargeClass.class, RefusedBequest.class,
				AvoidLazyClass.class, AvoidLargeMethod.class, AvoidTooManyParametersInOneMethod.class,
				AvoidDataClumps.class, AvoidTooManyCasesInOneSwitch.class, AvoidShotgunSurgery.class));
	}
>>>>>>> 2aa38c9e29a232c4ac23effe890c312120239d07

	public static List<Class<? extends JavaCheck>> getJavaTestChecks() {
		return Collections.emptyList();
	}
}

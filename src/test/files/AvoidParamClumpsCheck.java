/*
 * This file is the sample code against we run our unit test. It is placed
 * src/test/files in order to not be part of the maven compilation.
 */
class AvoidParamClumpsCheck {

  public void m1(int p, Integer p1, double p2, double p3) {// Noncompliant {{Avoid dataClump for group: p, p1, p2}}

  }

  public void m2(Test5 p, Test3 p1, double p2) {// Noncompliant {{Avoid dataClump for group: p, p1, p2}}

  }

  public void m3(int p, int p1, double p2) {// Noncompliant {{Avoid dataClump for group: p, p1, p2}}

  }

  // compliant
  public void m4(int p, int p1, float p4) {

  }

}

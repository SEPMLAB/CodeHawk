/*
 * This file is the sample code against we run our unit test. It is placed
 * src/test/files in order to not be part of the maven compilation.
 */
public class AvoidPrimitveObsessionCheck {

  void m1(A a, B b, int c) {
  }

  void m2(D d, int e) {
  }
}

class NonCompliantCheck {

  void m1(double a, Integer b, int c, D d) {// Noncompliant {{Using too many primitive type prameters}}
  }

  void m2(D d, int e) {
  }
}
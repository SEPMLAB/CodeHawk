/*
 * This file is the sample code against we run our unit test. It is placed
 * src/test/files in order to not be part of the maven compilation.
 */
class AvoidMemberClumpsCheck {// Noncompliant {{Avoid dataClump for group: a, b, c, d, e, f, g, h, i, j}}
  int a = 0;
  int b = 1;
  int c = 2;
  int d = 3;
  int e = 4;
  int f = 5;
  int g = 6;
  int h = 7;
  int i = 8;
  int j = 9;
}

// over ten fields of class duplicated
class NonCompliantClump {// Noncompliant {{Avoid dataClump for group: a, b, c, d, e, f, g, h, i, j}}
  int a = 0;
  int b = 1;
  int c = 2;
  int d = 3;
  int e = 4;
  int f = 5;
  int g = 6;
  int h = 7;
  int i = 8;
  int j = 9;
}

// not enough duplicated fields
class CopmliantClass {
  int g = 6;
  int h = 7;
  int i = 8;
  int j = 9;
}
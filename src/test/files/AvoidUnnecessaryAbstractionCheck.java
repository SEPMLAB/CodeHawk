/*
 * This file is the sample code against we run our unit test. It is placed
 * src/test/files in order to not be part of the maven compilation.
 */
public class AvoidUnnecessaryAbstractionCheck {// Noncompliant {{abstract classes should at least one method and 6 fields.}}
  int foo = 1;
  String bar = "foobar";
  int x = 2;
  int y = 3;
  int z = 4;
}

class CompliantCheck1 {
  int foo = 1;
  String bar = "foobar";
  int x = 2;
  int y = 3;
  int z = 4;
  int a = 5;
}

class CompliantCheck2 {
  public void m1() {

  }
}
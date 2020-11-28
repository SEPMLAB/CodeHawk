/*
 * This file is the sample code against we run our unit test. It is placed
 * src/test/files in order to not be part of the maven compilation.
 */
class AvoidCyclicDependentModularizationCheck {
	
}

class A {
	
	A(){
		
	}
	
	B bb = new B();
	
}

class B {// Noncompliant {{Class B has Cyclic Dependent with class A}}
	
	B(){
		A a = new A();
	}
	
}

class C{
	C(){
		D d = new D();
	}
}

class D{// Noncompliant {{Class D has Cyclic Dependent with class C}}
	D(){
		
	}
	
	C c = new C();
}

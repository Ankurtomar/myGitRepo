package overriding;

class A {
void m1(A a) {}}
class B extends A {void m1(B b) {System.out.print("B");}}
class C extends B {void m1(C c) {System.out.print("C");}}
class D {
	D() throws Exception{System.out.print("A");
if(true) throw new Exception();
}
  public static void main(String[] args) {
    	A a1;

	a1 = new A();
 A b1 = new B(); A c1 = new C(); C c4 = new C();
    a1.m1(c4); b1.m1(a1); c1.m1(c4);
	try{
		D dd=new D();
	}catch(Exception ee){
		System.out.println("get exception");
}
}}

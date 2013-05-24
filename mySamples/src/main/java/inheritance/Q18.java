package inheritance;

class A {String s1="A";}
class B extends A {String s1="B";}
class C extends B {String s1="C";}
class D extends C {
  String s1="D";
  void m1() {
    System.out.print(this.s1  + ",");      // 1
    System.out.print(((C)this).s1  + ","); // 2
    System.out.print(((B)this).s1  + ","); // 3
    System.out.print(((A)this).s1);        // 4
  }
  public static void main (String[] args) {
    new D().m1(); // 5
}}



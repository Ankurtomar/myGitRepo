package inheritance;

class A{
  A() {System.out.print("CA ");}
  static {System.out.print("SA ");}
}
class B extends A {
  B() {System.out.print("CB ");}
  static {System.out.print("SB ");}
  public static void main (String[] args) {B b = new B();}
}

package inheritance;

interface I {String s1 = "I";}
class A implements I {String s1 = "A";}
class B extends A {String s1 = "B";}
class C extends B {
  String s1 = "C";
  void printIt() {
    System.out.print(((A)this).s1 + ((B)this).s1 +
                     ((C)this).s1 + ((I)this).s1);
  }
  public static void main (String[] args) {new C().printIt();}
}

package Interface;

interface A {
	  String m1();
	  class B implements A {public String m1() {return "B";}}
	}
	class I26 {
	  public static void main(String[] args) {
		  System.out.print(new A.B().m1()); 
	  }
	}


package Interface;

	interface I10 {
		    String name = "I10";
		    String s10 = "I10.s10";
		  }
		  interface I20 {
		    String name = "I20";
		    String s20 = "I20.s20";
		  }
		  class I24 implements I10, I20 {
		  public static void main(String[] args) {
		     System.out.print(I10.s10+",");
		     System.out.print(I20.s20+",");
		    System.out.print(I20.name);
		   }
		 }



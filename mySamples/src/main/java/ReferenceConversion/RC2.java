package ReferenceConversion;

interface I1 {}
  interface I2 {}
  class Base implements I1 {}
  class Sub extends Base implements I2 {}
  class Silver {
    public static void main(String []args) {
      Base[] base = {new Base()};
      Sub sub[] = new Sub[1];
      Object obj = base;
     sub = (Sub[])obj;
     I1 []i1 = (I1[])obj;
   }
 }
		
	
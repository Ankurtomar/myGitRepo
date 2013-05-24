package ReferenceConversion;

interface I1 {}
interface I2 {}
class Base implements I1 {}
class Sub extends Base implements I2 {}
class Yellow {
  public static void main(String args[]) {
    Base base = new Sub();
    I1 i1 = base;
     Sub sub = (Sub)base;
     I2 i2 = (Sub)base;
   }
}

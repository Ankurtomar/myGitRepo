package ReferenceConversion;

class A{
	int a=10;
	}

class B extends A{
	int b=20;
}


class RCtest{
	public static void main(String[] args){
		A a=new B();
		System.out.println(a.a+"   "+a.b);
		
		B b=new A();
		}}
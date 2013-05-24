package ReferenceConversion;

class A{
	int a=10;
	int b=20;
	}

class B extends A{
	int b=30;
}


class RCtest{
	public static void main(String[] args){
		A a=new B();
		//B b=new B();
		System.out.println(a.a+"   "+a.b);
		
		//B b=new A();
		}}
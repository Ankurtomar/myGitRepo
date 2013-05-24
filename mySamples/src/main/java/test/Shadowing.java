class A{
	public static void AA(){
		System.out.println("A static method");
}
}

class B extends A{
	public static void AA(){
		System.out.println("B static method");
}}


class C{

	public static void main(String[] args){

		A ob=new B();
		ob.AA();
		//B ob1=new A();		//Error due to incompatible types as sub class cannot cast into super class
		//ob1.AA();

}
}
package B;

import A.BroadingAccess;

class B extends BroadingAccess{
	public void AB(){
		A();
		System.out.println("A");
		System.out.println(f);
}}

class C{
	public static void main(String[] args){
		//BroadingAccess a= new BroadingAccess(); 
		//a.A();
		B ob=new B();
		ob.AB();
		ob.A();

		
}}
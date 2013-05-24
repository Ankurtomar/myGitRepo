package AccessModifiers;

class PrivateClass{
	private void Display(){
		System.out.println("In private");
}
}

class myPrivate{
	public static void main(String[] args){
		PrivateClass ob=new PrivateClass();
		ob.Display();
}}
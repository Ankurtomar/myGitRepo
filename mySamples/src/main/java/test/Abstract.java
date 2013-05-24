abstract class AbstractClass{
	abstract void A();
	abstract void B();
	public final void C(){
		System.out.println("C");
}
}

class SubAbstract extends AbstractClass{
	void B(){}
	void A(){
		System.out.println("A");
		}
/*	public void C(){
		System.out.println("C");
}
*/
}


class RunAbstract{
	public static void main(String[] args){
		SubAbstract ob=new SubAbstract();
		ob.A();
		ob.C();
	}}
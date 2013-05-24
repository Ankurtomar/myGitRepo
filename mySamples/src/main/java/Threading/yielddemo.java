package Threading;

import print.Print;



class A implements Runnable{
	Thread t;
	
	A(String name){
		t=new Thread(this,name);
	}
	
	A(String name,int p){
		t=new Thread(this,name);
		t.setPriority(p);
	}
	
	public void run(){
		for(int i=0;i<5;i++){
			new Print(t.getName()+" "+i);
			t.yield();
		}
	}
	
	public void start(){
		t.start();
	}
	
	public void yield(){
		t.yield();
	}
}

class B implements Runnable{
	A a1,a2;
	Thread t;
	
	B(){
		t=new Thread(this);
		a1=new A("a1");
		a2=new A("a2");
		t.start();
	}
	
	public void run(){
		a1.start();
		a2.start();
		//a1.yield();
		for(int j=0;j<5;j++){
			new Print("B "+j);
		}
	}
}
public class yielddemo {
	public static void main(String[] args){
		B b=new B();
	}

}

package Threading;

import print.Print;

class A1 implements Runnable{
	Thread t;
	static int j;
	A1(String n){
		t=new Thread(this,n);
		t.start();
	}
	public void run(){
		for(int i=0;i<15;i++){
			if(i%3==0){
				new Print(t.getName()+" "+j++);
			}}
	}
}

public class staticVar {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		A1 a1,a2,a3;
		a1=new A1("a1");
		a2=new A1("a2");
		a3=new A1("a3");
		
	}

}

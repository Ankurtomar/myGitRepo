package Threading;

import print.Print;


class AB {
	Thread t;
	
	AB(){
		t=new Thread();
	}
	
	public void run(){
		for(int i=0;i<5;i++){
			new Print(i);
		}
	}
}

public class ThirdMethod {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		AB a=new AB();
		a.t.start();
		new Print("in main");
	}

}

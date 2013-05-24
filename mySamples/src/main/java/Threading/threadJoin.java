package Threading;

import print.Print;

public class threadJoin implements Runnable{
	Thread t;
	
	threadJoin(){
		t=new Thread(this);t.start();
	}
	
	Thread getThread(){
		return t;
	}
	
	public void run(){
		try{
			new Print("going to sleep");
			Thread.sleep(10000);
		}catch(InterruptedException e){
			new Print("interrupt");
		}
	}
	
	public static void main(String[] args){
		threadJoin tt=new threadJoin();
		try{
				
			 tt.getThread().join(2000);
			 new Print("calling join");
			 
		}catch(InterruptedException e){
			new Print("interrupted 2");
		}finally{
			new Print(tt.getThread().isAlive());
		}
	}
}

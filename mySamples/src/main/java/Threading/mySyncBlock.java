//Synchronized Block

//Priority works only first time rest of the time placement at stack works 

package Threading;

import print.Print;


public class mySyncBlock {
	
	public static void main(String args[]) {
		Share s = new Share();
		new M(s, "Thread1",10);
		new M(s, "Thread2",2);
		new M(s, "Thread3",5);
		new M(s,"Thread4",7);
	}

}

class M extends Thread {
	Share s;

	M(Share s, String str,int i) {
		super(str);
		this.s = s;
		this.setPriority(i);
		start();

	}

	public void run() {

		s.getShare(Thread.currentThread().getName());

	}
}

class Share {

	public void getShare(String str) {
		new Print(str+" waiting");
		synchronized (this)
		{
			new Print(str+" received");
			
			for (int i = 0; i < 2; i++) {

				System.out.println(str);
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				} 

			}

		}
	}

}
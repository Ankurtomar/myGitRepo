//Main thread can end before child thread

package Threading;

class NewThread implements Runnable {
	Thread t,main;

	NewThread(Thread main) {
		t = new Thread(this, "ChildThread");
		this.main=main;
		System.out.println("Child Thread:" + t);
		t.start();
	} 

	public void run() {
		System.out.println("Child Thread Started");
		for (int i = 0; i < 5; i++) {
			try {
				Thread.sleep(500);
				System.out.println("main.isAlive() - "+main.isAlive());
			} catch (InterruptedException e) {
				System.out.println("hehe");
			}
		}
		System.out.println("Exiting the child thread");
	}
}

public class myThread {
	
	public static void main(String args[]) {
		new NewThread(Thread.currentThread());
		System.out.println("Main thread Started");
		try{
			Thread.currentThread().stop();
		}catch(Exception e){
			System.out.println("Cannot stop");
		}
		finally {
			System.out.println(Thread.currentThread().getName());
			System.out.println("Exiting the main thread");
		}
	}
}
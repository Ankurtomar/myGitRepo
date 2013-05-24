package Threading;

class My extends Thread {
	public void run() {
		for (int i = 0; i < 5; i++) {
			System.out.println("child thread");
			Thread.currentThread().yield();
			//Thread.yield();
		}
	}
}

public class useyield {
	public static void main(String a[]) {
		My t = new My();
		t.start();
		for (int i = 0; i < 5; i++) {
			System.out.println("main thread");
			//Thread.currentThread().yield();
		}
	}}
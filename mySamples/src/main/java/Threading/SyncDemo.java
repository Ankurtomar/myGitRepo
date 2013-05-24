package Threading;

class Display1 {
	public synchronized void usingPen(String name) {

		// for(int i=0;i<3;i++)
		// {
		System.out.println(name + " using pen");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		System.out.println(name + " releasing pen");
	}

}

class MyThread12 extends Thread {
	Display1 d;
	String name;

	MyThread12(Display1 d, String name) {
		this.d = d;
		this.name = name;
	}

	public void run() {
		d.usingPen(name);
	}
}

class SyncDemo {
	public static void main(String a[]) {
		Display1 d1 = new Display1();
		MyThread12 t1 = new MyThread12(d1, "java");
		MyThread12 t2 = new MyThread12(d1, "Sun");
		MyThread12 t3 = new MyThread12(d1, "HCL");
		MyThread12 t4 = new MyThread12(d1, "Student");

		t1.start();
		t2.start();

		t3.start();
		t4.start();
	}
}

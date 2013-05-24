package Threading;

//Resource occupied = true;
//Resource free/Content not available=false

import print.Print;

class Resource {
	private String name;
	private boolean flag = false;
	private static int n;

	Resource(String s) {
		name = s;
	}

	public synchronized void Produce() {
		if (!flag) {
			try {
				new Print("Producer " + ++n + " ");
				wait();
			} catch (Exception e) {
				new Print(name + " Unable to produce " + e);
			}
		}

		flag = false;
		notify();
	}

	public synchronized void Consume() {
		if (flag) {
			try {
				new Print("Consumer " + --n + " ");
				wait();
				new Print("C "+System.currentTimeMillis());
			} catch (Exception e) {
				new Print(name + " Unable to consume " + e);
			}
		}
		flag = true;
		notify();
	}
}

class Producers implements Runnable {
	Thread t;
	Resource res;

	Producers(Resource r, String s) {
		t = new Thread(this, s);
		res = r;
		t.start();
	}

	public void run() {
		for (int i = 0; i < 10; i++) {
			res.Produce();

		}
	}
}

class Consumers implements Runnable {
	Thread t;
	Resource res;

	Consumers(Resource r, String s) {
		t = new Thread(this, s);
		res = r;
		t.start();
	}

	public void run() {
		for (int j = 0; j < 10; j++) {
			res.Consume();
		}
	}
}

public class ObjectLock {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Producers t1;
		Consumers t2;
		Resource r = new Resource("Thread");
		t1 = new Producers(r, "t1");
		t2 = new Consumers(r, "t2");
	}
}

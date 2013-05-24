package Threading;

class Q {

	private static int n;

	private static Boolean valueSet = false;

	synchronized int get() {
		if (!valueSet)
			try {
				Thread.sleep(100);
				wait();
			} catch (InterruptedException e) {
				System.out.println("InterruptedException caught");
			}
		valueSet = false;
		System.out.println("Got: " + --n + " " + valueSet);

		notify();
		return n;
	}

	synchronized void put() {
		if (valueSet)
			try {
				Thread.sleep(100);
				wait();
			} catch (InterruptedException e) {
				System.out.println("InterruptedException caught");
			}
		valueSet = true;
		System.out.println("Put: " + ++n + " " + valueSet);
		notify();
	}
}

class Producer implements Runnable {
	Q q;

	Producer(Q q) {
		this.q = q;
		new Thread(this, "Producer").start();
	}

	public void run() {
		while (true) {
			q.put();
		}
	}
}

class Consumer implements Runnable {
	Q q;

	Consumer(Q q) {
		this.q = q;
		new Thread(this, "Consumer").start();
	}

	public void run() {
		while (true) {
			q.get();
		}
	}
}

class WaitNotify {
	public static void main(String args[]) {
		Q q = new Q();

		new Producer(q);
		new Consumer(q);
		new Producer(q);
		new Consumer(q);
		new Producer(q);
		new Consumer(q);
		new Producer(q);
		new Consumer(q);

		System.out.println("Press Control-C to stop.");
	}
}
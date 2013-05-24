package myTest.Tool;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

public class myGoogleGuice {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Injector injector = Guice.createInjector(new AppModuleMust());
        for(int i=0;i<5;i++){
        	injector.getInstance(Runnable.class);
        }
	}

}

interface ImyThread {
	
}

class myThread implements ImyThread, Runnable{

	private ITask task;
	private Thread t;
	
	@Inject
	myThread(ITask task){
		this.task = task;
		t = new Thread(this);
		t.start();
	}
	
	@Override
	public void run() {
		task.doTask();
	}
}
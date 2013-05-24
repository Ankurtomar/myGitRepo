package myTest.Tool;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class AppModuleMust extends AbstractModule {

	@Override
	protected void configure() {
		
		//Singleton objects in jvm heap.
		bind(ITask.class).to(Task.class).in(Singleton.class);
		
		//User of singleton objects.
		bind(Runnable.class).to(myThread.class);
		bind(ImyThread.class).to(myThread.class);
		
	}

}

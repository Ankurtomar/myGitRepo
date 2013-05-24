package org.anks2089.java.Thread;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class myCallableExample {

	
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ExecutorService pool = Executors.newSingleThreadExecutor();//Executors.newFixedThreadPool(3);
		Set<Future<Object>> set = new HashSet<Future<Object>>();
		
		for(int i=0;i<10;i++){
			Callable<Object> callable = new myThread(i,0);
		    Future<Object> future = pool.submit(callable);
		    set.add(future);
		}
		
		for (Future<Object> future : set) {
		      System.out.println(System.currentTimeMillis() + " ------ " + (Integer)future.get());
		    }
	}

}

class myThread implements Callable{

	private int a,b;
	
	myThread(final int a, final int b){
		this.a=a;
		this.b=b;
	}

	public Object call() throws Exception {
		//Thread.currentThread().sleep(1000);
		return a+b;
		
	}	
	
}

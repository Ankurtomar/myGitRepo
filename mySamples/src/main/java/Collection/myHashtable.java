//Hashtable - Synchronized and not Ordered
//HashMap - Not Synchronized and not Ordered
//TreeMap - Sorted only

package Collection;

import java.util.Enumeration;
import java.util.Hashtable;

import print.Print;


class Threads implements Runnable{
	private Thread t;
	private Hashtable h;
	private static int key,val;
	Threads(Hashtable h,String s){
		t=new Thread(this,s);
		this.h=h;
		t.start();
	}
	public void run(){
		for(int i=0;i<5;i++)
			h.put(++key +" "+Thread.currentThread().getName(), ++val);
	}
}
public class myHashtable {

	Hashtable h;
	Enumeration e;
	Threads t1,t2;
	String key;
	Object o;
	myHashtable(){
		h=new Hashtable();
		t1=new Threads(h,"T1");
		t2=new Threads(h,"T2");
	}
	void print(){
		e=h.keys();
		while(e.hasMoreElements()){
			key=(String) e.nextElement();
			new Print(key+" "+h.get(key));
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		myHashtable ht=new myHashtable();
		Thread.sleep(2000);
		ht.print();
	}

}

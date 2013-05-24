//Priority works only first time rest of the time placement at stack works 

package Threading;

public class PriorityCase implements Runnable{

	private Thread t;
	
	PriorityCase(String name,int priority){
		t=new Thread(this,name);
		t.setPriority(priority);
		t.start();
	}
	
	public static void main(String[] args) {
		PriorityCase p1,p2,p3;
		p1=new PriorityCase("Arvind",10);
		p2=new PriorityCase("Ankit",6);
		p3=new PriorityCase("Saurabh",1);
		
		
	}

	public synchronized void print(){
		System.out.println(/*t.getId()+" "+*/t.getName()+" "+t.getPriority());
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for(int i=0;i<5;i++){
			print();
			/*try{
				t.sleep(1000);
			}catch(InterruptedException e){
				
			}*/
		}
	}

}

package Threading;

class SynThread1
{
    synchronized void call()
    {
	System.out.println("first statement");
	try
	{
   	  Thread.sleep(1000);
	}
	catch(Exception e)
	{
	  System.out.println("Error " + e);
        }
	System.out.println("second statement");
   }
}
class SynThread2 extends Thread
{
	SynThread1 t;
   	public SynThread2(SynThread1 t)
	{
		this.t = t;
   	}
   	public void run()
	{
		t.call();
   	}
}
public class SychronizedThreads
{
	public static void main(String args[])
	{
		SynThread1 obj1 = new SynThread1();
		SynThread2 Obja = new SynThread2(obj1);
	   SynThread2 Objb = new SynThread2(obj1);
	   Obja.start();
	   Objb.start();
        }}
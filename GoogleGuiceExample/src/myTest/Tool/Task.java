package myTest.Tool;

public class Task implements ITask {

	private int i = 0;
	
	@Override
	public void doTask() {
		i++;
		System.out.println("Now value of i is : " + i);
	}

}

package Collection;

import java.util.Iterator;
import java.util.Vector;

public class myVector{
	public static void main(String[] args) {
		Vector<String> vv = new Vector<String>();
		vv.add("Ankit");
		vv.add("Ankush");
		vv.add("Arvind");
		vv.add("Amit");
		vv.add("Ayush");
		

		System.out.println("Vector Size " + vv.size());
		System.out.println("Vector Capcity " + vv.capacity());
		System.out.println("Vector Size " + vv.firstElement());
		System.out.println("Vector Size " + vv.remove(1));

		// Using Iterator Interface

		Iterator<String> i = vv.iterator();

		System.out.println("Vector Size " + vv.size());
		while (i.hasNext()) {
			System.out.println("Using Iterator Interface in  Vector "
					+ i.next());
		}

		i.remove();
		System.out.println("Vector Size " + vv.size());

		i=vv.iterator();
		while (i.hasNext()) {
			System.out.println("Using Iterator Interface in  Vector "
					+ i.next());
		}

		

	}

	
}

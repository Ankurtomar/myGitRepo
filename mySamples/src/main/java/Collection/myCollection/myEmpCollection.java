package Collection.myCollection;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import print.Print;

public class myEmpCollection {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List s=new LinkedList();
		
		s.add(new Emp());
		s.add(new Emp(-2323));
		s.add(new Emp(100));
		s.add(new Emp(52));
		
		ListIterator il=s.listIterator();
		while(il.hasNext()){
			new Print(il.next().toString());
		}
		
		Collections.sort(s,new EmpComparator());
		
		il=s.listIterator();
		while(il.hasNext()){
			new Print(il.next().toString());
		}
		
	}

}

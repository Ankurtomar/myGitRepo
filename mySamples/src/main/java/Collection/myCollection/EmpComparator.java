package Collection.myCollection;

import java.util.Comparator;

public class EmpComparator implements Comparator<Emp>{

	@Override
	public int compare(Emp o1, Emp o2) {
		
		return o1.compareTo(o2);
	}

	
}

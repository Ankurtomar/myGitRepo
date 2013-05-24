//TreeSet - its sorted and no duplicate value will be return

package Collection;

import java.util.*;

import print.Print;

public class myTreeSet {

	public static void main(String[] args) {
		SortedSet<String> l = new TreeSet<String>();
		l.add("Bull");
		l.add("Cult");
		l.add("Bullshit");
		l.add("bicycle");
		l.add("Bullish");
		l.add("Zohan");
		l.add("Fat");
		l.add("King");
		l.add("12");
		l.add("/*-+");
		l.add(" ");
		l.add("\t");

		Iterator i = l.iterator();
		while (i.hasNext()) {
			new Print(i.next());
		}
	}
}

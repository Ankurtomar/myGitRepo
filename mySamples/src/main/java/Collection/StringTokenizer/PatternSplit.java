//If it doesn't find pattern or specified character it will raise exception

package Collection.StringTokenizer;

import java.util.StringTokenizer;

import print.Print;

public class PatternSplit {

	private static String s = "Name=Anks/test;status=training/train;profile=s/w engineer";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StringTokenizer st = new StringTokenizer(s, ";=/");
		while (st.hasMoreTokens()) {
			new Print(st.nextElement() + "\t" + st.nextToken() + "\t"
					+ st.nextToken());
			new Print(st.countTokens());
		}
	}
}
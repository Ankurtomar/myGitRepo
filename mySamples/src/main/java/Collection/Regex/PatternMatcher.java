package Collection.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import print.Print;

public class PatternMatcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Pattern p=Pattern.compile("00110");			//patter to be match
		Matcher m=p.matcher("0101010101010101101101001100101101101101011011011010001101101110110110101101001100110110101"); //Input in which pattern to be match
		while(m.find()){
			new Print(m.start());		//gives starting position where it is found
		}
	}

}

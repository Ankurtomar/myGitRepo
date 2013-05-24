package DateCalenderCurrency;

import java.text.NumberFormat;
import java.util.Locale;

import print.Print;

public class myCurrency {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double f= 1234.455;
		Locale[] l;
		
		NumberFormat nf=NumberFormat.getCurrencyInstance();
		new Print(nf.getCurrency() +" "+nf.format(f));
		
		l=NumberFormat.getAvailableLocales();
		for(int i=0;i<l.length;i++){
			nf=NumberFormat.getCurrencyInstance(l[i]);
			new Print(l[i]+"\t"+nf.format(f));
		}
	}

}

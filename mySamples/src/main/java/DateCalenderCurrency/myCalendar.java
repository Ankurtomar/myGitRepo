//Calendar is abstract class

package DateCalenderCurrency;

import java.util.Calendar;

import print.Print;

public class myCalendar {

	/**
	 * @param args
	 */
	Calendar c;
	
	myCalendar(){
		c=Calendar.getInstance();
	}
	
	Calendar getCalendar(){
		return c;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Print(new myCalendar().getCalendar());

	}

}

package DateCalenderCurrency;

import java.util.Date;

import print.Print;

public class myDate {

	
	Date dd;
	myDate(){
		dd=new Date();
		dd.setTime(System.currentTimeMillis());
	}
	Date getDate(){
		return dd;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		myDate date=new myDate();
		new Print(date.getDate());
		new Print(date.getDate().after(new Date(12,12,2004)));
	}

}

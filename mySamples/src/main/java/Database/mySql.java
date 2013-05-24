package Database;

import java.sql.ResultSet;
import java.sql.SQLException;

import print.Print;



public class mySql {

	/**
	 * @param args
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		myDB db=new myDB("com.mysql.jdbc.Driver","jdbc:mysql://localhost:3306/mydb","root","root");
		ResultSet rs=db.executeQuery("select * from mydb.identity");
		
		while(rs.next()){
			new Print(rs.getString(1)+" "+rs.getString(2));
		}
	}

}

package Database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import print.Print;

public class myDB {

	private Connection con;
	private Statement st;
	private ResultSet rs;
	private String user,pass;
	
	myDB(String Driver,String dsn) throws ClassNotFoundException, SQLException{
		this(Driver,dsn,null,null);
	}
	
	myDB(String Driver,String dsn,String user,String pass) throws ClassNotFoundException, SQLException{
		this.user=user;
		this.pass=pass;
		Class.forName(Driver);
		con=DriverManager.getConnection(dsn,user,pass);
		st=con.createStatement();	
	}
	
	public ResultSet executeQuery(String query) throws SQLException{
		return st.executeQuery(query);
	}
	
	public DatabaseMetaData getDBMetaData() throws SQLException{
		return con.getMetaData();
	}
	
	public ResultSetMetaData getResultMetaData() throws SQLException{
		return rs.getMetaData();
	}
	
	public boolean excute(String query) throws SQLException{
		return st.execute(query);
	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		// TODO Auto-generated method stub
		ResultSet rs;
		myDB ob=new myDB("sun.jdbc.odbc.JdbcOdbcDriver","jdbc:odbc:myDB");
		//rs=ob.executeQuery("Select * from Identity");
		rs=ob.executeQuery("create table employee (P_Id int,LastName varchar(255),FirstName varchar(255),Address varchar(255),City varchar(255))");
		while(rs.next()){
			new Print(rs.getString(1)+" "+rs.getString(2));
		}
		
	}

}

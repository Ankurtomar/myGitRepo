package IO.Serialization;

import java.io.FileNotFoundException;
import java.io.IOException;

import print.Print;

public class mySerialization {

	/**
	 * @param args
	 */
	
	private Emp e,e2;
	private ObjectReader or;
	private ObjectWriter ow;
		
	public mySerialization() throws FileNotFoundException, IOException, ClassNotFoundException{
		e=new Emp(1,22,"Anks");
		ow=new ObjectWriter("Object.ob",e);
		or=new ObjectReader("Object.ob");
		e2=(Emp) or.getObject();
		new Print(e2.toString());
	}
	
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		// TODO Auto-generated method stub
		new mySerialization();
	}

}

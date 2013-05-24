package IO.Serialization;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;

public class ObjectReader {
	
	private ObjectInputStream in;
	private Object o;
	
	public ObjectReader(String filename) throws FileNotFoundException, IOException, ClassNotFoundException{
		in=new ObjectInputStream(new FileInputStream(filename));
		o=in.readObject();
	}
	
	public Object getObject(){
		return o;
	}
	
}

package IO.Serialization;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class ObjectWriter {

	/**
	 * @param args
	 */
	private ObjectOutputStream o;
	
	public ObjectWriter(String filename,Object obj) throws FileNotFoundException, IOException{
		o=new ObjectOutputStream(new FileOutputStream(filename));
		o.writeObject(obj);
		o.flush();
		o.close();
	}
	
}

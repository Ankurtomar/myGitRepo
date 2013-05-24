//DataReader

package IO.Reader;

import java.io.DataInputStream;
import java.io.IOException;
import print.Print;

public class myDataInputStream {
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		DataInputStream ds=new DataInputStream(System.in);
		String s=ds.readLine();
		while(s != "quit"){
			new Print(s);
			s=ds.readLine();
		}
		ds.close();
	}

}

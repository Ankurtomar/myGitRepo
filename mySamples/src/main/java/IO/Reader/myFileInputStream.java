package IO.Reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import print.Print;

public class myFileInputStream {

	/**
	 * @param args
	 */
	private FileInputStream f;
	private StringBuffer sb;
	
	myFileInputStream(String name) throws IOException{
		f=new FileInputStream(new File(name));
		sb=new StringBuffer();
		//Integer i;
		int i;
		i=f.read();
		while(i != -1){
			sb.append((char)i);
			
			//sb.append(i.toBinaryString(i));
			i=f.read();
		}
	}
	
	public String getFileOutput(){
		return sb.toString();
	}
	
	protected void finalize() throws IOException{
		f.close();
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		myFileInputStream ob=new myFileInputStream("hell.txt");
		new Print(ob.getFileOutput());
	}

}

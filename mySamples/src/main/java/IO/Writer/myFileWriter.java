//CharacterSream
//Better way to write smthng into file than character by character 

package IO.Writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class myFileWriter {

	/**
	 * @param args
	 */
	private File f;
	private FileWriter fw;
	public PrintWriter pw;
	
	myFileWriter(String name) throws IOException{
		f=new File(name);
		fw=new FileWriter(f);
		pw=new PrintWriter(fw);
	}
	
	protected void finalize()throws IOException{
		fw.close();
		pw.close();
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		PrintWriter pw2=new myFileWriter("hell.txt").pw;
		pw2.println("hello word");
		pw2.flush();
	}

}

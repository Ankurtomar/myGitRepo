//CharacterStream Reader

package IO.Reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import print.Print;

public class myFileReader {

	/**
	 * @param args
	 */
	private File f;
	private FileReader fr;
	public BufferedReader br;
	
	myFileReader(String name) throws FileNotFoundException{
		f=new File(name);
		fr=new FileReader(f);
		br=new BufferedReader(fr);
	}
	
	protected void finalize() throws IOException{
		fr.close();
		br.close();
	}
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader br2=new myFileReader("hell.txt").br;
		String s=br2.readLine();
		while(s!=null){
			new Print(s);
			s=br2.readLine();
		}
		
	}

}

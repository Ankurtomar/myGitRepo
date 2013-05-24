package IO;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.SequenceInputStream;

public class mySequenceStream {

	/**
	 * @param args
	 */
	public static void main(String a[]) throws Exception 
	{
		FileInputStream fis = new FileInputStream("hcl.dat");
		FileInputStream fis1 = new FileInputStream("hell.txt");

//		byte b [] = " JAVA HCL BATCHE".getBytes();
//		ByteArrayInputStream bis = new ByteArrayInputStream( b );

		SequenceInputStream sis = new SequenceInputStream( fis, fis1 );

		FileOutputStream fos = new FileOutputStream("CombineOutput.txt");

		int ch;
		while( (ch = sis.read() ) != -1 )
			fos.write(ch);
	}

}

package IO;

import java.io.File;

public class myFileIO {

	/**
	 * @param args
	 */
	public static void main(String s[]) throws Exception
	{
	File f=new File("abc.java");
	System.out.println("Is Directory::"+f.isDirectory());

	File f1=new File("file11.java");
	System.out.println("Is File::"+f1.isFile());
	System.out.println("Exists::"+f1.exists());

	System.out.println("Readable::"+f1.canRead());
	System.out.println("Writeable::"+f1.canWrite());

	//File f2=new File("file11.java");
	//f1.setReadOnly();

	File f3=new File("abc.txt");

	System.out.println("Dir mkdir::"+f3.mkdir());

	//System.out.println("Delete::"+f3.delete());

	File f4=new File("file1.java");
	
	f4.mkdir();

	File f5=new File("rename.cpp");
	System.out.println("Remane:"+f4.renameTo(f5));
	 
	 }

}

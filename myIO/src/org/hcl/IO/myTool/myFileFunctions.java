/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hcl.IO.myTool;

import java.awt.Frame;
import java.io.BufferedReader;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import jxl.write.WriteException;

/**
 * 
 * @author Ankur_Tomar
 */
class myFilenameFilter implements FilenameFilter {

	String ext;

	public myFilenameFilter(String ext) {
		if (!ext.equals("")) {
			this.ext = "." + ext;
		} else {
			this.ext = ext;
		}
	}

	public boolean accept(java.io.File dir, String name) {
		if (dir.isDirectory()) {
			return true;
		}else if(ext.equals("")){
			return true;
		}
		return name.endsWith(ext);
	}
}

class myFileFilter extends FileFilter {

	private String[] extn;

	public myFileFilter(String[] extn) {
		this.extn = extn;
	}

	public String getExtension(File f) {
		String name = f.getName();
		if (name.indexOf('.') != -1) {
			return name.substring(name.lastIndexOf('.') + 1, name.length());
		} else {
			return null;
		}
	}

	public boolean accept(File f) {
		if (f.isDirectory()) {
			return true;
		}
		String extension = getExtension(f);

		if (extension != null) {
			for (int i = 0; i < extn.length; i++) {
				if (extension.equals(extn[i])) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}

	public String getDescription() {
		String tmp = "Only [";
		for (int i = 0; i < extn.length; i++, tmp += " : ") {
			tmp += extn[i];
		}
		tmp += "]";
		return tmp;
	}
}

public class myFileFunctions {

	private static boolean flag = false;
	private static StringBuffer status;
	private static FilenameFilter ob;
	private static DocumentBuilderFactory dbf;
	private static DocumentBuilder db;
	public static String fileSeparator = System.getProperty("file.separator");
	private static String current_directory = System.getProperty("user.dir");

	/**
	 * Returns list of all directory and files present in given directory and
	 * filtered by mentioned extension.
	 * 
	 * @param directory
	 *            path of directory.
	 * @param fileExt
	 *            extn for filtering.
	 * @return List<String> containing filtered files name and its absolute path.  
	 */
	public static List getListOfFiles(List list, String directory,
			String fileExt) {
		File f = new File(directory);
		if (ob == null) {
			ob = new myFilenameFilter(fileExt);
		}
		String[] dir;

		if (f.isDirectory()) {

			if (fileExt.equals("")) {
				dir = f.list();
			} else {
				dir = f.list(ob);
			}
			for (int i = 0; i < dir.length; i++) {
				getListOfFiles(list, directory + fileSeparator + dir[i],
						fileExt);
			}
		} else if (f.isFile()) {
			if (ob.accept(f, f.getName())) {
				list.add(f.getName());
				list.add(f.getAbsolutePath());
			}
		}
		return list;
	}

	/**
	 * Copy File into new file.
	 * 
	 * @param f2
	 *            source file.
	 * @param f3
	 *            sink file.
	 * @return status of operation.
	 * @throws IOException
	 */
	public static boolean copyFile(File f2, File f3) throws IOException {

		if (f2.exists()) {
			if (!f3.exists()) {
				try {
					f3.createNewFile();
				} catch (IOException e) {
					System.out.println("Unable to create file at : "
							+ f3.getAbsolutePath());
				}
			}
			FileChannel source = null, destination = null;

			try {
				source = new FileInputStream(f2).getChannel();
				destination = new FileOutputStream(f3).getChannel();
				destination.transferFrom(source, 0, source.size());
			} finally {
				if (source != null) {
					source.close();
				}
				if (destination != null) {
					destination.close();
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public static boolean writeToFile(File f, String s) throws IOException {
		f.createNewFile();
		FileWriter fw = new FileWriter(f);
		fw.write(s);
		fw.flush();
		fw.close();
		return true;
	}

	/**
	 * Move source file to destination directory.
	 * 
	 * @param f1
	 *            source file.
	 * @param directory
	 *            destination directory.
	 * @return status of operation.
	 * @throws IOException
	 */
	public static boolean moveFile(File f1, String directory)
			throws IOException {

		String temp = directory + fileSeparator + f1.getName();
		File ff = new File(temp);
		if (ff.exists()) {
			copyFile(f1,
					new File(temp
							+ "_"
							+ new Date(System.currentTimeMillis())
									.toGMTString().replace(':', '-')));
		} else {
			copyFile(f1, new File(temp));
		}
		flag = true;

		return (f1.delete() && flag);
	}

	public static String getFile(String[] extn) {
		JFileChooser fc = new JFileChooser();
		if (extn != null) {
			fc.setFileFilter(new myFileFilter(extn));
			fc.setAcceptAllFileFilterUsed(false);
		}
		if (fc.showOpenDialog(new Frame()) == JFileChooser.APPROVE_OPTION) {
			return fc.getSelectedFile().getAbsolutePath();
		}
		return null;
	}

	public static String addDateToFilename(String filename) {
		Date d = new Date(System.currentTimeMillis());
		return filename + "_" + d.toGMTString().replace(':', '-');
	}

	/**
	 * Parse and validate given xml file as per mentioned in file.
	 * @param f
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static boolean xmlValidator(File f)
			throws ParserConfigurationException, SAXException, IOException {
		dbf = DocumentBuilderFactory.newInstance();
		db = dbf.newDocumentBuilder();
		dbf.setValidating(true);
		dbf.setIgnoringElementContentWhitespace(true);
		db.parse(f);
		return true;
	}

	/**
	 * Replace the oldString with the newString in the provided file.
	 * @param file
	 * @param oldString
	 * @param newString
	 * @return
	 * @throws IOException
	 */
	public static boolean makeChangesInFile(File file, String oldString,
			String newString) throws IOException {
		File ff = new File(file.getParent()  + "New");
		if (!ff.exists()) {
			ff.mkdir();
		}
		status = new StringBuffer();
		status.append("-------------------------------------------------\nMaking change in file: "
				+ file.getName());
		FileReader fr = new FileReader(file);
		char temp[] = new char[(int) file.length()];
		fr.read(temp);
		status.append("\n File size: " + temp.length);

		String temp1 = String.valueOf(temp);
		temp1 = temp1.replace(oldString, newString);
		status.append("\nstring replaced");

		fr.close();

		System.out.println(temp1);
		File tempFile=new File(current_directory +fileSeparator+"Temp"+ fileSeparator + file.getName());
		writeToFile(tempFile,temp1);
		
		copyFile(tempFile,file);
		
		return true;
	}
	
	public static void myLogger(ArrayList errorlist) throws IOException{
		if (errorlist.size() > 1) {
					
			File output = new File(myFileFunctions.current_directory
					+ myFileFunctions.fileSeparator + "logs");
			if (!output.canRead()) {
				output.mkdir();
			}
			
			System.out
					.println("Please find the Logs at "+output.getAbsolutePath()+" it has "
							+ errorlist.size() + "entries");
			StringBuffer temp = new StringBuffer();
			Iterator it = errorlist.listIterator();
			
			while (it.hasNext()) {
				temp.append(it.next().toString());
			}
			
			myFileFunctions
					.writeToFile(
							new File(
									output.getAbsolutePath()
											+ myFileFunctions.fileSeparator
											+ myFileFunctions
													.addDateToFilename("Swap Operation")
											+ ".log"), temp.toString());
		}
	}

	public static void main(String[] a) throws IOException, WriteException,
			ParserConfigurationException, SAXException {
		// System.out.println(renameFile(new File(),""));
		/*
		 * String[] tmp=new String[1]; tmp[0]="xls";
		 * //System.out.println(myFileFunctions.getFile(tmp));
		 * 
		 * System.out.print("Please select MS Excel Sheet for input : "); String
		 * filename=myFileFunctions.getFile(tmp); System.out.println(tmp);
		 * Excel.readExcel(filename);
		 * 
		 * 
		 * System.out.println(
		 * "Please choose in :\na: Rename files\nb: Get file path\nChoice:");
		 * char input = (char) System.in.read(); switch (input) { case 'a': {
		 * String[] temp=Excel.readExcel("Rename.xls"); for(int
		 * i=0;i<temp.length;i++){ myFileFunctions.copyFile(new
		 * File(temp[i]),new File(temp[i+1]));
		 * System.out.println("\r"+temp[i]+" : "+temp[i+1]); }
		 * System.out.println(".......Renaming complete........"); } break;
		 * 
		 * case 'b': {
		 */

		// System.out.println(myFileFunctions.xmlValidator(new
		// File("abc.xml")));

		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter directory location:");
		String directory = br.readLine();
		if (!directory.equals("")) {
			System.out.println("Enter file extension:");
			String ext = br.readLine().trim();


			if(!(ext.length()>0)){
				ext="";
			}
			
			
			String[] temp = (String[]) getListOfFiles(new ArrayList(),
					directory, ext).toArray(new String[0]);
			Excel.writeExcel(temp, "filename", 2);
		}
			
		//makeChangesInFile(new File("a.xml"),"abc","def");
			
			/*
			 * } break; default: System.out.println("...Invalid entry..."); }
			 * 
			 * 
			 * 
			 * 
			 * 
			 * //moveFile(new File(
			 * "C:\\Documents and Settings\\ankur_tomar\\My Documents\\NetBeansProjects\\Extracter\\filename.xls"
			 * ),"c:\\");
			 * 
			 * //System.out.println(myFileFunctions.renameFile(new
			 * File("filename.xls"),"test2.xls"));
			 */

		
		System.out.println("Exiting");
	}

}

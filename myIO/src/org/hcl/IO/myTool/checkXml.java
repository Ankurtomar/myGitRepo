package org.hcl.IO.myTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import jxl.write.WriteException;

public class checkXml {

	public static void main(String[] a) throws IOException, WriteException,
			ParserConfigurationException, SAXException {
		ArrayList list = new ArrayList();
		list.add("Files with Error");
		list.add("location of file");
		list.add("Type of Exception");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter directory location:");
		String directory = br.readLine();
		if (!directory.equals("")) {
			System.out.println("Enter file extension:");
			String ext = br.readLine();

			String[] temp = (String[]) myFileFunctions.getListOfFiles(
					new ArrayList(), directory, ext).toArray(new String[0]);
			for (int i = 0; i < temp.length; i += 2) {
				try {
					System.out.println("Checking file :" + temp[i]);
					myFileFunctions.xmlValidator(new File(temp[i + 1]));
				} catch (ParserConfigurationException e) {
					System.out.println("Parser exception :" + temp[i]);
					list.add(temp[i]);
					list.add(temp[i + 1]);
					list.add(e.toString());
				} catch (SAXException saxe) {
					System.out.println("SAX exception :" + temp[i]);
					list.add(temp[i]);
					list.add(temp[i + 1]);
					list.add(saxe.toString());
				}catch(Exception e){
					System.out.println("Exception :" + temp[i]);
					list.add(temp[i]);
					list.add(temp[i + 1]);
					list.add(e.getMessage());
				}
			}

			if (list.size() > 3) {
				System.out
						.println("Please find the list of files in :xml parser report.xls");
				Excel.writeExcel((String[]) list.toArray(new String[0]),
						"xml parser report", 3);
			} else {
				System.out.println("No parsing exception in any file");
			}
		}
	}
}

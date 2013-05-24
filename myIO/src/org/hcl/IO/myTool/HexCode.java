package org.hcl.IO.myTool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;

public class HexCode {

	public static void main(String[] args) {
		File file;
		FileInputStream fin;
		String record = null;
		FileReader filereader = null;
		FileWriter filewriter = null;
		BufferedReader buffreader = null;
		StringTokenizer token = null;
		int recordcount = 0;
		String first;

		try {
			String fname = "TOC.xml";
			file = new File(fname);
			filereader = new FileReader(fname);
			filewriter = new FileWriter("output.xml");
			buffreader = new BufferedReader(filereader);
			record = new String();
			while ((record = buffreader.readLine()) != null) { 
				
				// System.out.println(recordcount);
				// System.out.println("record   : "+record);
				// System.out.println(record.length());
				// System.out.println(record.length()+".................");

				first = "";

				for (int ctr = 0; ctr < record.length(); ctr++) {
					int character = (int) record.charAt(ctr);
					char character1 = record.charAt(ctr);
					// System.out.println(character+";;;;;;");

					if (character >= 128) {
						System.out.println(record);
						System.out.println("special char" + character);
						String hexValue = Integer
								.toHexString(0x10000 | character).substring(1)
								.toUpperCase();
						first += "&#x" + hexValue + ";";
					}
					if (character <= 128)
						first += character1;
					// System.out.println(first);

				}
				if (first != null)
					filewriter.write(first + "\n");
				// System.out.println(first);
				recordcount++;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				filereader.close();
				filewriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

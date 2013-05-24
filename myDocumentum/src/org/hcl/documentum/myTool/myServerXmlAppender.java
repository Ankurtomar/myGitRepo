package org.hcl.documentum.myTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import org.hcl.IO.myTool.Excel;
import org.hcl.IO.myTool.myFileFunctions;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

public class myServerXmlAppender {

	
	private static DctmClient client;
	private static IDfSession sess;
	private static String[] data;
	private static IDfId id;
	private static ArrayList errorlist;
	/**
	 * @param args
	 * @throws DfException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, DfException {
		// TODO Auto-generated method stub
		String[] tmp = new String[1];
		tmp[0] = "xls";
		errorlist=new ArrayList();
		DfLogger.info(
				Excel.class,
				"Please select MS Excel Sheet for input : "
						+ "\nSheet should have\n"
						+ "Object_name : DCTM path : Old String : New String",
				null, null);
		String filename = myFileFunctions.getFile(tmp);
		if (filename != null) {
			DfLogger.info(Excel.class, "\nSelected filename : " + filename,
					null, null);
			data = Excel.readExcel(filename);
			
			ArrayList id,oldStringlist,newStringlist;
			id=new ArrayList();
			oldStringlist=new ArrayList();
			newStringlist=new ArrayList();
			
			client=DctmClient.getInstance();
			client.setLogger(errorlist);
			sess=client.getSessionManager().newSession(client.getDocbase());
			
			for(int i=0;i<data.length;i+=4){
				try{
					System.out.println("\nObject_name : "+data[i]+
							"\nObject location : "+data[i+1]+
							"\nOldString : "+data[i+2]+
							"\nNewString : "+data[i+3]);
				id.add(client.getObjectID(data[i], data[i+1],"dm_document",true,sess)[0]);
				oldStringlist.add(data[i+2]);
				newStringlist.add(data[i+3]);
				}catch(DfException ex){
					DfLogger.info(Excel.class, "Unable to retrieve info for :"+data[i]+" : "+data[i+1],
							null, ex);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			
			
			
			String[] checkOutFilePath=client.doCheckout((IDfId[])id.toArray(new IDfId[0]), sess);
			String[] oldString=(String[]) oldStringlist.toArray(new String[0]),newString=(String[]) newStringlist.toArray(new String[0]);
			
			
			for(int i=0;i<checkOutFilePath.length;i++){
				myFileFunctions.makeChangesInFile(new File(checkOutFilePath[i]), oldString[i], newString[i]);
				System.out.println("Changing In :"+checkOutFilePath[i]);
			}
			
			client.doCheckin((IDfId[])id.toArray(new IDfId[0]), sess);
			
			myFileFunctions.myLogger(errorlist);
		
			
	}
		System.out.println("........DONE.........");	
	}

}

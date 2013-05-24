package org.hcl.documentum.myTool;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.hcl.IO.myTool.Excel;
import org.hcl.IO.myTool.myFileFunctions;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

public class myObjectRenamer {

	/**
	 * @param args
	 */
	private static DctmClient client;
	private static IDfSession sess;
	private static String[] data;
	private static IDfId id;
	
	
	public static void main(String[] args) throws FileNotFoundException, IOException, DfException  {
		// TODO Auto-generated method stub
		
		client=DctmClient.getInstance();
		sess=client.getSessionManager().newSession(client.getDocbase());
		
		String[] tmp = new String[1];
		tmp[0] = "xls";

		DfLogger.info(
				Excel.class,
				"Please select MS Excel Sheet for input : "
						+ "\nSheet should have\n"
						+ "Object_name : Object_type : DCTM path : New file name",
				null, null);
		String filename = myFileFunctions.getFile(tmp);
		if (filename != null) {
			DfLogger.info(Excel.class, "\nSelected filename : " + filename,
					null, null);
			data = Excel.readExcel(filename);
			
			ArrayList newname=new ArrayList(),list=new ArrayList();
			
			int count=0;
			for(int i=0;i<data.length;i+=4){
				try{
					System.out.println("Object_name : "+data[i]+
							"\nObject_type : "+data[i+1]+
							"\nObject_location : "+data[i+2]+
							"\nNew Object_name :"+data[i+3]);
					
					
				client.doRenameSysObject(client.getObjectID(data[i], data[i+2], data[i+1],true,sess)[0],data[i+3],sess);
				}catch(DfException ex){
					ex.printStackTrace();
					DfLogger.error(DctmClient.class, "Unable to rename: "+ex.toString(),
							null, ex);
				}catch(Exception e){
					e.printStackTrace();
				}
				}
			System.out.println("...........Done.............");
			
	}

}}

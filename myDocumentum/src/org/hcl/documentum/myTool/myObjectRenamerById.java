package org.hcl.documentum.myTool;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.hcl.IO.myTool.Excel;
import org.hcl.IO.myTool.myFileFunctions;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

public class myObjectRenamerById {

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
						+ "r_object_id : New file name",
				null, null);
		String filename = myFileFunctions.getFile(tmp);
		if (filename != null) {
			DfLogger.info(Excel.class, "\nSelected filename : " + filename,
					null, null);
			data = Excel.readExcel(filename);
			
			ArrayList newname=new ArrayList(),list=new ArrayList();
			
			int count=0;
			for(int i=0;i<data.length;i+=2){
				try{
					System.out.println("r_object_id : "+data[i]+
							"\nNew Object_name :"+data[i+1]);
					
					
					System.out.println("Success : "+client.doRenameSysObject(new DfId(data[i]),data[i+1],sess));
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

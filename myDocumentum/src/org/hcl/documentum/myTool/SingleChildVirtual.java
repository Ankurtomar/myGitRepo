package org.hcl.documentum.myTool;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.hcl.IO.myTool.Excel;
import org.hcl.IO.myTool.myFileFunctions;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfVirtualDocument;
import com.documentum.fc.client.IDfVirtualDocumentNode;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLoginInfo;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfLoginInfo;

public class SingleChildVirtual {
	
	 private static String[] data;
	    private static ArrayList errorlist;
	    
	public static void main(String str[])throws Exception
	{
		errorlist=new ArrayList();
        DctmClient client = DctmClient.getInstance();
        client.setLogger(errorlist);
        IDfSession sess = client.getSessionManager().newSession(client.getDocbase());
        String[] tmp = new String[1];
        tmp[0] = "xls";

        System.out.print("Please select MS Excel Sheet for input : "
                + "\nSheet should have\n"
                + "Object_name : Child file object_name : Dctm path");
        String filename = myFileFunctions.getFile(tmp);
        System.out.println("\nSelected filename : " + filename);
        data = Excel.readExcel(filename);

        IDfId parent;
        IDfId[] child = new DfId[(data.length / 3) - 1];

        for (int i = 0; i < data.length; i += 3) {
            try {
                System.out.println("Parent File :" + data[i]);
                System.out.println("Child File :" + data[i+1]);
                System.out.println("Parent File Location:" + data[i + 2]);

                errorlist.add("Parent File :" + data[i]);
                errorlist.add("Child File :" + data[i+1]);
                errorlist.add("Parent File Location:" + data[i + 2]);
                
                parent = client.getObjectID(data[i], data[i + 2],"dm_document",false,sess)[0];
                child = client.getObjectID(data[i+1], data[i + 2],"dm_document",false,sess);

                client.addVirtualChild(parent, child, sess);

            } catch (DfException e) {
            	errorlist.add("Invalid info : " + data[i] + " " + data[i + 2]);
                System.out.println("Invalid info : " + data[i] + " " + data[i + 2]);
                e.printStackTrace();
            }catch(Exception ex){
                ex.printStackTrace();
                errorlist.add(data[i]+" : "+ex.getMessage());
            }
        }
        myFileFunctions.myLogger(errorlist);
        System.out.println(".........Done..........");

	}}
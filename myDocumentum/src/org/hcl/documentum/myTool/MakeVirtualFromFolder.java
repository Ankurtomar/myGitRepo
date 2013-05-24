/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hcl.documentum.myTool;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.IDfId;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.hcl.IO.myTool.Excel;
import org.hcl.IO.myTool.myFileFunctions;

/**
 * Add child files to virtual parent.
 * where child files are all files present in mention location.
 * @author Ankur_Tomar
 */
public class MakeVirtualFromFolder {

    private static String[] data;
    private static ArrayList errorlist;

    public static void main(String[] args) throws FileNotFoundException, IOException, DfException {
    	errorlist=new ArrayList();
        DctmClient client = DctmClient.getInstance();
        client.setLogger(errorlist);
        IDfSession sess = client.getSessionManager().newSession(client.getDocbase());
        String[] tmp = new String[1];
        tmp[0] = "xls";

        System.out.print("Please select MS Excel Sheet for input : "
                + "\nSheet should have\n"
                + "Object_name : DCTM path : Folder location of child files");
        String filename = myFileFunctions.getFile(tmp);
        System.out.println("\nSelected filename : " + filename);
        data = Excel.readExcel(filename);

        IDfId parent;
        IDfId[] child = new DfId[(data.length / 3) - 1];

        for (int i = 0; i < data.length; i += 3) {
            try {
                System.out.println("Parent File :" + data[i]);
                System.out.println("Parent File Location:" + data[i + 1]);

                errorlist.add("Parent File :" + data[i]);
                errorlist.add("Parent File Location:" + data[i + 1]);
                
                parent = client.getObjectID(data[i], data[i + 1],"dm_document",true,sess)[0];
                child = client.getObjectID(null, data[i + 2],"dm_document",true,sess);

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

    }
}

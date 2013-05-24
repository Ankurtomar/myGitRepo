/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hcl.documentum.myTool;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfVirtualDocument;
import com.documentum.fc.client.IDfVirtualDocumentNode;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.hcl.IO.myTool.Excel;
import org.hcl.IO.myTool.myFileFunctions;

/**
 *Removes all child files from virtual document.
 * @author Ankur_Tomar
 */
public class RemoveVirtual {

    private DctmClient client;
    private IDfSession session;
    private static IDfVirtualDocument vDoc;
    private static IDfVirtualDocumentNode nodeRoot, child;
    private IDfSysObject object;
    private static String[] data;

    /**
     * Default Initiation of the object.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws DfException
     */
    RemoveVirtual() throws FileNotFoundException, IOException, DfException {
        client = DctmClient.getInstance();
        session = client.getSessionManager().getSession(client.getDocbase());
    }

    /**
     * Removes child files from the mentioned virtual document located at provided path in repository.
     * @param path Folder path of virtual file in repository.
     * @param filename Object_name of virtual file.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws DfException
     */
    RemoveVirtual(String path, String filename) throws FileNotFoundException, IOException, DfException {
        this();
        object = (IDfSysObject) session.getObjectByPath(path + "/" + filename);
        client.removeAllNode(object,session);
    }

    /**
     * Removes child files from the r_object_id virtual document.
     * @param id r_object_id of virtual object.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws DfException
     */
    RemoveVirtual(String id) throws FileNotFoundException, IOException, DfException {
        this();
        object = (IDfSysObject) session.getObject(new DfId(id));
        client.removeAllNode(object,session);
    }

       

    /**
     * Remove child from virtual file whose information is in excel.
     * Excel workbook should have object_name and path of the virtual file.
     * @param a Takes workbook name.
     * @throws FileNotFoundException
     * @throws IOException
     * @throws DfException
     */
    public static void main(String[] a) throws FileNotFoundException, IOException, DfException {
        String[] tmp = new String[1];
        tmp[0] = "xls";

        System.out.print("Please select MS Excel Sheet for input : "
                + "\nSheet should have\n"
                + "Object_name : DCTM path");
        String filename = myFileFunctions.getFile(tmp);
        System.out.println("\nSelected filename : " + filename);
        data = Excel.readExcel(filename);

        for (int i = 2; i < data.length; i += 2) {
            new RemoveVirtual(data[i + 1].trim(), data[i].trim());
        }

    }
}

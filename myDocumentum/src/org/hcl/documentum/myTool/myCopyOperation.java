package org.hcl.documentum.myTool;

import java.util.ArrayList;

import org.hcl.IO.myTool.Excel;
import org.hcl.IO.myTool.myFileFunctions;

import com.documentum.fc.client.DfAuthenticationException;
import com.documentum.fc.client.DfIdentityException;
import com.documentum.fc.client.DfPrincipalException;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

public class myCopyOperation {

	/**
	 * @param args
	 */

	private static DctmClient client;
	private static IDfSession sess;
	private static String[] data;
	private static IDfId id;
	private static IDfFolder destFolder;

	static {
		System.out
				.println(".....................\n"
						+ "Name: Copy operation"
						+ "\n Description: It creates copy of files at the new location"
						+ "\n");
	}

	public static void main(String[] args) throws DfIdentityException,
			DfAuthenticationException, DfPrincipalException,
			DfServiceException, DfException {
		// TODO Auto-generated method stub
		client = DctmClient.getInstance();
		sess = client.getSessionManager().newSession(client.getDocbase());

		String[] tmp = new String[1];
		tmp[0] = "xls";

		DfLogger.info(
				Excel.class,
				"Please select MS Excel Sheet for input : "
						+ "\nSheet should have\n"
						+ "Object_name : Object_location : New file name : New file location",
				null, null);
		String filename = myFileFunctions.getFile(tmp);
		if (filename != null) {
			DfLogger.info(Excel.class, "\nSelected filename : " + filename,
					null, null);
			data = Excel.readExcel(filename);

			ArrayList newname = new ArrayList(), list = new ArrayList();

			int count = 0;
			for (int i = 0; i < data.length; i += 4) {
				try {
					System.out.println("Object_name : " + data[i]
							+ "\nObject_location : " + data[i + 1]
							+ "\nNew file name : " + data[i + 2]
							+ "\nNew file location :" + data[i + 3]);
					id = null;
					destFolder = null;
					
					id = client.getObjectID(data[i], data[i + 1],
							"dm_document",true, sess)[0];
					destFolder = sess.getFolderByPath(data[i + 3]);
					
					if (id != null && destFolder != null) {
						client.doCopyOperation(id, destFolder, sess);
						id = client.getObjectID(data[i], data[i + 1],
								"dm_document",true, sess)[1];
						System.out
								.println("Copied Object id: " + id.toString());
						client.doRenameSysObject(id, data[i + 2], sess);
						System.out.println("Object renamed to :" + data[i + 2]);
					} else {
						System.out.println("Problem in creating copy of "
								+ data[i] + " : " + data[i + 2]);
					}

				} catch (DfException ex) {
					ex.printStackTrace();
					DfLogger.error(DctmClient.class,
							"Problem in copy Operation: " + ex.toString(),
							null, ex);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			System.out.println("...........Done.............");

		}
	}

}

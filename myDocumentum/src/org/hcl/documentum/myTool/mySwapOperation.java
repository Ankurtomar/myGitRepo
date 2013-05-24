/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hcl.documentum.myTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.hcl.IO.myTool.Excel;
import org.hcl.IO.myTool.myFileFunctions;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;

/**
 * 
 * @author Ankur_Tomar
 */
public class mySwapOperation {

	private static String[] data;
	private boolean checkout;
	private DctmClient client;
	private String[] checkOutPath;
	private IDfId[] object_id;
	private static IDfSessionManager sMgr;
	private static IDfSession sess;
	private String temp;
	private static ArrayList errorlist;

	/**
	 * Do swapping of files of mentioned r_object_id by creating backup at
	 * mentioned location of filesystem and checkIn the file provided in
	 * parameter.
	 * 
	 * @param client
	 *            Documentum client.
	 * @param id
	 *            Array of r_object_id.
	 * @param file
	 *            Array of new files.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws DfException
	 */
	mySwapOperation() {
	}

	public boolean run(DctmClient client, IDfId[] id, File[] newfile,
			String[] backup, IDfSession sess) throws FileNotFoundException,
			IOException, DfException {

		this.client = client;
		this.object_id = id;

		try {

			System.out.println("Begin checkout operation");

			checkOutPath = client.doCheckout(id, sess);
			if (checkOutPath != null) {

				File[] checkOutFile = new File[checkOutPath.length];
				System.out.println("Total checkout objects"
						+ checkOutPath.length);
				DfLogger.info(this, "r_object_id\t\t:Backup\t\tReplace", null,
						null);

				for (int i = 0; i < checkOutPath.length; i++) {

					checkOutFile[i] = new File(checkOutPath[i]);

					temp = i
							+ 1
							+ ": "
							+ id[i]
							+ "\t"
							+ myFileFunctions.moveFile(checkOutFile[i],
									backup[i])
							+ "\t\t"
							+ myFileFunctions.copyFile(newfile[i],
									checkOutFile[i]);

					System.out.print("\r" + temp);
					// DfLogger.info(this, temp, null, null);
				}

				// System.out.println("New File location: " + data[i + 2]);

				System.out.println("Begin checkIn operation");

				client.doCheckin(id, sess);

				DfLogger.info(this, "CheckIn Status : " + true, null, null);
			}
		} catch (DfException exception) {
			exception.printStackTrace();
			DfLogger.error(this, exception.toString(), null, exception);

			if (checkout) {
				System.out.println("Begin cancelCheckOut operation");
			}

		} finally {
			client.cancelCheckOut(id, sess);
		}
		return true;
	}

	/**
	 * Required a SwapFiles.xls having information in four columns
	 * [object_name:object location in dctm: new file absolute path:backup
	 * directory path in filesystem]
	 * 
	 * @param a
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws DfException
	 */
	public static void main(String[] a) throws FileNotFoundException,
			IOException, DfException, InterruptedException {
		IDfId[] id;
		DctmClient client = null;
		errorlist = new ArrayList();
		errorlist.add("..................Logs.....................");

		try {

			String[] tmp = new String[1];
			tmp[0] = "xls";

			DfLogger.info(
					Excel.class,
					"Please select MS Excel Sheet for input : "
							+ "\nSheet should have\n"
							+ "Object_name : DCTM path : New file location for CheckIn : Backup location",
					null, null);
			String filename = myFileFunctions.getFile(tmp);
			if (filename != null) {
				DfLogger.info(Excel.class, "\nSelected filename : " + filename,
						null, null);
				data = Excel.readExcel(filename);
				ArrayList object_id;
				ArrayList swapfiles;
				ArrayList backupPath;
				ArrayList newFile;

				client = DctmClient.getInstance();
				client.setLogger(errorlist);
				sMgr = client.getSessionManager();
				sess = sMgr.getSession(client.getDocbase());
				mySwapOperation myswap = new mySwapOperation();
				int count = 0;

				object_id = new ArrayList();
				swapfiles = new ArrayList();
				backupPath = new ArrayList();
				newFile = new ArrayList();

				for (int i = 0, k = 0; i < data.length; i += 4) {

					try {
						id = client.getObjectID(data[i], data[i + 1],
								"dm_document",true, sess);
						File temp = new File(data[i + 2]);

						if (temp.exists() && id.length > 0) {
							object_id.add(id[0]);
							newFile.add(temp);
							swapfiles.add(data[i + 2]);
							backupPath.add(data[i + 3]);
							count++;
						} else {
							errorlist.add("Problem : " + data[i] + " : "
									+ id.toString() + " : "
									+ "Object found in dctm :"
									+ ((id.length > 0) ? "true" : "false")
									+ " File exist :" + temp.exists());
							System.out.println("Object found in dctm :"
									+ ((id.length > 0) ? "true" : "false")
									+ " File exist :" + temp.exists());
						}
						k++;
					} catch (DfException excep) {
						DfLogger.warn(client,
								"Wrong object_name = " + data[i]
										+ " location = " + data[i + 1] + "\n"
										+ excep.toString(), null, null);
					} catch (Exception ioe) {
						DfLogger.error(client, "Something is wrong for "
								+ data[i] + "\n" + ioe.toString(), null, null);
					}

					if (count >= 100 || i + 5 >= data.length) {
						System.out.println("Size:"+object_id.size());
						myswap.run(client,
								(IDfId[]) object_id.toArray(new IDfId[0]),
								(File[]) newFile.toArray(new File[0]),
								(String[]) backupPath.toArray(new String[0]),
								sess);
						object_id = new ArrayList();
						swapfiles = new ArrayList();
						backupPath = new ArrayList();
						newFile = new ArrayList();
						count = 0;
					}
				}
				errorlist.add("........DONE.........");
				myFileFunctions.myLogger(errorlist);
			}

			System.out.println("........DONE.........");
			DfLogger.info(mySwapOperation.class, "........DONE.........", null,
					null);

		} catch (Exception e) {
			e.printStackTrace();
			DfLogger.error(mySwapOperation.class, e.getMessage(), null, e);
		}
	}
}

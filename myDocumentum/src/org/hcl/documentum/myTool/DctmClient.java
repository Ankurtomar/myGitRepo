package org.hcl.documentum.myTool;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.DfAuthenticationException;
import com.documentum.fc.client.DfIdentityException;
import com.documentum.fc.client.DfPrincipalException;
import com.documentum.fc.client.DfServiceException;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfDocument;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfVirtualDocument;
import com.documentum.fc.client.IDfVirtualDocumentNode;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfId;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.fc.common.IDfLoginInfo;
import com.documentum.fc.common.IDfProperties;
import com.documentum.fc.common.IDfValue;
import com.documentum.operations.IDfCheckinNode;
import com.documentum.operations.IDfCheckinOperation;
import com.documentum.operations.IDfCheckoutNode;
import com.documentum.operations.IDfCheckoutOperation;
import com.documentum.operations.IDfCopyNode;
import com.documentum.operations.IDfCopyOperation;
import com.documentum.operations.IDfExportNode;
import com.documentum.operations.IDfExportOperation;
import com.documentum.operations.IDfFile;
import com.documentum.operations.IDfImportNode;
import com.documentum.operations.IDfImportOperation;
import com.documentum.operations.IDfOperation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.hcl.IO.myTool.myFileFunctions;

/**
 * Take care of connecting and functionality to documentum.
 * 
 * @author Ankur_Tomar
 */ 
public class DctmClient {

	private static DctmClient instance;
	private static Properties prop;
	private static IDfSessionManager sMgr;
	private static IDfClientX clientx;
	private static IDfClient client;
	private static String docbase, user, pass;
	private static String prop_file = "login.properties";

	private static IDfQuery queryObj;
	private ArrayList checkOutPath;
	private IDfVirtualDocument vDoc;
	private IDfVirtualDocumentNode nodeRoot, child;
	private IDfSysObject object;
	private ArrayList log;
	private static int SIMULTANEOUS_CHECKIN = 10, SIMULTANEOUS_CHECKOUT = 100,
			SIMULTANEOUS_EXPORT = 100, SIMULTANEOUS_VIRTUAL = 43;

	/**
	 * 
	 * @return returns singleton object of this class
	 */
	public static DctmClient getInstance() {
		if (instance == null) {
			synchronized (DctmClient.class) {
				instance = new DctmClient();
			}
		}
		return instance;
	}

	/**
	 * Get IDFClientX reference from content server. Required login.properties
	 * file in the class path that contain information about docbase, username
	 * and password.
	 * 
	 * @throws FileNotFoundException
	 *             when login.properties file not found
	 * @throws IOException
	 * @throws DfException
	 */
	private DctmClient() {
		log = new ArrayList();
		try {
			prop = new Properties();
			prop.load(new FileInputStream(prop_file));
			clientx = new DfClientX();
			client = clientx.getLocalClient();
			docbase = prop.getProperty("docbase");
			user = prop.getProperty("username");
			pass = prop.getProperty("password");
			SIMULTANEOUS_CHECKIN = Integer.parseInt(prop
					.getProperty("SIMULTANEOUS_CHECKIN"));
			SIMULTANEOUS_CHECKOUT = Integer.parseInt(prop
					.getProperty("SIMULTANEOUS_CHECKOUT"));
			SIMULTANEOUS_EXPORT = Integer.parseInt(prop
					.getProperty("SIMULTANEOUS_EXPORT"));
			SIMULTANEOUS_VIRTUAL = Integer.parseInt(prop
					.getProperty("SIMULTANEOUS_VIRTUAL"));
		} catch (FileNotFoundException fnfe) {
			fnfe.printStackTrace();
			DfLogger.error(prop, fnfe.getMessage(), null, fnfe);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			DfLogger.error(prop, ioe.getMessage(), null, ioe);
		} catch (DfException de) {
			de.printStackTrace();
			DfLogger.error(clientx, de.getMessage(), null, de);
		} catch (Exception e) {
			e.printStackTrace();
			DfLogger.error(clientx, e.getMessage(), null, e);
		} finally {
			SIMULTANEOUS_CHECKIN = (SIMULTANEOUS_CHECKIN < 1000 && SIMULTANEOUS_CHECKIN > 0) ? 10
					: SIMULTANEOUS_CHECKIN;

			SIMULTANEOUS_CHECKOUT = (SIMULTANEOUS_CHECKOUT < 1000 && SIMULTANEOUS_CHECKOUT > 0) ? 100
					: SIMULTANEOUS_CHECKOUT;

			SIMULTANEOUS_EXPORT = (SIMULTANEOUS_EXPORT < 1000 && SIMULTANEOUS_EXPORT > 0) ? 100
					: SIMULTANEOUS_EXPORT;

			SIMULTANEOUS_VIRTUAL = (SIMULTANEOUS_VIRTUAL < 0 || SIMULTANEOUS_VIRTUAL > 45) ? 45
					: SIMULTANEOUS_VIRTUAL;
		}
	}

	/**
	 * 
	 * @param docbase
	 *            Name of docbase
	 * @param user
	 *            Client username
	 * @param pass
	 *            Client password
	 * @throws DfException
	 */
	protected DctmClient(String docbase, String user, String pass)
			throws DfException {
		clientx = new DfClientX();
		client = clientx.getLocalClient();
		this.docbase = docbase;
		this.user = user;
		this.pass = pass;
	}

	/**
	 * 
	 * @return returns IDFSessionManager reference after authenticating
	 * @throws DfException
	 */
	public IDfSessionManager createSessionManager() throws DfException {
		sMgr = client.newSessionManager();
		try {
			IDfLoginInfo loginInfoObj = clientx.getLoginInfo();
			loginInfoObj.setUser(user);
			loginInfoObj.setPassword(pass);
			sMgr.setIdentity(docbase, loginInfoObj);
			System.out.println("......Client Authenticated........");
			log.add("......Client Authenticated........");
			System.out.println("Logger info:" + clientx.getLogger().toString());

		} catch (DfException ex) {
			log.add(ex.getMessage());
			DfLogger.error(this, ex.getMessage(), null, ex);
		}
		return sMgr;
	}

	/**
	 * 
	 * @return returns IDFSessionManager reference.
	 * @throws DfException
	 */
	public IDfSessionManager getSessionManager() throws DfException {
		if (sMgr != null) {
			return sMgr;
		} else {
			return createSessionManager();
		}
	}

	/**
	 * Clear identity and close connection.
	 */
	public void finalize() {
		sMgr.clearIdentities();
		clientx.closeTrace();
	}

	/**
	 * 
	 * @return returns Docbase name
	 */
	public String getDocbase() {
		return docbase;
	}

	/**
	 * 
	 * @return returns IDfClientX reference
	 */
	public IDfClientX getClientX() {
		return clientx;
	}

	/**
	 * Executes IDfOperation
	 * 
	 * @param operation
	 * @return returns true if operation gets successful
	 * @throws DfException
	 */
	public synchronized boolean executeOperation(IDfOperation operation)
			throws DfException {
		boolean status = operation.execute();
		if (!status) {
			operation.getErrors();
		}
		return status;
	}

	/**
	 * Returns the IDfId in String format of the given object_name and location
	 * Default Query: select r_object_id from dm_document where
	 * object_name='OBJECT_NAME' adn folder('PATH',descend) if object_name is
	 * null it takes only location to return r_object_id of all objects present
	 * in mentioned path.
	 * 
	 * @param object_name
	 *            name of object
	 * @param location
	 *            dctm location
	 * @param depth
	 *            Dql enable descend functionality in folder constraint
	 * @param sess
	 *            IDfSession
	 * @return Array of r_object_id (DfId) of objects
	 * @throws DfException
	 */
	public synchronized IDfId[] getObjectID(String object_name,
			String location, String table, boolean depth, IDfSession sess)
			throws DfException {
		String query = "select r_object_id from " + table + " where ";
		if (!(object_name == null || object_name.equals(""))) {
			query += "object_name='" + object_name + "' and ";
		}

		query += "folder('" + location + "'";

		if (depth) {
			query += ",descend";
		}
		query += ")";

		queryObj = getClientX().getQuery();

		DfLogger.info(this, query, null, null);
		// sMgr = getSessionManager();
		// sess = sMgr.newSession(getDocbase());

		IDfCollection col = this.ExecuteReadQuery(query, sess);
		// System.out.println(col.getAttrCount());
		ArrayList list = new ArrayList();
		while (col.next()) {
			// IDfAttr attr=col.getAttr(0);

			IDfValue val = col.getValue("r_object_id");
			list.add(new DfId(val.asString()));
		}
		log.add("Query : " + query + " : Output :" + list.size());
		DfLogger.info(queryObj, "Output :" + list.size(), null, null);
		col.close();
		// System.out.println(list.size());
		return (IDfId[]) list.toArray(new IDfId[0]);
	}

	/**
	 * Returns location of checkout object if successfully checkout those
	 * r_object_ids.
	 * 
	 * @param myId
	 *            array of r_object_ids
	 * @param session
	 *            IDfSession
	 * @return Array of string containing location of checkout files.
	 * @throws DfException
	 */
	public synchronized String[] doCheckout(IDfId[] myId, IDfSession session)
			throws DfException {
		IDfCheckoutOperation operation = clientx.getCheckoutOperation();
		IDfSysObject sysObj[] = new IDfSysObject[myId.length];
		IDfCheckoutNode node[] = new IDfCheckoutNode[SIMULTANEOUS_CHECKOUT];
		checkOutPath = new ArrayList();
		int i = 0, count = 0;
		while (i < myId.length) {
			sysObj[i] = (IDfSysObject) session.getObject(myId[i]);
			if (sysObj[i] != null) {
				if (sysObj[i].isCheckedOut() == true) {
					sysObj[i].cancelCheckout();
					log.add("Checkout Operation : Cancelling previous checkout for Object "
							+ myId[i]);
					DfLogger.info(this,
							"Checkout Operation : Cancelling previous checkout for Object "
									+ myId[i], null, null);
				}
				node[count] = (IDfCheckoutNode) operation.add(sysObj[i]);
				count++;
			} else {
				log.add("Checkout Operation : Object :"
						+ sysObj[i].getObjectName() + " : " + myId[i]
						+ " can not be found.");
				DfLogger.info(
						this,
						"Checkout Operation : Object :"
								+ sysObj[i].getObjectName() + " : " + myId[i]
								+ " can not be found.", null, null);
				System.out.println("Checkout Operation : Object :"
						+ sysObj[i].getObjectName() + " : " + myId[i]
						+ " can not be found.");
			}
			if (count >= SIMULTANEOUS_CHECKOUT - 1 || i >= myId.length - 1) {
				try {
					executeOperation(operation);
					for (int j = 0; j < count; j++) {
						checkOutPath.add(node[j].getFilePath());
						log.add("Checkout Operation : checkout location for Object "
								+ node[j].getId()
								+ " : "
								+ node[j].getFilePath());
						DfLogger.info(
								this,
								"Checkout Operation : checkout location for Object "
										+ node[j].getId() + " : "
										+ node[j].getFilePath(), null, null);
					}
				} catch (DfException e) {
					System.out.println(e.getMessage());
				} finally {
					operation = clientx.getCheckoutOperation();
					count = 0;
				}
			}
			i++;
		}
		return (String[]) checkOutPath.toArray(new String[0]);
	}

	/**
	 * CheckIn all r_object_ids of objects with current version.
	 * 
	 * @param myId
	 *            r_object_id of objects
	 * @param session
	 *            IDfSession
	 * @return status of operation.
	 * @throws DfException
	 */
	public synchronized boolean doCheckin(IDfId[] myId, IDfSession session)
			throws DfException {
		IDfCheckinOperation operation = clientx.getCheckinOperation();
		IDfSysObject sysObj[] = new IDfSysObject[myId.length];
		IDfCheckinNode node[] = new IDfCheckinNode[SIMULTANEOUS_CHECKIN];
		int i = 0, count = 0;
		while (i < myId.length) {
			sysObj[i] = (IDfSysObject) session.getObject(myId[i]);
			if (sysObj[i] != null) {

				if (sysObj[i].isCheckedOut() == false) {
					log.add("CheckIn operation : Object "
							+ sysObj[i].getObjectName() + " " + myId[i]
							+ " is not checked out.");
					DfLogger.info(this, "CheckIn operation : Object "
							+ sysObj[i].getObjectName() + " " + myId[i]
							+ " is not checked out.", null, null);
					System.out.println("CheckIn operation : Object "
							+ sysObj[i].getObjectName() + " " + myId[i]
							+ " is not checked out.");
				}
				node[count] = (IDfCheckinNode) operation.add(sysObj[i]);
				count++;
			} else {
				log.add("CheckIn operation : Object of id : " + myId[i]
						+ " can not be found.");
				DfLogger.warn(this, "CheckIn operation : Object of id : "
						+ myId[i] + " can not be found.", null, null);
			}
			// Other options for setCheckinVersion: VERSION_NOT_SET, NEXT_MAJOR,
			// NEXT_MINOR, BRANCH_VERSION

			if (count >= SIMULTANEOUS_CHECKIN - 1 || i >= myId.length - 1) {
				try {
					operation
							.setCheckinVersion(IDfCheckinOperation.SAME_VERSION);
					executeOperation(operation);
				} catch (DfException e) {
					System.out.println(e.getMessage());
					for (int l = 0; l < count; l++) {
						IDfSysObject obj = node[l].getObject();
						log.add(obj.getObjectId() + " : " + obj.getObjectName()
								+ " : " + e.toString());
					}
				} finally {
					for (int l = 0; l < count; l++) {
						IDfSysObject obj = node[l].getObject();
						log.add("CheckIn operation : CheckIn object name: "
								+ obj.getObjectName() + ":" + obj.getObjectId()
								+ node[l].getCheckinVersion());
						DfLogger.info(
								this,
								"CheckIn operation : CheckIn object name: "
										+ obj.getObjectName() + ":"
										+ obj.getObjectId()
										+ node[l].getCheckinVersion(), null,
								null);
						/*
						 * System.out
						 * .println("CheckIn operation : CheckIn object name: "
						 * + obj.getObjectName() + " : " + obj.getObjectId() +
						 * " : " + node[l].getCheckinVersion());
						 */
					}
					count = 0;
					operation = clientx.getCheckinOperation();
				}
			}
			i++;
		}
		return true;

	}

	/**
	 * Cancels the checkout lock on object
	 * 
	 * @param id
	 *            r_object_id of object
	 * @param session
	 *            IDfSession
	 * @throws DfException
	 */
	public synchronized void cancelCheckOut(IDfId id, IDfSession session)
			throws DfException {
		executeOperation((IDfOperation) clientx.getCancelCheckoutOperation()
				.add(session.getObject(id)));
	}

	/**
	 * Cancels the checkout lock on objects
	 * 
	 * @param id
	 *            array of r_object_id
	 * @param session
	 *            IDfSession
	 * @throws DfException
	 */
	public synchronized void cancelCheckOut(IDfId[] id, IDfSession session)
			throws DfException {

		IDfOperation operation = clientx.getCancelCheckoutOperation();

		IDfSysObject sysObj[] = new IDfSysObject[id.length];
		for (int i = 0; i < id.length; i++) {
			sysObj[i] = (IDfSysObject) session.getObject(id[i]);
			if (sysObj[i].isCheckedOut()) {
				operation.add(sysObj[i]);
				log.add("Cancel CheckOut operation : Cancelling checkout flag for :"
						+ sysObj[i].getObjectName());
				DfLogger.info(this,
						"Cancel CheckOut operation : Cancelling checkout flag for :"
								+ sysObj[i].getObjectName(), null, null);
			}
		}
		executeOperation(operation);
	}

	/**
	 * Add virtual child file into the parent file.
	 * 
	 * @param id
	 *            parent file r_object_id
	 * @param childId
	 *            array of r_object_id of child files
	 * @param session
	 *            IDfSession
	 * @throws DfException
	 */
	public synchronized void addVirtualChild(IDfId id, IDfId childId[],
			IDfSession session) throws DfException {
		object = (IDfSysObject) session.getObject(id);
		vDoc = object.asVirtualDocument("CURRENT", false);
		nodeRoot = vDoc.getRootNode();

		ArrayList alreadychildlist = new ArrayList();

		for (int child_count = nodeRoot.getChildCount(); child_count > 0; child_count--) {
			child = nodeRoot.getChild(child_count - 1);
			alreadychildlist.add(child.getChronId().getId());
		}

		int i = 0;
		int k = 0;
		if (childId.length >= SIMULTANEOUS_VIRTUAL) {
			k = SIMULTANEOUS_VIRTUAL;
		} else {
			k = childId.length;
		}
		while (i < childId.length) {
			if (object.isCheckedOut()) {
				executeOperation((IDfOperation) clientx
						.getCancelCheckoutOperation().add(object));
			}
			object.checkout();

			for (int j = 0; j < k; j++, i++) {
				IDfSysObject child = (IDfSysObject) session
						.getObject(childId[i]);
				if (!id.equals(childId[i])
						&& !(alreadychildlist.contains(childId[i].getId()))) {
					IDfVirtualDocumentNode nodeChild = this.vDoc.addNode(
							this.nodeRoot, null, child.getChronicleId(),
							"CURRENT", false, false);
					child.save();
					log.add("Child :" + child.getObjectName() + " : " + i);
					System.out.println("Child :" + child.getObjectName()
							+ " : " + i);
				}
			}
			object.save();
			k = childId.length - i;
			if (k >= SIMULTANEOUS_VIRTUAL) {
				k = SIMULTANEOUS_VIRTUAL;
			}

		}
	}

	/**
	 * Exports the Object from dctm to the mentioned destDir also create files
	 * with their r_object_id in temp folder
	 * 
	 * @param id
	 *            array of r_object_id
	 * @param destDir
	 *            Destination directory
	 * @param sess
	 *            IDfSession
	 * @throws DfException
	 * @throws IOException
	 * @throws InterruptedException
	 */

	public synchronized boolean doExportObject(IDfId[] id, String destDir,
			IDfSession sess) throws DfException, IOException,
			InterruptedException {
		File f = new File(destDir);
		f.mkdir();
		IDfExportOperation operation = clientx.getExportOperation();
		operation.setDestinationDirectory(destDir);
		IDfExportNode[] node = new IDfExportNode[SIMULTANEOUS_EXPORT];
		IDfSysObject[] myObj = new IDfSysObject[id.length];
		IDfFile[] destFile = new IDfFile[id.length];
		int i = 0, count = 0;
		while (i < id.length) {
			myObj[i] = (IDfSysObject) sess.getObject(id[i]);
			destFile[i] = clientx.getFile(destDir
					+ myFileFunctions.fileSeparator + myObj[i].getObjectName());
			if (!destFile[i].exists()) {
				// add the IDfSysObject to the operation
				node[count] = (IDfExportNode) operation.add(myObj[i]);
				count++;
			}
			if (count >= SIMULTANEOUS_EXPORT || i >= id.length - 1) {
				try {
					boolean status = executeOperation(operation);

				} catch (DfException e) {
					log.add("Export operation : " + e.getMessage());
				} finally {
					operation = clientx.getExportOperation();
					operation.setDestinationDirectory(destDir);
					count = 0;
				}
			}
			i++;
		}
		return true;
	}

	public synchronized boolean doBulkExport(String location, String destDir,
			IDfSession sess) throws DfIdentityException,
			DfAuthenticationException, DfPrincipalException,
			DfServiceException, DfException, IOException, InterruptedException {

		String destfolder;
		String temp1;
		if (location != null && destDir != null) {

			File folderlocation = new File(destDir);
			if (!(folderlocation.exists())) {
				folderlocation.mkdir();
			}

			IDfId[] objectids = getObjectID(null, location, "dm_document",
					false, sess);
			doExportObject(objectids, destDir, sess);

			IDfId[] folderids = getObjectID(null, location, "dm_folder", false,
					sess);

			if (folderids.length > 0) {
				IDfSysObject ob;
				for (int i = 0; i < folderids.length; i++) {

					ob = (IDfSysObject) sess.getObject(folderids[i]);

					temp1 = location + "/"
							+ ob.getObjectName().replaceAll("'", "''");
					destfolder = destDir + myFileFunctions.fileSeparator
							+ ob.getObjectName();
					System.out.println("Export :" + temp1 + " "
							+ doBulkExport(temp1, destfolder, sess));
				}
			}
		}
		return true;
	}

	/**
	 * Remove all child files of the virtual object.
	 * 
	 * @param session
	 *            IDfSession of docbase.
	 * @param object
	 *            IDfSysObject or virtual object.
	 * @throws DfException
	 */
	public synchronized void removeAllNode(IDfSysObject object,
			IDfSession session) throws DfException {
		System.out.println("Remove Virtual Nodes : " + object.getObjectName());
		log.add("Remove Virtual Nodes : " + object.getObjectName());
		if (object.isVirtualDocument()) {
			vDoc = object.asVirtualDocument("Current", false);
			nodeRoot = vDoc.getRootNode();
			int child_count = nodeRoot.getChildCount();
			if (child_count > 0) {
				while (child_count > 0) {
					object.checkout();
					child = nodeRoot.getChild(--child_count);
					vDoc.removeNode(child);
					object.save();
					vDoc.resync(session, child.getSelectedObject()
							.getObjectId(), 0);
					child_count = nodeRoot.getChildCount();
					log.add("Remove Virtual Nodes :"
							+ child.getSelectedObject().getObjectName()
							+ " was removed from the Virtual Document. "
							+ child_count);
					System.out.println("Remove Virtual Nodes :"
							+ child.getSelectedObject().getObjectName()
							+ " was removed from the Virtual Document. "
							+ child_count);
				}
			}
		} else {
			log.add("Remove Virtual Nodes : " + object.getObjectName()
					+ " is not virtual file.");
			System.out.println("Remove Virtual Nodes : "
					+ object.getObjectName() + " is not virtual file.");
		}
	}

	/**
	 * Set Lifecycle to the Object.
	 * 
	 * @param object
	 *            r_object_id of object on which lifecycle to apply.
	 * @param businessPolicy
	 *            r_object_if
	 * @param sess
	 * @throws DfException
	 */
	public synchronized void setBusinesspolicy(IDfId object,
			IDfId businessPolicy, IDfSession sess) throws DfException {
		((IDfSysObject) sess.getObject(object)).attachPolicy(businessPolicy,
				"preliminary", "");
	}

	public synchronized void getRendition(IDfId id, IDfSession sess)
			throws DfException, IOException, InterruptedException {
		String query = "select r_object_id from dmr_content where any parent_id='"
				+ id + "'";
		IDfCollection col = this.ExecuteReadQuery(query, sess);
		ArrayList list = new ArrayList();
		while (col.next()) {
			IDfValue val = col.getValue("r_object_id");
			list.add(val.asString());
		}
		String[] renditionIdArray = (String[]) list.toArray(new String[0]);
		if (renditionIdArray.length > 0) {
			IDfId[] renditionId = new IDfId[renditionIdArray.length];
			for (int i = 0; i < renditionIdArray.length; i++) {
				renditionId[i] = new DfId(renditionIdArray[i]);
			}
			this.doExportObject(renditionId, "./Export", sess);
		}
	}

	/**
	 * Sets rendition to the object of mentioned r_object_id.
	 * 
	 * @param id
	 *            r_object_id
	 * @param renditionFile
	 *            location of rendition file.
	 * @param renditionType
	 *            rendition type.
	 * @param sess
	 * @throws DfException
	 */
	public synchronized void setRendition(IDfId id, String renditionFile,
			String renditionType, IDfSession sess) throws DfException {
		object = (IDfSysObject) sess.getObject(id);
		if (!object.isCheckedOut()) {
			object.addRendition(renditionFile, renditionType);
		} else {
			log.add(object.getObjectName()
					+ " Object is CheckOut, needs to be checkIn before setting rendition");
			new DfException(
					object.getObjectName()
							+ " Object is CheckOut, needs to be checkIn before setting rendition");
		}
		object.save();
	}

	/**
	 * Executes read query or select query and return IDfCollection.
	 * 
	 * @param query
	 *            query in String format.
	 * @param session
	 *            IDfSession
	 * @return IDfCollection
	 * @throws DfException
	 */
	public synchronized IDfCollection ExecuteReadQuery(String query,
			IDfSession session) throws DfException {
		queryObj = clientx.getQuery();
		queryObj.setDQL(query);
		DfLogger.info(this, "Query :" + query, null, null);
		return queryObj.execute(session, IDfQuery.DF_READ_QUERY);
	}

	/**
	 * Create copy of given object at the mentioned location.
	 * 
	 * @param id
	 *            r_object_id that need to be copied.
	 * @param destFolder
	 *            IDfFolder object of destionation folder.
	 * @param sess
	 *            IDfSession.
	 * @return true if successful.
	 * @throws DfException
	 */
	public synchronized boolean doCopyOperation(IDfId id, IDfFolder destFolder,
			IDfSession sess) throws DfException {
		IDfSysObject sysObj = (IDfSysObject) sess.getObject(id);
		IDfCopyOperation operation = clientx.getCopyOperation();
		operation.setDestinationFolderId(destFolder.getObjectId());
		operation.setDeepFolders(true);

		IDfCopyNode node;
		if (sysObj.isVirtualDocument()) {
			IDfVirtualDocument vDoc = sysObj
					.asVirtualDocument("CURRENT", false);
			node = (IDfCopyNode) operation.add(vDoc);
		} else {
			node = (IDfCopyNode) operation.add(sysObj);
		}

		executeOperation(operation);
		return true;

	}

	/**
	 * Rename object_name.
	 * 
	 * @param id
	 *            r_object_id of object.
	 * @param newName
	 *            String new object_name
	 * @param session
	 *            IDfSession.
	 * @return true if successful.
	 * @throws DfException
	 */
	public synchronized boolean doRenameSysObject(IDfId id, String newName,
			IDfSession session) throws DfException {
		IDfSysObject ob = (IDfSysObject) session.getObject(id);
		if (!ob.getObjectName().equals(newName)) {
			ob.setObjectName(newName);
			ob.save();
		} else {
			return false;
		}
		return true;
	}

	public synchronized IDfDocument doImport(String object_name,
			String object_type, String a_content_type, IDfFolder folder,
			String filePath, IDfSession session) throws DfException {
		IDfDocument document = (IDfDocument) session.newObject(object_type);
		document.setObjectName(object_name);
		document.setContentType(a_content_type);
		// setFileEx parameters (fileName,formatName,pageNumber,otherFile)
		document.setFileEx(filePath, a_content_type, 0, null);

		// Specify the folder in which to create the document
		document.link(folder.getObjectId().toString());

		// Save the document in the Docbase
		document.save();

		return document;
	}

	public synchronized boolean doImportOperation(File srcFileOrDir,
			IDfFolder destFolder, IDfSession sess) throws DfException {
		IDfImportOperation operation = clientx.getImportOperation();
		// the Import Operation requires a session
		operation.setSession(sess);
		operation.setDestinationFolderId(destFolder.getObjectId());
		String[] files = srcFileOrDir.list();
		IDfFile[] myFile = new IDfFile[files.length];
		for (int i = 0, j = 0; i < files.length; i++) {
			myFile[i] = clientx.getFile(files[i]);
			if (myFile[i].exists()) {
				operation.add(myFile[i]);
			}
		}
		operation.execute();

		IDfList myNodes = operation.getNodes();
		int iCount = myNodes.getCount();
		System.out.println("Number of nodes after operation: " + iCount);
		for (int i = 0; i < iCount; ++i) {
			IDfImportNode aNode = (IDfImportNode) myNodes.get(i);
			System.out.print("Object ID " + i + ": "
					+ aNode.getNewObjectId().toString() + "  ");
			System.out.println("Object name: " + aNode.getNewObjectName());
		}
		return false;
	}

	/**
	 * Description about object.
	 * 
	 * @return String info of this object
	 */
	public String toString() {
		String tmp;
		tmp = clientx.toString();
		IDfProperties p = clientx.getProperties();
		int i = p.getCount();
		try {
			IDfList l = p.getProperties();
			int j = 0;
			while (j < i) {
				tmp += l.get(i).toString();
			}
		} catch (DfException ex) {
			log.add(ex.getMessage());
			ex.printStackTrace();
			DfLogger.error(this, ex.getMessage(), null, ex);
		}
		return tmp;
	}

	public void setLogger(ArrayList list) {
		list.addAll(this.log);
		if (list != null)
			log = list;
	}

	public static void main(String[] args) {
		// System.out.println(System.getProperty("java.class.path"));
		System.out.println("Retreiving Documentum's client variables:"
				+ DctmClient.getInstance().toString());
	}

}
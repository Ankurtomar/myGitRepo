package org.hcl.documentum.myTool;

import com.documentum.fc.client.IDfQuery;
import java.util.Date;

import org.hcl.IO.myTool.Excel;
import org.hcl.IO.myTool.myFileFunctions;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

/**
 * Extract Thread.
 * @author Ankur_Tomar
 */
public class ExtractThread {

    private Thread t;
    private final DctmClient myClient;
    private IDfQuery queryObj;
    private final String location;
    private final String myquery, filename;
    private boolean status;
    private Date d;
    private int count;

    /**
     * Initiates the Extracter Thread.
     * @param client Object of Dctm_Client
     * @param query DQL
     * @param location path of repository.
     * @throws DfException
     */
    public ExtractThread(DctmClient client, String query, String loc) throws DfException {
        status = false;
        this.location = loc;
        this.myquery = query;
        this.myClient = client;
        filename = myFileFunctions.addDateToFilename(location);

        t = new Thread() {

            public void run() {
                try {
                    IDfSession sess = myClient.getSessionManager().newSession(myClient.getDocbase());
                    Excel.writeExcel(myClient.ExecuteReadQuery(myquery, sess), filename);
                    status = true;
                    System.out.println(location + " complete...");
                    
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        this.count = count;
        System.out.println(location + " started...");
        t.start();
    }

    /**
     *
     * @return returns path of repository.
     */
    public String getLocation() {
        return location;
    }

    /**
     *
     * @return returns status of thread.
     */
    public boolean getStatus() {
        return status;
    }
}

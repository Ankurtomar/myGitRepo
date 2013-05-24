package org.hcl.documentum.myTool;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;

public class myExportOperation {
	
	static Pattern p;
    static Matcher m;
	
	public static void main(String[] args) throws FileNotFoundException, IOException, DfException, InterruptedException{
		
		String location="",temp;
		String destDir="C:\\Documentum\\Export";
		
		System.out.println("Please enter the Dctm location for Export Objects:");
		BufferedReader br = new BufferedReader(new InputStreamReader(
                System.in));
		location = br.readLine();
		
		System.out.println("Please enter the destination directory:");
		br = new BufferedReader(new InputStreamReader(
                System.in));
		temp = br.readLine();
		
		if(temp!=null){
			destDir=temp;
		}
		
		if(location!=""){
			p = Pattern.compile("");
			m = p.matcher(location);
			if(m.find()){
				DctmClient client=DctmClient.getInstance();
				IDfSession sess=client.getSessionManager().newSession(client.getDocbase());
				client.doBulkExport(location, destDir, sess);
			}
		}
		System.out.println(".........Done............");
	}

}

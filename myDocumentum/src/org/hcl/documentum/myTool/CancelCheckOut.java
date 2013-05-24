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

public class CancelCheckOut {

	static Pattern p;
	static Matcher m;

	public static void main(String[] a) throws FileNotFoundException,
			IOException, DfException {

		String location = "";

		System.out
				.println("Please enter the Dctm location for Cancel CheckOut of Objects:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		location = br.readLine();
		if (location != "") {
			p = Pattern.compile("");
			m = p.matcher(location);
			if (m.find()) {
				DctmClient client = DctmClient.getInstance();
				IDfSession sess = client.getSessionManager().newSession(
						client.getDocbase());
				IDfId[] id = client.getObjectID(null, location, "dm_document",true,sess);
				client.cancelCheckOut(id, sess);
			}
		}
		System.out.println(".........Done............");
	}
}

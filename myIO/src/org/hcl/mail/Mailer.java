package org.hcl.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Mailer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// String to = "prashant.kumar3@pearson.com";
		// String to = "himadri.pant@hcl.com";
		String to = "manish.aggarwal@pearson.com";
		// String to = "sushant.kumar3@pearson.com";

		// Sender's email ID needs to be mentioned
		String from = "AssetLibrary@hcl.com";
		// String from = "AssetLibrary@pearson.com";

		// Assuming you are sending email from localhost
		// String host = "10.98.10.211";
		String host = "10.64.160.70";

		// Get system properties
		Properties properties = System.getProperties();

		// Setup mail server
		properties.setProperty("mail.smtp.host", host);

		// Get the default Session object.
		Session session = Session.getDefaultInstance(properties);

		try {
			// Create a default MimeMessage object.
			MimeMessage message = new MimeMessage(session);

			// Set From: header field of the header.
			message.setFrom(new InternetAddress(from));

			// Set To: header field of the header.
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
					to));
//			message.addRecipient(Message.RecipientType.TO, new InternetAddress(
//					"manish.aggarwal@pearson.com"));

			// Set Subject: header field
			message.setSubject("Asset Download SU2_hires_request");

			// Send the actual HTML message, as big as you like
			// message.setContent("<!DOCTYPE html><html><body><style type=\"text/css\">p{font-family:\"verdana\";font-size:12px; color:\"black\"} table{font-family:\"verdana\";font-size:12px} td{text-align:center} th{padding:10px}<p>Welcome to Asset Library - Asset Download.</p><p>Below are the details of the assets you requested for download.</p><b>If you have requested multiple assets, these may have been split across several emails.</b><p><b>A list of the requested assets has been provided below.</b></p><table><tr><th>Request Date</th><th>Requestor</th><th>Project Identifier Ref</th><th>Project Title</th><th>Placement</th><th>Number of Assets</th></tr><tr><td>7/21/2012 3:07:53 PM</td><td>internal_asset_manager1</td><td>asd</td><td>asd</td><td>Electronic Content</td><td>3</td></table><p> </p><p><u><b>Asset Information:</b></u></p><p> </p><p><b>Asset Id: </b>AL47065  Test Caption  Penguin_UK_DK</p><p><b>Credit Line: </b>Credit Line</p><p> </p><p><b>Asset Id: </b>AL47066  Test Caption  Penguin_UK_DK</p><p><b>Credit Line: </b>Credit Line</p><p> </p><p><b>Asset Id: </b>AL47064  Test Caption  Penguin_UK_DK</p><p><b>Credit Line: </b>Credit Line</p></style></body></html>","text/html; charset=utf-8");
			// message.setContent("Welcome to Asset Library - Asset Download. \nDetails of the request has been provided below. \nRequest Date: 1/30/2013 8:11:29 PM \nNumber of Assets: 2 \nRequestor: internal_asset_manager2'","text/html; charset=utf-8");
			// message.setContent("Welcome to Asset Library - Asset Download","text/html; charset=utf-8");
			message.setContent(
					// "<p>Below are the details of the assets you requested for download from the Asset Library. If you have requested multiple assets, these may have been split across several emails.</p><p><b>Please note: All assets are to be used in strict accordance with the Asset Library <a href=\"http://10.113.124.215:28080/assetlibrary-web/termsPage.jsp\">terms and conditions<a>.</b></p><p>A list of the requested assets has been provided below.</p><table border='0'><tr><td>Request Date</td><td>2/12/2013 11:56:20 AM</td></tr><tr><td>Requestor</td><td>standard_user1</td></tr><tr><td>Project Identifier Ref</td><td>56</td></tr><tr><td>Project Title</td><td>67</td></tr><tr><td>Placement</td><td>Back Cover</td></tr><tr><td>Number of Assets</td><td>1</td></tr></table><br/><p><b>ASSET INFORMATION</b></p><br/><p><b>Asset Id: </b>AL14017  Test Caption  PI_UK_CM</p><p><b>Asset Manager Comment: </b>bug's status</p><p><b>Credit Line: </b> updated</p><p><b>Model Release: </b>No</p><p><b>Restrictions: </b>Tested</p><p><b>It is your responsibility to ensure that you comply with any restrictions described above and that any restrictions do not prevent your intended use.</b></p><br/><br/>",
					"<p>Below are the details of the assets you requested for download from the Asset Library. If you have requested multiple assets, these may have been split across several emails.</p><p><b>Please note: All assets are to be used in strict accordance with the Asset Library <a href=\"http://10.113.124.215:28080/assetlibrary-web/termsPage.jsp\">terms and conditions<a>.</b></p><p>A list of the requested assets has been provided below.</p><table border='0'><tr><td>Request Date</td><td>4/2/2013 8:25:06 AM</td></tr><tr><td>Requestor</td><td>standard_user1</td></tr><tr><td>Project Identifier Ref</td><td>12312313123123</td></tr><tr><td>Project Title</td><td>123</td></tr><tr><td>Placement</td><td>Flashcard</td></tr><tr><td>Number of Assets</td><td>1</td></tr></table><br/><p><b>ASSET INFORMATION</b></p><br/><p><b>Asset Id: </b>AL25173  Fireworks exploding in night sky  PI_UK_CM</p><p><b>Asset Manager Comment: </b>kshdkahh's d;jljgj``dfgljdjgd's ~~@#$$%%^^&*&*(*()_+_{|}|}\":",
					"text/html; charset=utf-8");
			// Send message
			Transport.send(message);
			System.out.println("Sent message successfully....");
		} catch (Exception mex) {
			mex.printStackTrace();
		}

	}

}

package org.hcl.documentum.myTool;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import com.documentum.com.DfClientX;
import com.documentum.com.IDfClientX;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfLoginInfo;

public class myRendition {
	public static void main(String args[])throws IOException{
		
			myRendition r=new myRendition();
			
			InputStream fin;
			POIFSFileSystem fsys;
			HSSFWorkbook wbook;
			HSSFSheet xlssheet;
			HSSFCell cell;
			
			short i , j ;
			int lastrownum,lastCell,k1;
			String token_name="";
			int count=0;
			short row1=1,cell1=1;
			
			ArrayList al=new ArrayList();
			ArrayList a2=new ArrayList();
			ArrayList a3=new ArrayList();
			
			BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
			IDfSessionManager sMgr=null;
			IDfSession session=null;
			
			String docbase=null;
			String user=null;
			String pass=null;
			String option=null;
			String content=null;
	// Entering credentials-----------------------------------------------------------
		try{
			IDfClientX clientx = new DfClientX();
			/*System.out.println("Enter ur Docbase :");
			 docbase=br.readLine();
			 System.out.println("Enter ur Login name :");
			 user=br.readLine();
			 System.out.println("Enter ur Password :");
			 pass=br.readLine();*/
			 sMgr = r.createSessionManager("BFW_docbase","hcl","Tyr9!Q*");
				
				//IDfClientX clientx = new DfClientX();
				session=sMgr.newSession("BFW_docbase");
			
	//	 reading excel sheet 1--------------------------------------------------------------------------------------
			
				fin = new FileInputStream("rendition.xls");		
				fsys 			= new POIFSFileSystem( fin );
				wbook 			= new HSSFWorkbook(fsys);
				xlssheet 		= wbook.getSheetAt(0);		
				lastrownum		= xlssheet.getLastRowNum();	
				
				for(int m=1;m<=lastrownum;m++)		
				{
					HSSFRow row	=xlssheet.getRow(m);
					if(row==null)					//check rows if they are empty 
					{
						continue;
					}
					lastCell = row.getLastCellNum();
					
					i=(short)lastCell;
					for(j=0;j<i;j++)
					{
						
						cell=row.getCell(j);
						
						if(cell==null)
						{
							al.add("");
							continue;						
						}
						
						switch (cell.getCellType() )
						{
						case HSSFCell.CELL_TYPE_NUMERIC:	al.add(new Double(cell.getNumericCellValue()));
						break;
						
						case HSSFCell.CELL_TYPE_BLANK:	    al.add("");
						break;
						
						case HSSFCell.CELL_TYPE_FORMULA:	al.add(cell.getCellFormula());
						break;
						
						case HSSFCell.CELL_TYPE_STRING:   	al.add(cell.getStringCellValue().toString());
						break;
						
						case HSSFCell.CELL_TYPE_ERROR: 
							break;
							
						case HSSFCell.CELL_TYPE_BOOLEAN:  
							break;
							
						default:		al.add("");				
						break;
						}
					}
				}
			
	//	fetching output of sheet1(code not necessary)--------------------------------------------------------------------------------
			
				//System.out.println("Size Al : "+al.size());
				Object obj[]=al.toArray();
				System.out.println("---------------------SHEET 1--------------------------");
				for (int k=0; k<al.size(); k+=4){  //outer loop: to move throughout the excel sheet
					System.out.println("ID     : "+obj[k]);
					System.out.println("Name   : "+obj[k+1]);
					System.out.println("R_Name : "+obj[k+2]);
					System.out.println("R_Type : "+obj[k+3]);
					//add rendition-------------------------------------------------------------------------------------
					IDfId myId = clientx.getId(obj[k].toString());							
					IDfSysObject document = (IDfSysObject)session.getObject(myId);
			        
			        if( document == null ) {
			            System.out.println("Document does not exist in the Docbase!");
			            return;
			        }
			        
			        if( document.isCheckedOut() ) {
			            System.out.println("The document is checked out. Check in the document before creating a rendition.");
			            return;
			        }
			        
			        //add "filePath" as a "format" rendition of the document
			        //example filePath "c:/chap_1.html"
			        //example format "html"
			        //String filepath="C:\\Documents and Settings\\sourav.dey\\workspace\\bfw\\new html rendition\\"+obj[k+2].toString();
			        String filepath="Rendition\\"+obj[k+2].toString();
			        document.addRendition(filepath,(String)obj[k+3]);
			        System.out.println(obj[k+3]+" rendition added");
			        document.save();

				}
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			if(session!=null){
				sMgr.release(session);
				
			}
		}
	}
	
// method createSessionManager	
	IDfSessionManager createSessionManager(String docbase, String user, String pass)
    throws DfException {
		//create Client objects
        IDfClientX clientx = new DfClientX();
        IDfClient client = clientx.getLocalClient();
		//create a Session Manager object
        IDfSessionManager sMgr = client.newSessionManager();
        
        try
        {

        //create an IDfLoginInfo object named loginInfoObj
        IDfLoginInfo loginInfoObj = clientx.getLoginInfo();
        loginInfoObj.setUser(user);
        loginInfoObj.setPassword(pass);
        //loginInfoObj.setDomain(null);
        
        //bind the Session Manager to the login info
        sMgr.setIdentity(docbase, loginInfoObj);
        //could also set identity for more than one Docbases:
        // sMgr.setIdentity( strDocbase2, loginInfoObj );
        //use the following to use the same loginInfObj for all Docbases (DFC 5.2 or later)
        // sMgr.setIdentity( * , loginInfoObj );
       
        }
        catch(DfException ex)
        {
        	System.out.println("Exception : "+ex);
        }
        return sMgr;
    }
}

/**
 ************************************************************************************************ 
 * Copyright (C) 2012 
 * Pearson Plc. All Rights Reserved 
 * Project : Asset Library 
 * File : ResultItemBean.java
 * Created By : Manju.Singh
 * Creation Time : Jan 18, 2012
 * 
 * Description :
 *              
 * 
 ************************************************************************************************ 
 */

package org.anks2089.bean;

import java.io.Serializable;

import lombok.Data;

/**
 * @author Manju Singh This class will provide the getters & setters for result item which will used in displaying the
 *         search result
 * 
 * @version $Revision: 1.0 $
 */

@SuppressWarnings("serial")
@Data
public class ResultItemBean implements Serializable {

    // Boxing - Change Request
    private String assetName; // c_asset_id
    private String assetId; // r_object_id
    private String CCaption;// c_caption
    private String CPermTempName;// c_permissions_template_name
    private long CExclusiveEndDate;// c_exclusivity_end_date
    private String CRestrictionsText; // c_restriction_text

    // Asset Id to take a alpha character - Business CR
    private String CRenditionPath; // c_rendition_path
    
    private String aclName; //aclName
    
    private String adminBusiness; //c_admin_business
}

/**
 ************************************************************************************************ 
 * Copyright (C) 2012 
 * Pearson Plc. All Rights Reserved 
 * Project : Asset Library 
 * File : SeachQueryBean.java
 * Created By : Manju.Singh
 * Creation Time : Jan 18, 2012
 * 
 * Description :
 *              
 * 
 ************************************************************************************************ 
 */

package org.anks2089.bean;

import java.util.List;
import java.util.Map;

import lombok.Data;

import org.anks2089.elasticsearch.SearchOperatorEnum;
import org.apache.commons.lang.StringUtils;


/**
 * @author Manju Singh This class will provide the getters & setters for result item which will used in displaying the
 *         search result
 * 
 * @version $Revision: 1.0 $
 */

@SuppressWarnings("serial")
@Data
public class SearchQueryBean {

    private static final String CHKBOX_ON = "on";

    private String userRole;
    private String sortAttribute;
    private String orgSearchText;
    private String searchText;
    private String filterTerms;
    private String photoCheckbox;
    private String artworkCheckbox;
    private String startIndex;
    private String maxResult;
    private String sortType;
    private String rightsTemplate;
    private String facetsTerms;
    private String option;
    private SearchOperatorEnum conditionOperator;
    private List<String> conditionField;
    private List<String> conditionType;
    private List<String> conditionText;
    private String lightBoxCondition;
    private String lightBoxStatusCondition;
    private String assetUsagesCondition;
    private Map<String, String> exclusionList;

    private List<String> gettyKeyword;
    private List<String> basicKeyword;
    private List<String> returnAttr;
    
    private List<Facet> facetList;
    
    /**
     * @param photoCheckbox
     *            the photoCheckbox to set
     */
    public void setPhotoCheckbox(final String photoCheckbox) {
        if (StringUtils.equalsIgnoreCase(CHKBOX_ON, photoCheckbox)
                || StringUtils.equalsIgnoreCase(String.valueOf(true), photoCheckbox)) {
            this.photoCheckbox = String.valueOf(true);
        } else {
            this.photoCheckbox = String.valueOf(false);
        }
    }

    /**
     * @param artworkCheckbox
     *            the artworkCheckbox to set
     */
    public void setArtworkCheckbox(final String artworkCheckbox) {
        if (StringUtils.equalsIgnoreCase(CHKBOX_ON, artworkCheckbox)
                || StringUtils.equalsIgnoreCase(String.valueOf(true), artworkCheckbox)) {
            this.artworkCheckbox = String.valueOf(true);
        } else {
            this.artworkCheckbox = String.valueOf(false);
        }

    }

}

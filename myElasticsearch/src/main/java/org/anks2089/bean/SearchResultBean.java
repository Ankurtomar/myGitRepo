/**
 ************************************************************************************************ 
 * Copyright (C) 2012 
 * Pearson Plc. All Rights Reserved 
 * Project : Asset Library 
 * File : SearchResultBean.java
 * Created By : PeeyushK
 * Creation Time : Jan 21, 2012
 * 
 * Description :
 *              
 * 
 ************************************************************************************************ 
 */

package org.anks2089.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author PeeyushK
 * 
 * @version $Revision: 1.0 $
 */
@Data
@SuppressWarnings("serial")
public class SearchResultBean implements Serializable {

	private List<ResultItemBean> lstSearchResult;
	private Map<String, Map<String, String>> facetbean;
	private int resultCount; 

}

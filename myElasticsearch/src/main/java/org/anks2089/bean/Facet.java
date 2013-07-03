/**
 ************************************************************************************************ 
 * Copyright (C) 2012 
 * Pearson Plc. All Rights Reserved 
 * Project : Asset Library 
 * File : Facet.java
 * Created By : Ankur_Tomar
 * Creation Time : Dec 20, 2012
 * 
 * Description :
 *              
 * This class is used to configure facet's search.
 ************************************************************************************************ 
 */

package org.anks2089.bean;

import lombok.Data;

/**
 * @author Ankur_Tomar
 * 
 */
@Data
public class Facet {
    private String key;
    private String type;
    private String name;
    private String size;
}

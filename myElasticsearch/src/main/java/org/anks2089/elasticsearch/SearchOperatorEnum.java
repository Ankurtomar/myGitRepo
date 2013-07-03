/**
 ************************************************************************************************ 
 * Copyright (C) 2012 
 * Pearson Plc. All Rights Reserved 
 * Project : Asset Library 
 * File : SearchOperatorEnum.java
 * Created By : Manav Leslie
 * Creation Time : 15 October 2012
 * 
 * Description :This enumeration holds the different operators
 *              
 * 
 ************************************************************************************************ 
 */
package org.anks2089.elasticsearch;

/**
 * 
 * @version $Revision: 1.0 $
 */

public enum SearchOperatorEnum {
    AND_OPERATION("and_operation"), OR_OPERATION("or_operation");

    private final String operatorValue;

    /**
     * Protected constructors.
     * 
     * @param values operator value.
     */
    SearchOperatorEnum(String values) {
        this.operatorValue = values;
    }

    /**
     * Gets the operator value.
     * 
     * @return operator String value.
     */
    public String getValue() {
        return operatorValue;
    }

    /**
     * This method Retrieve the opeartorNames.
     * 
     * @param name String key.
     * @return value.
     */
    public static SearchOperatorEnum find(String name) {
        for (SearchOperatorEnum lang : SearchOperatorEnum.values()) {
            if (lang.getValue().contains(name)) {
                return lang;
            }
        }
        return null;
    }

    /**
     * Method to Retrieve operator Names from enums.
     * 
     * @param name  String key.
     * @return value.
     */
    public static SearchOperatorEnum findByName(String name) {
        for (SearchOperatorEnum lang : SearchOperatorEnum.values()) {
            if (lang.name().contains(name)) {
                return lang;
            }
        }
        return null;
    }

}


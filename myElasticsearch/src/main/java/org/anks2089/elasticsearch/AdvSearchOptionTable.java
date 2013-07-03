/**
 ************************************************************************************************ 
 * Copyright (C) 2012 
 * Pearson Plc. All Rights Reserved 
 * Project : Asset Library 
 * File : AdvSearchOptionTable.java
 * Created By : Ankur_Tomar
 * Creation Time : Jan 14, 2013
 * 
 * Description :
 *              
 * 
 ************************************************************************************************ 
 */

package org.anks2089.elasticsearch;

import java.util.List;
import java.util.StringTokenizer;

import lombok.Data;

import org.anks2089.bean.PrsnLibAssetLightbox;
import org.anks2089.bean.PrsnLibAssetUsage;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ankur_Tomar
 * 
 */
@Data
public class AdvSearchOptionTable implements IElasticSearchUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvSearchOptionTable.class);

    private String objectType;
    private String attributeName;
    private String searchableAttr;
    private String attributeType;
    private boolean isParent;
    private boolean isRepeating;

    /**
     * Returns the AdvSearchOptionTable object to construct filter as per attribute type.
     * @param basicAttr List of basic search attributes.
     * @param condition String containing info. about table, attribute name, attribute type and is it repeating information as
     *            table-attribute-type-repeating.
     * @return AdvSearchOptionTable.
     */
    public static AdvSearchOptionTable parseCondition(final List<String> basicAttr, final String condition) {
        LOGGER.trace(condition);
        AdvSearchOptionTable srchOption = new AdvSearchOptionTable();

        final StringTokenizer tkrs = new StringTokenizer(condition, HYPHEN_SEPARATOR);
        String objectType = tkrs.nextToken();
        final String attribute = tkrs.nextToken();
        final String attributeType = tkrs.nextToken();
        final boolean isRepeating = StringUtils.equalsIgnoreCase(tkrs.nextToken(), REPEATING_FLAG_CHAR);

        objectType = ES_NODE_ASSETS;
        srchOption.setParent(true);
        if (StringUtils.startsWithIgnoreCase(condition, PrsnLibAssetLightbox.LIGHTBOX_TYPE_NAME)) {
            objectType = ES_NODE_LIGHTBOX;
            srchOption.setParent(false);
        } else if (StringUtils.startsWithIgnoreCase(condition, PrsnLibAssetUsage.PRSN_LIB_ASSET_USAGE)) {
            objectType = ES_NODE_USAGE;
            srchOption.setParent(false);
        }
        srchOption.setObjectType(objectType);

        if (basicAttr.contains(condition)) {
            if (isRepeating) {
                srchOption.setSearchableAttr(attribute + DOT_SEPARATOR + attribute + DOT_SEPARATOR
                        + ATTRIBUTE_FIELD_UNTOUCHED_STRING);
            } else {
                srchOption.setSearchableAttr(attribute + DOT_SEPARATOR + ATTRIBUTE_FIELD_UNTOUCHED_STRING);
            }
        } else {
            srchOption.setSearchableAttr(attribute);
        }
        srchOption.setAttributeName(attribute);
        srchOption.setAttributeType(attributeType);
        srchOption.setRepeating(isRepeating);

        LOGGER.trace(srchOption.toString());

        return srchOption;
    }
}

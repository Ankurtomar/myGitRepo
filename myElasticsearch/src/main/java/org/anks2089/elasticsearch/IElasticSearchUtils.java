/**
 ************************************************************************************************ 
 * Copyright (C) 2012 
 * Pearson Plc. All Rights Reserved 
 * Project : Asset Library 
 * File : IElasticSearchUtils.java
 * Created By : Ankur_Tomar
 * Creation Time : Oct 8, 2012
 * 
 * Description :
 *              
 * 
 ************************************************************************************************ 
 */

package org.anks2089.elasticsearch;

/**
 * @author Ankur_Tomar
 * 
 */
public interface IElasticSearchUtils {

    public static final String ELASTICSEARCH_INDEX = "assetlibrary";

    public static final String ELASTICSEARCH_SETTING = "client.transport.sniff";

    public static final int ELASTICSEARCH_FACET_SIZE = 200;

    /**
     * Search operators
     */
    public static final String OPERATOR_AND = "AND";
    public static final String OPERATOR_NOT = "NOT";
    public static final String OPERATOR_OR = "OR";

    /**
     * Search UI conditional operators.
     */
    public static final String CONDITION_OPR_CONTAINS_ATLEAST = "contains_at_least_one";
    public static final String CONDITION_OPR_HAS_NO_VALUE = "has_no_value";
    public static final String CONDITION_OPR_HAS_VALUE = "has_value";
    public static final String CONDITION_OPR_DOES_NOT_CONTAIN = "does_not_contain";
    public static final String CONDITION_OPR_IS = "is";
    public static final String CONDITION_OPR_ENDS_WITH = "ends_with";
    public static final String CONDITION_OPR_BEGINS_WITH = "begins_with";
    public static final String CONDITION_OPR_ONLYCONTAINS = "onlycontains";
    public static final String CONDITION_OPR_BEFORE = "before";
    public static final String CONDITION_OPR_AFTER = "after";

    /**
     * Asset type
     */
    public static final String ASSET_TYPE_ARTWORK = "artwork";
    public static final String ASSET_TYPE_PHOTOGRAPH = "photograph";

    /**
     * Blank term
     */
    public static final String BLANK_TERM = "";

    /**
     * Wildcard string
     */
    public static final String WILDCARD_STRING = "*";

    /**
     * Refernece to original version of attribute value.
     */
    public static final String ATTRIBUTE_FIELD_UNTOUCHED_STRING = "untouched";

    /**
     * Default permission template applied on assets.
     */
    public static final String DEFAULT_PERMISSION_TEMPLATE_ID = "0000000000000000";

    /**
     * prsn_lib_usage
     */
    public static final String ES_NODE_USAGE = "usage";
    /**
     * prsn_lib_lightbox
     */
    public static final String ES_NODE_LIGHTBOX = "lightbox";
    /**
     * prsn_lib_asset
     */
    public static final String ES_NODE_ASSETS = "assets";
    /**
     * prsn_lib_asset
     */
    public static final String ES_NODE_BULKTEMPLATE = "bulktemplate";

    /**
     * Application date format
     */
    public static final String APPLICATION_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    /**
     * 
     */
    public static final String USERROLE_SA = "SA";
    public static final String USERROLE_IAM = "IAM";
    public static final String USERROLE_EAM = "EAM";
    /**
     * Custom facet for get status of used assets.
     */
    public static final String C_HAS_USAGE_FACET = "c_has_usage_facet";

    /**
     * attributes of $ES_NODE_BULKTEMPLATE object.
     */
    public static final String OBJECT_ID = "object_id";
    public static final String MESSAGE = "message";
    /**
     * 
     */

    public static final String FACET_RIGHTF = "rightf";
    public static final String FACET_EXTERNALCHRG = "externalchrg";
    public static final String FACET_INTERNALCHRG = "internalchrg";
    public static final String FACET_RIGHTS_BLANK = "rights_blank";
    public static final String FACET_RIGHTS_WITH_BLANK = "rights_with_blank";
    public static final String FACET_HI_RES_PERM = "hiResPerm";
    public static final String FACET_EXTERNALCHRG_EXCLUDE = "externalchrg_exclude";
    public static final String FACET_INTERNALCHRG_EXCLUDE = "internalchrg_exclude";
    public static final String FACET_HI_RES_PERM_EXCLUDE = "hiResPerm_exclude";
    public static final String FACET_RIGHTF_EXCLUDE = "rightf_exclude";
    public static final String FACET_LOW_RES = "lowRes";
    public static final String FACET_USED = "used";
    public static final String FACET_KEY_BLANK = "blank";
    public static final String FACET_KEY_NOT_ = "not_";
    public static final String FACET_KEY_NO = "No";
    public static final String FACET_KEY_YES = "Yes";
    /**
     * 
     */
    public static final String CUSTOM_ATTR_HIRES_EXCLUDE = "hires_exclude";
    public static final String CUSTOM_ATTR_EXTERNAL_CHARGE_EXCLUDE = "external_charge_exclude";
    public static final String CUSTOM_ATTR_INTERNAL_CHARGE_EXCLUDE = "internal_charge_exclude";
    public static final String CUSTOM_ATTR_RIGHTS_EXCLUDE = "rights_exclude";
    public static final String CUSTOM_ATTR_HIRES = "hires";
    public static final String CUSTOM_ATTR_EXTERNAL_CHARGE = "external_charge";
    public static final String CUSTOM_ATTR_INTERNAL_CHARGE = "internal_charge";
    public static final String CUSTOM_ATTR_RIGHTS = "rights";
    public static final String CUSTOM_ATTR_GETTYKEYWORDS = "gettykeywords";
    /**
     * 
     */
    public static final String ATTR_DATE_TYPE = "date";
    /**
     * 
     */
    public static final String REPEATING_FLAG_CHAR = "T";
    /**
     * 
     */
    public static final String COMMA_SEPARATER = ",";
    public static final String DOT_SEPARATOR = ".";
    public static final String HYPHEN_SEPARATOR = "-";
    public static final String PIPE_SEPARATOR = "|";
    public static final String SINGLEQUOTE_STRING = "'";
    public static final String WHITESPACE_STRING = " ";
    public static final String[] CHAR_FOR_CONTAINS = new String[] { "(", ")" };
    public static final String CHAR_STRING_FOR_DOUBLEQUOTE = "\"";

    /**
     * 
     */
    public static final String SORT_ORDER_DESC = "DESC";
    /**
     * 
     */
    public static final String SORT_KEY_ASSET_CREATION_DATE = "AssetCreationDate";
    public static final String SORT_KEY_COLLECTION_ID = "CollectionID";
    public static final String SORT_KEY_COLLECTION_TITLE = "CollectionTitle";
    public static final String SORT_KEY_ORIGINAL_FILE_NAME = "OriginalFileName";
    public static final String SORT_KEY_FILE_NAME = "FileName";
    public static final String SORT_KEY_LAST_MODIFIED = "LastModified";
    public static final String SORT_KEY_DATE_INGESTED = "DateIngested";
    /**
     * char array of special characters.
     */
    public static final String[] SPECIAL_CHARS = {"~", "!", "@", "#", "$", "£", "%", "^", "&", 
        HYPHEN_SEPARATOR, PIPE_SEPARATOR};

}

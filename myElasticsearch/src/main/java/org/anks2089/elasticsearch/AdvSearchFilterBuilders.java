/**
 ************************************************************************************************ 
 * Copyright (C) 2012 
 * Pearson Plc. All Rights Reserved 
 * Project : Asset Library 
 * File : AdvSearchFilterBuilders.java
 * Created By : Ankur_Tomar
 * Creation Time : Jan 14, 2013
 * 
 * Description :
 *              
 * 
 ************************************************************************************************ 
 */

package org.anks2089.elasticsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.HasChildFilterBuilder;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.TermFilterBuilder;

/**
 * @author Ankur_Tomar
 * 
 */
@Data
public class AdvSearchFilterBuilders {
    private transient Map<AdvSearchOptionTable, RangeFilterBuilder> rangeFilterMap;
    private transient List<TermFilterBuilder> termFilterList;
    private transient List<HasChildFilterBuilder> childFilterList;
    private transient List<FilterBuilder> filterList;

    /**
     * 
     */
    public AdvSearchFilterBuilders() {
        super();
        rangeFilterMap = new HashMap<AdvSearchOptionTable, RangeFilterBuilder>();
        termFilterList = new ArrayList<TermFilterBuilder>();
        childFilterList = new ArrayList<HasChildFilterBuilder>();
        filterList = new ArrayList<FilterBuilder>();
    }

    /**
     * Returns true if any list has any criteria.
     * 
     * @return boolean
     */
    public boolean hasAdvSearchFilter() {
        boolean flag = false;
        if (!(rangeFilterMap.isEmpty() && termFilterList.isEmpty() && childFilterList.isEmpty() && filterList.isEmpty())) {
            flag = true;
        }
        return flag;
    }
}

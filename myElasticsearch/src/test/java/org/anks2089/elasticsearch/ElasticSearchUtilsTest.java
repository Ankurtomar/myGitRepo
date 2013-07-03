/**
 ************************************************************************************************ 
 * Copyright (C) 2012 
 * Pearson Plc. All Rights Reserved 
 * Project : Asset Library 
 * File : ElasticSearchUtilsTest.java
 * Created By : Ankur_Tomar
 * Creation Time : Oct 8, 2012  
 * 
 * Description :
 *              
 * 
 ************************************************************************************************ 
 */

package org.anks2089.elasticsearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List; 

import junit.framework.TestCase;

import org.anks2089.bean.SearchQueryBean;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ankur_Tomar
 * 
 */
@RunWith(value = Parameterized.class)
public class ElasticSearchUtilsTest extends TestCase {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticSearchUtilsTest.class);

    private Object[] expectedResult;
    private SearchQueryBean sqb;
    private int resultCount;

    /**
     * Constructor.
     * 
     * @param searchTerm
     *            search term.
     * @param expectedResult
     *            array of expected result.
     */
    public ElasticSearchUtilsTest(final String testType, final SearchQueryBean sqb, final Object[] expectedResult,
            final int count) {

        this.setName(testType);
        this.sqb = sqb;
        this.expectedResult = expectedResult;
        this.resultCount = count;

        this.sqb = ESClientTestUtil.INSTANCE.setBasicSearchAttr(this.sqb);
        this.sqb = ESClientTestUtil.INSTANCE.setGettyAttr(this.sqb);
        this.sqb = ESClientTestUtil.INSTANCE.setReturnSearchAttr(this.sqb);

        this.sqb.setArtworkCheckbox("false");
        this.sqb.setStartIndex("0");
        this.sqb.setMaxResult("50");
    }

    @Parameters
    public static Collection<Object[]> getData() throws Exception {

        List<List<String>> testCases = ESClientTestUtil.INSTANCE.sheetReader("advance");

        List<Object[]> param = new ArrayList<Object[]>();

        Iterator<List<String>> iter = testCases.iterator();
        iter.next();

        SearchQueryBean sqb = null;
        List<String> row = null;
        try {

            while (iter.hasNext()) {
                row = iter.next();
                final Iterator<String> rowIt = row.iterator();
                if (rowIt.hasNext() && row.size() > 7) {
                    final String testType = rowIt.next();
                    // row.next();

                    final String userRole = rowIt.next();
                    // final String searchTerm = rowIt.next();
                    final String advOpr = rowIt.next();

                    if (StringUtils.isNotBlank(userRole)) {
                        sqb = new SearchQueryBean();
                        sqb.setUserRole(userRole);
                        sqb.setSearchText("advTest OR basicTest");

                        if (StringUtils.isNotBlank(advOpr)) {

                            final String condField = rowIt.next();
                            final String condType = rowIt.next();
                            final String condText = rowIt.next();

                            LOGGER.trace("Adv search Data : {} -- {} -- {} -- {}", new String[] { advOpr, condField,
                                    condType, condText });

                            sqb.setConditionOperator(SearchOperatorEnum.findByName(advOpr));
                            sqb.setConditionField(Arrays.asList(StringUtils.splitByWholeSeparator(condField, ", ")));
                            sqb.setConditionType(Arrays.asList(StringUtils.splitByWholeSeparator(condType, ", ")));
                            sqb.setConditionText(Arrays.asList(StringUtils.splitByWholeSeparator(condText, ", ")));

                            List<String> tempList = new ArrayList<String>();
                            for (String str : sqb.getConditionText()) {
                                tempList.add(StringUtils.replace(str, "|", ","));
                            }
                            sqb.setConditionText(tempList);
                        }

                        String countt = rowIt.next().replace('#', ' ').trim();
                        int count = Integer.parseInt(countt);
                        String[] result = null;
                        if (rowIt.hasNext()) {
                            result = StringUtils.split(rowIt.next());
                        }

                        param.add(new Object[] { testType, sqb, result, count });
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.trace("Row :: {}", row);
            LOGGER.error("Problem in basic sheet, row number > {}", testCases.indexOf(row));
            throw e;
        }

        return param;

    }

    @Test
    public void testGetSearchResults() throws Exception {
        LOGGER.info("----------------- Start Test ----------------------------");
        LOGGER.info("Test case :: {}", this.getName());
        LOGGER.trace("-- {}", sqb);

        final List<String> result = ESClientTestUtil.INSTANCE.readResult(ElasticSearchUtils.getSearchResults(
                ESClientTestUtil.INSTANCE.getESClient(IElasticSearchUtils.ELASTICSEARCH_INDEX), sqb, false));
        boolean testStatus = false;

        LOGGER.debug("Result size - {}, subset - {}", result.size(), result);
        if (expectedResult instanceof String[] && expectedResult.length > 0) {
            final List<String> expected = Arrays.asList((String[]) expectedResult);

            LOGGER.debug("Expected result - {}", expected);
            testStatus = ESClientTestUtil.INSTANCE.checkResult(expected, result);

        } else {

            LOGGER.debug("Expected result - {}", resultCount);
            testStatus = ESClientTestUtil.INSTANCE.checkCount(resultCount, result.size());
        }

        if (!testStatus) {
            fail(this.getName() + ", Expected - " + resultCount + ", Search result count - " + result.size());
        }

        Assert.assertTrue("::: Test case passed. :::", testStatus);

        LOGGER.info("------------------- End Test ----------------------------");
    }
}

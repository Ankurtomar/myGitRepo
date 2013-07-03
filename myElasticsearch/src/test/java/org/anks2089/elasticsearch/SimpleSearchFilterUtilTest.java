/**
 ************************************************************************************************ 
 * Copyright (C) 2012 
 * Pearson Plc. All Rights Reserved 
 * Project : Asset Library 
 * File : SimpleSearchFilterUtilTest.java
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.anks2089.bean.SearchQueryBean;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.AfterClass;
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
public class SimpleSearchFilterUtilTest extends TestCase {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SimpleSearchFilterUtilTest.class);

	private String searchTerm;
	private Object[] expectedResult;
	private SearchQueryBean sqb;

	private int resultCount;

	/**
     * 
     */
	private SimpleSearchFilterUtilTest() {
		sqb = new SearchQueryBean();
		sqb = ESClientTestUtil.INSTANCE.setBasicSearchAttr(sqb);
	}

	/**
	 * Constructor.
	 * 
	 * @param searchTerm
	 *            search term.
	 * @param expectedResult
	 *            array of expected result.
	 */
	public SimpleSearchFilterUtilTest(final String testType,
			final String searchTerm, final Object[] expectedResult,
			final int count) {
		this();

		// this.searchTerm = StringUtils.lowerCase(searchTerm);
		this.searchTerm = searchTerm;
		this.expectedResult = expectedResult;
		this.resultCount = count;
		this.setName(testType);
	}

	/**
	 * Test method for
	 * {@link com.pearson.al.common.util.elasticsearch.SimpleSearchFilterUtil#groupSearchTerm(java.lang.String)}
	 * .
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGroupSearchTerm() throws Exception {

		LOGGER.info("----------------- Start Test ----------------------------");

		LOGGER.info("Test case :: {}", this.getName());
		final SearchRequestBuilder srchReqBld = ESClientTestUtil.INSTANCE
				.getESClient(IElasticSearchUtils.ELASTICSEARCH_INDEX)
				.prepareSearch(IElasticSearchUtils.ELASTICSEARCH_INDEX)
				.setQuery(
						QueryBuilders.filteredQuery(QueryBuilders
								.matchAllQuery(), SimpleSearchFilterUtil
								.prepareBasicSearchFilter(sqb, searchTerm)))
				.setSize(100);

		final List<String> result = ESClientTestUtil.INSTANCE
				.readResult(ElasticSearchUtils.execute(srchReqBld));
		boolean testStatus = false;

		if (expectedResult instanceof String[] && expectedResult.length > 0) {

			final List<String> expected = Arrays
					.asList((String[]) expectedResult);
			LOGGER.debug("Expected result - {}", expected);
			LOGGER.debug("Result - {}", result);
			LOGGER.debug("Result - {}", result.size());
			testStatus = ESClientTestUtil.INSTANCE
					.checkResult(expected, result);

		}

		if (!testStatus) {

			LOGGER.debug("Expected result - {}", resultCount);
			LOGGER.debug("Result - {}", result);
			LOGGER.debug("Result - {}", result.size());
			testStatus = ESClientTestUtil.INSTANCE.checkCount(resultCount,
					result.size());

		}

		if (!testStatus) {
			fail(this.getName());
		} else {
			Assert.assertTrue("::: Test case passed. :::", testStatus);
		}
		LOGGER.info("------------------- End Test ----------------------------");

	}

	@Parameters
	public static Collection<Object[]> getData() throws Exception {
		ESClientTestUtil.INSTANCE
				.getESClient(IElasticSearchUtils.ELASTICSEARCH_INDEX);
		List<List<String>> testCases = ESClientTestUtil.INSTANCE
				.sheetReader("basic");
		List<Object[]> param = new ArrayList<Object[]>();
		Iterator<List<String>> iter = testCases.iterator();
		iter.next();

		List<String> row = null;

		try {

			while (iter.hasNext()) {
				row = iter.next();
				Iterator<String> rowIt = row.iterator();

				final String testType = rowIt.next();
				// rowIt.next();
				final String searchTerm = rowIt.next();

				int count = Integer.parseInt(rowIt.next().replace("#", "")
						.trim());
				String[] result = null;
				if (rowIt.hasNext()) {
					result = StringUtils.split(rowIt.next());
				}
				param.add(new Object[] { testType, searchTerm, result, count });
			}
		} catch (Exception e) {
			LOGGER.trace("Row :: {}", row);
			LOGGER.error("Problem in basic sheet, row number > {}",
					testCases.indexOf(row));
			throw e;
		}
		return param;
	}

	@AfterClass
	public static void end() throws Exception {
		Thread.sleep(300000);
		ESClientTestUtil.INSTANCE.stopServer();
	}
}

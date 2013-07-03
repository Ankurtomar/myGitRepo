/**
 ************************************************************************************************ 
 * Copyright (C) 2012 
 * Pearson Plc. All Rights Reserved 
 * Project : Asset Library 
 * File : ElasticSearchUtils.java
 * Created By : Ankur_Tomar
 * Creation Time : Oct 3, 2012
 * 
 * Description :
 *              
 * Elastic search query implementation for assetlibrary application.
 ************************************************************************************************ 
 */

package org.anks2089.elasticsearch;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.anks2089.bean.PrsnLibImage;
import org.anks2089.bean.ResultItemBean;
import org.anks2089.bean.SearchQueryBean;
import org.anks2089.bean.SearchResultBean;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.HasChildFilterBuilder;
import org.elasticsearch.index.query.OrFilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeFilterBuilder;
import org.elasticsearch.index.query.TermFilterBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;
import org.elasticsearch.search.facet.Facet;
import org.elasticsearch.search.facet.FacetBuilders;
import org.elasticsearch.search.facet.terms.TermsFacet.Entry;
import org.elasticsearch.search.facet.terms.TermsFacetBuilder;
import org.elasticsearch.search.facet.terms.strings.InternalStringTermsFacet;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ankur Tomar
 * 
 */
public class ElasticSearchUtils implements IElasticSearchUtils {

	/**
     * 
     */
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ElasticSearchUtils.class);

	/**
	 * Constructor.
	 */
	public ElasticSearchUtils() {
		super();
	}

	/**
	 * Executes elasticsearch query and returns response object.
	 * 
	 * @param srchReqBld
	 *            SearchRequestBuilder object.
	 * @return SearchResponse search result response object.
	 */
	public static SearchResponse execute(final SearchRequestBuilder srchReqBld) {
		LOGGER.trace("Search Query Json : {} ", srchReqBld.toString());
		return srchReqBld.execute().actionGet();
	}

	/**
	 * Prepares the search query as per searched criteria.
	 * 
	 * @param client
	 *            ES Client. preferred used transport client.
	 * @param objSQB
	 *            SearchQueryBean containing search criteria.
	 * @return SearchRequestBuilder SearchRequestBuilder object.
	 */
	private static SearchRequestBuilder prepareSearchQuery(final Client client,
			final SearchQueryBean objSQB) {
		LOGGER.debug("Repository: {}", ELASTICSEARCH_INDEX);
		LOGGER.trace("ES Transport client: {}", client);
		LOGGER.trace("SearchQueryBean: {}", objSQB);

		SearchRequestBuilder srchReqBld = client
				.prepareSearch(ELASTICSEARCH_INDEX);
		srchReqBld.setSearchType(SearchType.QUERY_THEN_FETCH);
		LOGGER.trace("new searchQuery json : {}", srchReqBld);

		AndFilterBuilder rootAndFilter = getBasicSearchFilterBuilder(objSQB);
		LOGGER.trace("conditional opr : {}", objSQB.getConditionOperator());

		if (null != objSQB.getConditionField()
				&& objSQB.getConditionField().size() > 0) {
			final AdvSearchFilterBuilders advSearchFilter = getAdvSearchFilter(objSQB);

			LOGGER.trace("Advance search has criterias: {}",
					advSearchFilter.hasAdvSearchFilter());
			if ((objSQB.getConditionText().size() > 0)
					&& advSearchFilter.hasAdvSearchFilter()) {
				if (objSQB.getConditionOperator().getValue()
						.equals(SearchOperatorEnum.AND_OPERATION.getValue())) {
					AndFilterBuilder andFilterBuilder = new AndFilterBuilder();

					LOGGER.trace("getHasChildFilterBuilderList size : {}",
							advSearchFilter.getChildFilterList().size());
					for (int i = 0; i < advSearchFilter.getChildFilterList()
							.size(); i++) {
						andFilterBuilder.add(advSearchFilter
								.getChildFilterList().get(i));
					}
					LOGGER.trace("getRangeFilterBuilderList size : {}",
							advSearchFilter.getRangeFilterMap().size());

					final Iterator<Map.Entry<AdvSearchOptionTable, RangeFilterBuilder>> rangeIterator = advSearchFilter
							.getRangeFilterMap().entrySet().iterator();
					while (rangeIterator.hasNext()) {
						final Map.Entry<AdvSearchOptionTable, RangeFilterBuilder> entry = rangeIterator
								.next();
						final AdvSearchOptionTable option = entry.getKey();
						final RangeFilterBuilder filter = entry.getValue();

						if (option.isParent()) {
							andFilterBuilder.add(filter);
						} else {
							andFilterBuilder.add(FilterBuilders.hasChildFilter(
									option.getObjectType(), QueryBuilders
											.filteredQuery(QueryBuilders
													.matchAllQuery(), filter)));
						}

					}

					LOGGER.trace("getTermFilterBuilderList size : {}",
							advSearchFilter.getTermFilterList().size());
					for (int i = 0; i < advSearchFilter.getTermFilterList()
							.size(); i++) {
						andFilterBuilder.add(advSearchFilter
								.getTermFilterList().get(i));
					}
					LOGGER.trace("getFilterBuilderList size : {}",
							advSearchFilter.getFilterList().size());
					for (int i = 0; i < advSearchFilter.getFilterList().size(); i++) {
						andFilterBuilder.add(advSearchFilter.getFilterList()
								.get(i));
					}

					rootAndFilter.add(andFilterBuilder);

				} else {
					OrFilterBuilder orFilterBuilder = new OrFilterBuilder();

					LOGGER.trace("getHasChildFilterBuilderList size : {}",
							advSearchFilter.getChildFilterList().size());
					for (int i = 0; i < advSearchFilter.getChildFilterList()
							.size(); i++) {
						orFilterBuilder.add(advSearchFilter
								.getChildFilterList().get(i));
					}
					LOGGER.trace("getRangeFilterBuilderList size : {}",
							advSearchFilter.getRangeFilterMap().size());
					final Iterator<Map.Entry<AdvSearchOptionTable, RangeFilterBuilder>> rangeIterator = advSearchFilter
							.getRangeFilterMap().entrySet().iterator();
					while (rangeIterator.hasNext()) {
						final Map.Entry<AdvSearchOptionTable, RangeFilterBuilder> entry = rangeIterator
								.next();
						final AdvSearchOptionTable option = entry.getKey();
						final RangeFilterBuilder filter = entry.getValue();
						if (null != option && null != filter) {
							if (option.isParent()) {
								orFilterBuilder.add(filter);
							} else {
								orFilterBuilder
										.add(FilterBuilders.hasChildFilter(
												option.getObjectType(),
												QueryBuilders.filteredQuery(
														QueryBuilders
																.matchAllQuery(),
														filter)));
							}
						}
					}

					LOGGER.trace("getTermFilterBuilderList size : {}",
							advSearchFilter.getTermFilterList().size());
					for (int i = 0; i < advSearchFilter.getTermFilterList()
							.size(); i++) {
						orFilterBuilder.add(advSearchFilter.getTermFilterList()
								.get(i));
					}
					LOGGER.trace("getFilterBuilderList size : {}",
							advSearchFilter.getFilterList().size());
					for (int i = 0; i < advSearchFilter.getFilterList().size(); i++) {
						orFilterBuilder.add(advSearchFilter.getFilterList()
								.get(i));
					}

					rootAndFilter.add(orFilterBuilder);

				}
			}
		}

		// Refine Filter
		final FilterBuilder refinebuilder = appendRefineSearch(objSQB);
		if (null != refinebuilder) {
			rootAndFilter.add(refinebuilder);
		}

		// Exclude Filter
		final FilterBuilder excludebuilder = appendExclusionFilter(objSQB);
		if (null != excludebuilder) {
			rootAndFilter.add(excludebuilder);
		}

		// Filtered Query
		srchReqBld.setQuery(QueryBuilders.filteredQuery(
				QueryBuilders.matchAllQuery(), rootAndFilter));

		return srchReqBld;
	}

	/**
	 * Return the count of search result on searching the assetlibrary ES
	 * indexes as per searched criteria.
	 * 
	 * @param client
	 *            ES Client. preferred used transport client.
	 * @param objSQB
	 *            SearchQueryBean containing search criteria.
	 * @return int search result count.
	 */
	public static int getSearchCount(final Client client,
			final SearchQueryBean objSQB) {

		final SearchRequestBuilder srchReqBld = prepareSearchQuery(client,
				objSQB);
		final SearchResponse response = execute(srchReqBld);

		return (int) response.getHits().totalHits();
	}

	/**
	 * Return the search result on searching the assetlibrary ES indexes as per
	 * searched criteria.
	 * 
	 * @param client
	 *            ES Client. preferred used transport client.
	 * @param objSQB
	 *            SearchQueryBean containing search criteria.
	 * @param fetchFacets
	 *            flag to include facets.
	 * @return SearchResultBean search result custom object.
	 */
	public static SearchResultBean getSearchResults(final Client client,
			final SearchQueryBean objSQB, final boolean fetchFacets) {

		SearchRequestBuilder srchReqBld = prepareSearchQuery(client, objSQB);

		int startindex = Integer.parseInt(objSQB.getStartIndex());
		int endindex = Integer.parseInt(objSQB.getMaxResult());

		if (startindex < 2) {
			startindex = 0;
		}

		LOGGER.trace("StartIndex : {}, Size : {}", startindex, endindex);

		for (String returnAttr : objSQB.getReturnAttr()) {
			srchReqBld.addField(AdvSearchOptionTable.parseCondition(
					objSQB.getBasicKeyword(), returnAttr).getAttributeName());
		}

		// srchReqBld.addField(PrsnLibImage.R_OBJECT_ID).addField(PrsnLibImage.C_ASSET_ID)
		// .addField(PrsnLibImage.C_CAPTION).addField(PrsnLibImage.C_RENDITION_PATH)
		// .addField(PrsnLibImage.ACL_NAME).addField(PrsnLibImage.C_PERMISSIONS_TEMPLATE_ID)
		// .addField(PrsnLibImage.C_RESTRICTIONS_TEXT).addField(PrsnLibImage.C_EXCLUSIVITY_END_DATE);

		srchReqBld.setFrom(startindex).setSize(endindex);

		if (fetchFacets) {
			List<TermsFacetBuilder> termFacetList = getFacetBuilderList(objSQB);
			for (int i = 0; i < termFacetList.size(); i++) {
				srchReqBld.addFacet(termFacetList.get(i));
			}
		}

		FieldSortBuilder fieldSortBld = getSortOrder(objSQB);
		if (null != fieldSortBld) {
			srchReqBld.addSort(fieldSortBld);
			srchReqBld.addSort(new FieldSortBuilder(
					PrsnLibImage.C_RENDITION_PATH).order(SortOrder.ASC));
		}

		SearchResponse response = execute(srchReqBld);
		SearchResultBean srb = new SearchResultBean();

		srb.setResultCount((int) response.getHits().totalHits());

		srb.setLstSearchResult(readSearchResultResponse(response));

		if (fetchFacets) {
			srb.setFacetbean(getFacetsBeanFromResponse(objSQB, response));
		}
		return srb;
	}

	/**
	 * Develops the basic search filter.
	 * 
	 * @param sqb
	 *            SearchQueryBean containing search criteria.
	 * @return AndFilterBuilder Anded array of basic search criteria
	 */
	private static AndFilterBuilder getBasicSearchFilterBuilder(
			final SearchQueryBean sqb) {

		AndFilterBuilder rootAndFilter = FilterBuilders.andFilter();

		String searchText = sqb.getSearchText().trim();

		// Basic search
		FilterBuilder basicFilter = prepareBasicSearch(sqb, searchText);
		if (null != basicFilter) {
			rootAndFilter.add(basicFilter);
		}

		FilterBuilder assetFilter = appendAssetTypeClause(sqb);

		if (assetFilter != null) {
			rootAndFilter.add(assetFilter);
		}

		rootAndFilter.add(appendMandatoryClause(sqb));

		// Facet filter
		BoolFilterBuilder facetFilter = appendFacetClause(sqb);
		if (null != facetFilter) {
			rootAndFilter.add(facetFilter);
		}

		return rootAndFilter;
	}

	/**
	 * Develops the different types of filter as per adv. search criteria.
	 * 
	 * @param sqb
	 *            SearchQueryBean containing search criteria.
	 * @return AdvSearchFilterBuilders object containing all adv. search
	 *         criteria.
	 */
	private static AdvSearchFilterBuilders getAdvSearchFilter(
			final SearchQueryBean sqb) {

		AdvSearchFilterBuilders advSearchFilter = new AdvSearchFilterBuilders();

		Map<AdvSearchOptionTable, RangeFilterBuilder> rangeFilterMap = advSearchFilter
				.getRangeFilterMap();
		List<TermFilterBuilder> termFilterList = advSearchFilter
				.getTermFilterList();
		List<HasChildFilterBuilder> childFilterList = advSearchFilter
				.getChildFilterList();
		List<FilterBuilder> filterList = advSearchFilter.getFilterList();

		// SearchOperatorEnum conditionOperator = sqb.getConditionOperator();
		List<String> conditionField = sqb.getConditionField();
		List<String> conditionType = sqb.getConditionType();
		List<String> conditionText = sqb.getConditionText();
		List<String> str = new ArrayList<String>();

		for (int i = 0; i < conditionField.size(); i++) {
			LOGGER.trace("conditionField.get(i) {}", conditionField.get(i));

			AdvSearchOptionTable option = AdvSearchOptionTable.parseCondition(
					sqb.getBasicKeyword(), conditionField.get(i));
			LOGGER.trace("Object Type: {}", option.getObjectType());
			LOGGER.trace("Input: {} - {}", conditionField.get(i), option);

			str.clear();

			if (conditionType.get(i).equalsIgnoreCase(
					CONDITION_OPR_CONTAINS_ATLEAST)
					|| option.isRepeating()) {
				String assetIdValue = conditionText.get(i);

				if (conditionType.get(i).equalsIgnoreCase(
						CONDITION_OPR_CONTAINS_ATLEAST)) {
					LOGGER.trace("for contains_at_least_one operator,"
							+ " replacing Replacing whitespace and single quote as separator");

					// Replacing whitespace and single quote for
					// contains_at_least_one operator.
					assetIdValue = StringUtils.replace(assetIdValue.trim(),
							WHITESPACE_STRING, COMMA_SEPARATER);
					assetIdValue = StringUtils.replace(assetIdValue, "\\'",
							BLANK_TERM);
				}

				LOGGER.trace("cond text formed after processing - {}",
						assetIdValue);

				String[] assetIds = StringUtils.split(assetIdValue, ',');
				for (String token : assetIds) {
					if (StringUtils.isNotBlank(token.trim())) {
						str.add(StringUtils.lowerCase(token.trim()));
					}
				}
			} else {
				// No escaping required for date data type.
				if (ATTR_DATE_TYPE.equalsIgnoreCase(option.getAttributeType())) {
					str.add(conditionText.get(i).trim());
				} else {
					if (conditionType.get(i).equalsIgnoreCase(CONDITION_OPR_IS)) {
						str.add(StringUtils.lowerCase(conditionText.get(i)
								.trim()));
					} else {
						str.add(StringUtils.lowerCase(QueryParser
								.escape(conditionText.get(i).trim())));

					}
				}
			}

			if (CUSTOM_ATTR_GETTYKEYWORDS.equalsIgnoreCase(option
					.getAttributeName())) {
				FilterBuilder gettyFB = appendAdvSearchforKeywords(sqb, str,
						conditionType.get(i));
				if (null != gettyFB) {
					filterList.add(gettyFB);
				}
				continue;
			}

			if (conditionType.get(i).equalsIgnoreCase(CONDITION_OPR_AFTER)
					&& (ATTR_DATE_TYPE.equalsIgnoreCase(option
							.getAttributeType()))) {

				RangeFilterBuilder rangeFilter = null;

				if (rangeFilterMap.containsKey(option)) {
					rangeFilter = rangeFilterMap.get(option);
					rangeFilter.gt(String.valueOf(getSearchDateLongValue(str
							.get(0))));
					rangeFilter.includeLower(true);
				} else {
					rangeFilter = FilterBuilders.rangeFilter(
							option.getSearchableAttr()).gt(
							String.valueOf(getSearchDateLongValue(str.get(0))));
				}
				rangeFilterMap.put(option, rangeFilter);

			}

			if (conditionType.get(i).equalsIgnoreCase(CONDITION_OPR_BEFORE)
					&& (ATTR_DATE_TYPE.equalsIgnoreCase(option
							.getAttributeType()))) {

				RangeFilterBuilder rangeFilter = null;

				if (rangeFilterMap.containsKey(option)) {
					rangeFilter = rangeFilterMap.get(option);
					rangeFilter.lt(String.valueOf(getSearchDateLongValue(str
							.get(0))));
					rangeFilter.includeUpper(true);
				} else {
					rangeFilter = FilterBuilders.rangeFilter(
							option.getSearchableAttr()).lt(
							String.valueOf(getSearchDateLongValue(str.get(0))));
				}
				rangeFilterMap.put(option, rangeFilter);

			}

			if (conditionType.get(i).equalsIgnoreCase(
					CONDITION_OPR_ONLYCONTAINS)) {
				String term = WILDCARD_STRING + str.get(0) + WILDCARD_STRING;
				WildcardQueryBuilder wcd = new WildcardQueryBuilder(
						option.getSearchableAttr(), term);
				if (option.isParent()) {
					if (option.isRepeating()) {
						LOGGER.trace("Parent repeating ");

						// updated for DE1846.
						if (PrsnLibImage.C_ASSET_ID.equalsIgnoreCase(option
								.getAttributeName())) {
							OrFilterBuilder orFilterBuilder = FilterBuilders
									.orFilter();
							for (String tkns : str.toArray(new String[str
									.size()])) {
								orFilterBuilder
										.add(FilterBuilders.nestedFilter(
												option.getAttributeName(),
												new WildcardQueryBuilder(
														option.getSearchableAttr(),
														WILDCARD_STRING
																+ tkns
																+ WILDCARD_STRING)));
							}
							filterList.add(orFilterBuilder);
						} else {
							AndFilterBuilder andFilterBuilder = FilterBuilders
									.andFilter();
							for (String tkns : str.toArray(new String[str
									.size()])) {
								andFilterBuilder
										.add(FilterBuilders.nestedFilter(
												option.getAttributeName(),
												new WildcardQueryBuilder(
														option.getSearchableAttr(),
														WILDCARD_STRING
																+ tkns
																+ WILDCARD_STRING)));
							}
							filterList.add(andFilterBuilder);
						}
					} else {
						LOGGER.trace("Parent non repeating ");
						filterList.add(FilterBuilders.queryFilter(QueryBuilders
								.boolQuery().must(wcd)));
					}
				} else {
					LOGGER.trace("child repeating modified");
					childFilterList.add(FilterBuilders.hasChildFilter(option
							.getObjectType(),
							QueryBuilders.boolQuery().should(wcd)));
				}

			}
			if (conditionType.get(i).equalsIgnoreCase(CONDITION_OPR_IS)) {

				LOGGER.trace("{}", option.toString());

				/* To handle templates grouping */
				if (option.getAttributeName().equalsIgnoreCase(
						PrsnLibImage.C_PERMISSIONS_TEMPLATE_ID)
						|| option.getAttributeName().equalsIgnoreCase(
								CUSTOM_ATTR_RIGHTS)
						|| option.getAttributeName().equalsIgnoreCase(
								CUSTOM_ATTR_INTERNAL_CHARGE)
						|| option.getAttributeName().equalsIgnoreCase(
								CUSTOM_ATTR_EXTERNAL_CHARGE)
						|| option.getAttributeName().equalsIgnoreCase(
								CUSTOM_ATTR_HIRES)
						|| option.getAttributeName().equalsIgnoreCase(
								CUSTOM_ATTR_RIGHTS_EXCLUDE)
						|| option.getAttributeName().equalsIgnoreCase(
								CUSTOM_ATTR_INTERNAL_CHARGE_EXCLUDE)
						|| option.getAttributeName().equalsIgnoreCase(
								CUSTOM_ATTR_EXTERNAL_CHARGE_EXCLUDE)
						|| option.getAttributeName().equalsIgnoreCase(
								CUSTOM_ATTR_HIRES_EXCLUDE)) {

					OrFilterBuilder orFilterBuilder = null;

					String[] tkns = StringUtils.split(str.get(0),
							COMMA_SEPARATER);
					if (tkns.length > 0) {
						orFilterBuilder = FilterBuilders.orFilter();
						for (String permTemp : tkns) {
							orFilterBuilder.add(FilterBuilders.termFilter(
									PrsnLibImage.C_PERMISSIONS_TEMPLATE_ID,
									permTemp));
						}
					}

					if (option.getAttributeName().equalsIgnoreCase(
							CUSTOM_ATTR_RIGHTS_EXCLUDE)
							|| option.getAttributeName().equalsIgnoreCase(
									CUSTOM_ATTR_INTERNAL_CHARGE_EXCLUDE)
							|| option.getAttributeName().equalsIgnoreCase(
									CUSTOM_ATTR_EXTERNAL_CHARGE_EXCLUDE)) {

						if (null != orFilterBuilder) {
							filterList.add(FilterBuilders
									.notFilter(orFilterBuilder));
						}
					} else if (option.getAttributeName().equalsIgnoreCase(
							CUSTOM_ATTR_HIRES_EXCLUDE)
							|| option.getAttributeName().equalsIgnoreCase(
									CUSTOM_ATTR_HIRES)) {

						if ((StringUtils.equalsIgnoreCase(sqb.getUserRole(),
								USERROLE_SA) || StringUtils.equalsIgnoreCase(
								sqb.getUserRole(), USERROLE_IAM))) {

							// Not Hi-res filtering required for IAM/SA users.

							if (option.getAttributeName().equalsIgnoreCase(
									CUSTOM_ATTR_HIRES_EXCLUDE)) {
								filterList.add(FilterBuilders
										.notFilter(FilterBuilders
												.queryFilter(QueryBuilders
														.matchAllQuery())));
							}

						} else {
							if (option.getAttributeName().equalsIgnoreCase(
									CUSTOM_ATTR_HIRES)) {

								if (null == orFilterBuilder) {
									filterList
											.add(FilterBuilders
													.orFilter(
															FilterBuilders
																	.missingFilter(
																			PrsnLibImage.C_EXCLUSIVITY_END_DATE)
																	.nullValue(
																			true))
													.add(FilterBuilders
															.rangeFilter(
																	PrsnLibImage.C_EXCLUSIVITY_END_DATE)
															.lt(System
																	.currentTimeMillis())));
								} else {
									filterList
											.add(FilterBuilders
													.andFilter(orFilterBuilder)
													.add(FilterBuilders
															.orFilter(
																	FilterBuilders
																			.missingFilter(
																					PrsnLibImage.C_EXCLUSIVITY_END_DATE)
																			.nullValue(
																					true))
															.add(FilterBuilders
																	.rangeFilter(
																			PrsnLibImage.C_EXCLUSIVITY_END_DATE)
																	.lt(System
																			.currentTimeMillis()))));
								}
							} else {
								if (null == orFilterBuilder) {
									filterList
											.add(FilterBuilders
													.notFilter(FilterBuilders
															.orFilter(
																	FilterBuilders
																			.missingFilter(
																					PrsnLibImage.C_EXCLUSIVITY_END_DATE)
																			.nullValue(
																					true))
															.add(FilterBuilders
																	.rangeFilter(
																			PrsnLibImage.C_EXCLUSIVITY_END_DATE)
																	.lt(System
																			.currentTimeMillis()))));
								} else {
									filterList
											.add(FilterBuilders
													.notFilter(FilterBuilders
															.andFilter(
																	orFilterBuilder)
															.add(FilterBuilders
																	.orFilter(
																			FilterBuilders
																					.missingFilter(
																							PrsnLibImage.C_EXCLUSIVITY_END_DATE)
																					.nullValue(
																							true))
																	.add(FilterBuilders
																			.rangeFilter(
																					PrsnLibImage.C_EXCLUSIVITY_END_DATE)
																			.lt(System
																					.currentTimeMillis())))));
								}
							}
						}
					} else {
						if (null == orFilterBuilder) {
							filterList.add(FilterBuilders
									.notFilter(FilterBuilders
											.queryFilter(QueryBuilders
													.matchAllQuery())));
						} else {
							filterList.add(orFilterBuilder);
						}
					}

				} else {

					if (option.isParent()) {
						if (option.isRepeating()) {

							if (PrsnLibImage.C_ASSET_ID.equalsIgnoreCase(option
									.getAttributeName())) {
								OrFilterBuilder orFilterBuilder = FilterBuilders
										.orFilter();
								for (String tkns : str.toArray(new String[str
										.size()])) {
									orFilterBuilder
											.add(FilterBuilders.nestedFilter(
													option.getAttributeName(),
													QueryBuilders.termQuery(
															option.getSearchableAttr(),
															tkns.trim())));
								}
								filterList.add(orFilterBuilder);
							} else {
								AndFilterBuilder andFilterBuilder = FilterBuilders
										.andFilter();
								for (String tkns : str.toArray(new String[str
										.size()])) {
									andFilterBuilder
											.add(FilterBuilders.nestedFilter(
													option.getAttributeName(),
													QueryBuilders.termQuery(
															option.getSearchableAttr(),
															tkns.trim())));
								}
								filterList.add(andFilterBuilder);
							}
						} else {
							filterList.add(FilterBuilders
									.queryFilter(QueryBuilders.termQuery(
											option.getSearchableAttr(),
											str.get(0))));
						}

					} else {
						childFilterList.add(FilterBuilders.hasChildFilter(
								option.getObjectType(),
								QueryBuilders.boolQuery().should(
										QueryBuilders.termQuery(
												option.getSearchableAttr(),
												str.get(0)))));
					}

				}

			}
			if (conditionType.get(i).equalsIgnoreCase(
					CONDITION_OPR_DOES_NOT_CONTAIN)) {
				String term = WILDCARD_STRING + str.get(0) + WILDCARD_STRING;
				WildcardQueryBuilder wcd = new WildcardQueryBuilder(
						option.getSearchableAttr(), term);
				if (option.isParent()) {
					if (option.isRepeating()) {
						if (PrsnLibImage.C_ASSET_ID.equalsIgnoreCase(option
								.getAttributeName())) {
							OrFilterBuilder orFilterBuilder = FilterBuilders
									.orFilter();
							for (String tkns : str.toArray(new String[str
									.size()])) {
								orFilterBuilder
										.add(FilterBuilders.nestedFilter(
												option.getAttributeName(),
												new WildcardQueryBuilder(
														option.getSearchableAttr(),
														WILDCARD_STRING
																+ tkns
																+ WILDCARD_STRING)));
							}
							filterList.add(FilterBuilders
									.notFilter(orFilterBuilder));
						} else {
							AndFilterBuilder andFilterBuilder = FilterBuilders
									.andFilter();
							for (String tkns : str.toArray(new String[str
									.size()])) {
								andFilterBuilder
										.add(FilterBuilders.nestedFilter(
												option.getAttributeName(),
												new WildcardQueryBuilder(
														option.getSearchableAttr(),
														WILDCARD_STRING
																+ tkns
																+ WILDCARD_STRING)));
							}
							filterList.add(FilterBuilders
									.notFilter(andFilterBuilder));
						}
					} else {
						filterList.add(FilterBuilders.queryFilter(QueryBuilders
								.boolQuery().mustNot(wcd)));
					}

				} else {
					childFilterList.add(FilterBuilders.hasChildFilter(option
							.getObjectType(), QueryBuilders.boolQuery()
							.mustNot(wcd)));
				}

			}
			if (conditionType.get(i).equalsIgnoreCase(CONDITION_OPR_HAS_VALUE)) {

				if (option.isParent()) {

					if (option.isRepeating()) {
						filterList.add(FilterBuilders.nestedFilter(option
								.getAttributeName(), FilterBuilders
								.existsFilter(option.getSearchableAttr())));

						// FilterBuilders.andFilter(existsFilter(option.getSearchableAttr())).add(
						// FilterBuilders.notFilter(FilterBuilders.termFilter(
						// option.getSearchableAttr(), BLANK_TERM)))));

					} else {
						filterList.add(FilterBuilders.existsFilter(option
								.getSearchableAttr()));
						// FilterBuilders.andFilter(existsFilter(option.getSearchableAttr()))
						// .add(FilterBuilders.notFilter(FilterBuilders.termFilter(
						// option.getSearchableAttr(), BLANK_TERM))));
					}

				} else {
					childFilterList
							.add(FilterBuilders.hasChildFilter(option
									.getObjectType(), QueryBuilders
									.filteredQuery(QueryBuilders
											.matchAllQuery(), FilterBuilders
											.existsFilter(option
													.getSearchableAttr()))));

					// FilterBuilders.andFilter(existsFilter(option.getSearchableAttr())).add(
					// FilterBuilders.notFilter(FilterBuilders.termFilter(
					// option.getSearchableAttr(), BLANK_TERM))))));
				}

			}

			if (conditionType.get(i).equalsIgnoreCase(
					CONDITION_OPR_HAS_NO_VALUE)) {

				if (option.isParent()) {

					if (option.isRepeating()) {
						filterList.add(FilterBuilders.notFilter(FilterBuilders
								.nestedFilter(option.getAttributeName(),
										FilterBuilders.existsFilter(option
												.getSearchableAttr()))));

						// FilterBuilders.andFilter(existsFilter(option.getSearchableAttr())).add(
						// FilterBuilders.notFilter(FilterBuilders.termFilter(
						// option.getSearchableAttr(), BLANK_TERM))))));
					} else {
						filterList.add(FilterBuilders.missingFilter(
								option.getSearchableAttr()).nullValue(true));

						// FilterBuilders.notFilter(FilterBuilders.andFilter(
						// existsFilter(option.getSearchableAttr())).add(
						// FilterBuilders.notFilter(FilterBuilders.termFilter(option.getSearchableAttr(),
						// BLANK_TERM)))));
					}

				} else {
					filterList.add(FilterBuilders.hasChildFilter(option
							.getObjectType(), QueryBuilders.filteredQuery(
							QueryBuilders.matchAllQuery(), FilterBuilders
									.missingFilter(option.getSearchableAttr())
									.nullValue(true))));

					// FilterBuilders.orFilter(
					// FilterBuilders.missingFilter(option.getSearchableAttr())).add(
					// FilterBuilders.termFilter(option.getSearchableAttr(),
					// BLANK_TERM)))));
				}

			}

			if (conditionType.get(i).equalsIgnoreCase(
					CONDITION_OPR_CONTAINS_ATLEAST)) {

				OrFilterBuilder orFilterBuilder = FilterBuilders.orFilter();
				if (option.isParent()) {
					for (String tkns : str.toArray(new String[1])) {

						WildcardQueryBuilder wcd = new WildcardQueryBuilder(
								option.getSearchableAttr(), WILDCARD_STRING
										+ tkns + WILDCARD_STRING);
						if (option.isRepeating()) {
							orFilterBuilder.add(FilterBuilders.nestedFilter(
									option.getAttributeName(), QueryBuilders
											.boolQuery().must(wcd)));
						} else {
							orFilterBuilder.add(FilterBuilders
									.queryFilter(QueryBuilders.boolQuery()
											.must(wcd)));
						}

					}
				} else {

					for (String tkns : str.toArray(new String[1])) {
						WildcardQueryBuilder wcd = new WildcardQueryBuilder(
								option.getSearchableAttr(), WILDCARD_STRING
										+ tkns + WILDCARD_STRING);
						orFilterBuilder.add(FilterBuilders.hasChildFilter(
								option.getObjectType(), QueryBuilders
										.boolQuery().should(wcd)));
					}
				}
				filterList.add(orFilterBuilder);
			}

		}

		advSearchFilter.setChildFilterList(childFilterList);
		advSearchFilter.setRangeFilterMap(rangeFilterMap);
		advSearchFilter.setTermFilterList(termFilterList);
		advSearchFilter.setFilterList(filterList);

		return advSearchFilter;
	}

	/**
	 * Reads the response object to populate search result information.
	 * 
	 * @param response
	 *            SearchResponse search result response object.
	 * @return list of ResultItem beans.
	 */
	private static List<ResultItemBean> readSearchResultResponse(
			final SearchResponse response) {
		List<ResultItemBean> searchResultList = new ArrayList<ResultItemBean>();
		LOGGER.trace("hitCount - {}", response.getHits().getTotalHits());
		for (final SearchHit hit : response.getHits()) {

			ResultItemBean titem = new ResultItemBean();

			final Iterator<SearchHitField> iterator = hit.iterator();
			titem.setAssetId(hit.getId());
			while (iterator.hasNext()) {
				final SearchHitField hitField = iterator.next();

				for (final Object value : hitField.getValues()) {
					if (hitField.getName()
							.equals(PrsnLibImage.C_RENDITION_PATH)) {
						titem.setCRenditionPath(String.valueOf(value));
					} else if (hitField.getName().equals(
							PrsnLibImage.C_ASSET_ID)) {
						titem.setAssetName(String.valueOf(value));
					} else if (hitField.getName().equals(
							PrsnLibImage.C_RESTRICTIONS_TEXT)) {
						titem.setCRestrictionsText(StringEscapeUtils
								.escapeHtml(String.valueOf(value)));
					} else if (hitField.getName().equals(
							PrsnLibImage.C_PERMISSIONS_TEMPLATE_ID)) {
						titem.setCPermTempName(String.valueOf(value));
					} else if (hitField.getName().equals(
							PrsnLibImage.R_OBJECT_ID)) {
						titem.setAssetId(String.valueOf(value));
					} else if (hitField.getName()
							.equals(PrsnLibImage.C_CAPTION)) {
						titem.setCCaption(StringEscapeUtils.escapeHtml(String
								.valueOf(value)));
					} else if (hitField.getName().equals(PrsnLibImage.ACL_NAME)) {
						titem.setAclName(String.valueOf(value));
					} else if (hitField.getName().equals(
							PrsnLibImage.C_ADMIN_BUSINESS)) {
						titem.setAdminBusiness(String.valueOf(value));
					} else if (hitField.getName().equals(
							PrsnLibImage.C_EXCLUSIVITY_END_DATE)) {
						titem.setCExclusiveEndDate(getSearchDateLongValue(String
								.valueOf(value)));
						LOGGER.trace("Exclusivity end date: {}", value);
					}
				}
			}
			searchResultList.add(titem);
		}
		return searchResultList;
	}

	/**
	 * Reads the response object to populate facet information.
	 * 
	 * @param sqb
	 *            SearchQueryBean containing search criteria.
	 * @param response
	 *            SearchResponse search result response object.
	 * @return FacetBean bean containing facets results.
	 */
	private static Map<String, Map<String, String>> getFacetsBeanFromResponse(
			final SearchQueryBean sqb, final SearchResponse response) {
		Map<String, Facet> fctMap = response.getFacets().facetsAsMap();

		LOGGER.trace(fctMap.toString());
		// FacetBean objFB = new FacetBean();
		Map<String, Map<String, String>> facetResult = new TreeMap<String, Map<String, String>>();

		for (Map.Entry<String, Facet> entry : fctMap.entrySet()) {
			HashMap<String, String> objHMP = new HashMap<String, String>();
			String facetName = entry.getValue().getName();
			InternalStringTermsFacet fctIter = (InternalStringTermsFacet) fctMap
					.get(facetName);

			if (facetName
					.equalsIgnoreCase(PrsnLibImage.C_RESTRICTIONS_TEXT_FACET)) {
				objHMP.put(BLANK_TERM,
						String.valueOf(fctIter.getMissingCount()));
			} else {
				Iterator<Entry> iter = fctIter.iterator();
				while (iter.hasNext()) {
					Entry ent = iter.next();
					if (StringUtils.isNotBlank(ent.getTerm().string())) {
						objHMP.put(
								StringUtils.capitalize(ent.getTerm().string()),
								String.valueOf(ent.getCount()));
						LOGGER.trace("term {}, value {}", ent.getTerm(),
								ent.getCount());
					}
				}
			}

			TreeMap<String, String> result = new TreeMap<String, String>(
					String.CASE_INSENSITIVE_ORDER);
			result.putAll(objHMP);

			LOGGER.trace("Facet name: {} isEmpty : {}", facetName,
					result.isEmpty());

			if (!result.isEmpty()) {

				for (org.anks2089.bean.Facet facet : sqb.getFacetList()) {
					if (facet.getKey().contains(facetName)) {
						facetResult.put(facet.getName(), result);
					}
				}
			}
		}

		return facetResult;
	}

	/**
	 * Develops the facetbuilder to generate facets.
	 * 
	 * @param objSQB
	 *            SearchQueryBean containing search criteria.
	 * @return list of TermsFacetBuilder to build facets.
	 */
	private static List<TermsFacetBuilder> getFacetBuilderList(
			final SearchQueryBean objSQB) {
		List<TermsFacetBuilder> facetBuilderList = new ArrayList<TermsFacetBuilder>();

		for (org.anks2089.bean.Facet facet : objSQB.getFacetList()) {

			AdvSearchOptionTable option = AdvSearchOptionTable.parseCondition(
					objSQB.getBasicKeyword(), facet.getKey());
			TermsFacetBuilder facetFilter = null;

			if (option.isParent()) {

				if (StringUtils.equalsIgnoreCase("find_missing",
						facet.getType())) {
					facetFilter = FacetBuilders
							.termsFacet(option.getAttributeName())
							.field(option.getAttributeName())
							.facetFilter(
									FilterBuilders.missingFilter(
											option.getAttributeName())
											.nullValue(true));
				} else {
					facetFilter = FacetBuilders
							.termsFacet(option.getAttributeName())
							.field(option.getAttributeName())
							.size(Integer.parseInt(facet.getSize()));

				}
			} else {
				facetFilter = FacetBuilders
						.termsFacet(option.getAttributeName())
						.field(PrsnLibImage.C_ASSET_TYPE_FACET)
						.size(Integer.parseInt(facet.getSize()))
						.facetFilter(
								FilterBuilders.hasChildFilter(
										option.getObjectType(),
										QueryBuilders.matchAllQuery()));
			}

			if (null != facetFilter) {
				facetBuilderList.add(facetFilter);
			}
		}
		return facetBuilderList;
	}

	/**
	 * Appends Asset type(e.g PhotoGraph/Artwork) filter.
	 * 
	 * @param objSQB
	 *            SearchQueryBean containing search criteria.
	 * @return Asset type filter.
	 */
	private static FilterBuilder appendAssetTypeClause(
			final SearchQueryBean objSQB) {

		FilterBuilder srchReqFilterBld = null;
		if (StringUtils.equalsIgnoreCase(objSQB.getArtworkCheckbox(),
				String.valueOf(true))
				&& StringUtils.equalsIgnoreCase(objSQB.getPhotoCheckbox(),
						String.valueOf(true))) {
			srchReqFilterBld = FilterBuilders.orFilter(FilterBuilders
					.termFilter(PrsnLibImage.C_ASSET_TYPE, ASSET_TYPE_ARTWORK),
					FilterBuilders.termFilter(PrsnLibImage.C_ASSET_TYPE,
							ASSET_TYPE_PHOTOGRAPH));
		} else if (StringUtils.equalsIgnoreCase(objSQB.getArtworkCheckbox(),
				String.valueOf(true))
				&& StringUtils.equalsIgnoreCase(objSQB.getPhotoCheckbox(),
						String.valueOf(false))) {
			srchReqFilterBld = FilterBuilders.termFilter(
					PrsnLibImage.C_ASSET_TYPE, ASSET_TYPE_ARTWORK);
		} else if (StringUtils.equalsIgnoreCase(objSQB.getPhotoCheckbox(),
				String.valueOf(true))
				&& StringUtils.equalsIgnoreCase(objSQB.getArtworkCheckbox(),
						String.valueOf(false))) {
			srchReqFilterBld = FilterBuilders.termFilter(
					PrsnLibImage.C_ASSET_TYPE, ASSET_TYPE_PHOTOGRAPH);
		}
		return srchReqFilterBld;
	}

	/**
	 * Appends Asset Lifecyle state clause in Query to avoid deleted assets in
	 * search result.
	 * 
	 * @param objSQB
	 *            SearchQueryBean containing search criteria.
	 * @return mandatory filters as per user role..
	 */
	private static BoolFilterBuilder appendMandatoryClause(
			final SearchQueryBean objSQB) {

		BoolFilterBuilder boolFilter = FilterBuilders.boolFilter().mustNot(
				FilterBuilders.termFilter(
						PrsnLibImage.C_ASSET_LIFECYCLE_STATUS, "deleted"));

		if (!(StringUtils.equalsIgnoreCase(objSQB.getUserRole(), USERROLE_SA) || StringUtils
				.equalsIgnoreCase(objSQB.getUserRole(), USERROLE_IAM))) {
			boolFilter.must(FilterBuilders.termFilter(
					PrsnLibImage.C_ASSET_LIFECYCLE_STATUS, "active"));
		}
		return boolFilter;
	}

	/**
	 * Preapared Query for Keywords (5 attributes).
	 * 
	 * @param sqb
	 *            containing search criteria.
	 * @param searchparamtxt
	 *            list of search terms.
	 * @param conditionType
	 *            type of search condition.
	 * @return filter.
	 */
	private static FilterBuilder appendAdvSearchforKeywords(
			final SearchQueryBean sqb, final List<String> searchparamtxt,
			final String conditionType) {

		FilterBuilder qfb = null;
		// String[] gettyKeyword = sqb.getGettyKeyword().toArray(new
		// String[sqb.getGettyKeyword().size()]);

		if (CONDITION_OPR_ONLYCONTAINS.equalsIgnoreCase(conditionType)
				&& !searchparamtxt.isEmpty()) {

			AndFilterBuilder andFilterBuilder = FilterBuilders.andFilter();
			for (String atleastOneterm : searchparamtxt.toArray(new String[1])) {
				andFilterBuilder.add(SimpleSearchFilterUtil
						.prepareKeywordFilter(
								WILDCARD_STRING + atleastOneterm.trim()
										+ WILDCARD_STRING,
								sqb.getGettyKeyword(), 2));

			}
			qfb = andFilterBuilder;

		}

		if (conditionType.equalsIgnoreCase(CONDITION_OPR_BEGINS_WITH)
				&& !searchparamtxt.isEmpty()) {

			AndFilterBuilder andFilterBuilder = FilterBuilders.andFilter();
			for (String atleastOneterm : searchparamtxt.toArray(new String[1])) {
				andFilterBuilder.add(SimpleSearchFilterUtil
						.prepareKeywordFilter(atleastOneterm.trim()
								+ WILDCARD_STRING, sqb.getGettyKeyword(), 2));

			}
			qfb = andFilterBuilder;

		}
		if (conditionType.equalsIgnoreCase(CONDITION_OPR_ENDS_WITH)
				&& !searchparamtxt.isEmpty()) {

			AndFilterBuilder andFilterBuilder = FilterBuilders.andFilter();
			for (String atleastOneterm : searchparamtxt.toArray(new String[1])) {

				andFilterBuilder.add(SimpleSearchFilterUtil
						.prepareKeywordFilter(
								WILDCARD_STRING + atleastOneterm.trim(),
								sqb.getGettyKeyword(), 2));

			}
			qfb = andFilterBuilder;

		}
		if (conditionType.equalsIgnoreCase(CONDITION_OPR_IS)
				&& !searchparamtxt.isEmpty()) {

			AndFilterBuilder andFilterBuilder = FilterBuilders.andFilter();
			for (String atleastOneterm : searchparamtxt.toArray(new String[1])) {
				andFilterBuilder.add(SimpleSearchFilterUtil
						.prepareKeywordFilter(atleastOneterm.trim(),
								sqb.getGettyKeyword(), 1));

			}
			qfb = andFilterBuilder;

		}

		if (conditionType.equalsIgnoreCase(CONDITION_OPR_DOES_NOT_CONTAIN)
				&& !searchparamtxt.isEmpty()) {

			AndFilterBuilder andFilterBuilder = FilterBuilders.andFilter();
			for (String atleastOneterm : searchparamtxt.toArray(new String[1])) {

				andFilterBuilder.add(SimpleSearchFilterUtil
						.prepareKeywordFilter(
								WILDCARD_STRING + atleastOneterm.trim()
										+ WILDCARD_STRING,
								sqb.getGettyKeyword(), 2));

			}
			qfb = FilterBuilders.notFilter(andFilterBuilder);

		}
		if (conditionType.equalsIgnoreCase(CONDITION_OPR_HAS_VALUE)) {

			qfb = SimpleSearchFilterUtil.prepareKeywordFilter(null,
					sqb.getGettyKeyword(), 3);

		}

		if (conditionType.equalsIgnoreCase(CONDITION_OPR_HAS_NO_VALUE)) {

			qfb = FilterBuilders.notFilter(SimpleSearchFilterUtil
					.prepareKeywordFilter(null, sqb.getGettyKeyword(), 3));
		}
		if (conditionType.equalsIgnoreCase(CONDITION_OPR_CONTAINS_ATLEAST)
				&& !searchparamtxt.isEmpty()) {

			OrFilterBuilder orFilterBuilder = FilterBuilders.orFilter();
			for (String atleastOneterm : searchparamtxt.toArray(new String[1])) {
				orFilterBuilder.add(SimpleSearchFilterUtil
						.prepareKeywordFilter(
								WILDCARD_STRING + atleastOneterm.trim()
										+ WILDCARD_STRING,
								sqb.getGettyKeyword(), 2));
			}
			qfb = orFilterBuilder;

		}

		LOGGER.debug("Getty keywords filter : {}", qfb);
		return qfb;
	}

	/**
	 * Develops the AND-ed boolean filter for facet criteria.
	 * 
	 * @param objSQB
	 *            SearchQueryBean containing search criteria.
	 * @return BoolFilterBuilder to append facet refine filters.
	 */
	private static BoolFilterBuilder appendFacetClause(
			final SearchQueryBean objSQB) {

		BoolFilterBuilder srchReqFilterBld = null;
		String strFacets = objSQB.getFacetsTerms();

		if (StringUtils.isNotBlank(strFacets)) {

			LOGGER.debug("Facets condition string: {}", strFacets);
			srchReqFilterBld = FilterBuilders.boolFilter();

			/*
			 * Restriction for EAM && SU user.
			 */
			if (!(objSQB.getUserRole().equalsIgnoreCase(USERROLE_IAM) || objSQB
					.getUserRole().equalsIgnoreCase(USERROLE_SA))) {
				srchReqFilterBld.mustNot(FilterBuilders.termFilter(
						PrsnLibImage.C_PERMISSIONS_TEMPLATE_ID,
						DEFAULT_PERMISSION_TEMPLATE_ID));
			}

			LOGGER.trace("Inside appendFacetSearchClause(), Facet Search={}",
					strFacets);
			String[] arrFacet = StringUtils.split(strFacets, COMMA_SEPARATER);
			for (int i = 0; i < arrFacet.length; i++) {
				String strFacet = arrFacet[i];
				String[] arrFacetValue = strFacet.split(":");

				if (arrFacetValue.length != 2) {
					continue;
				}

				String strFacetName = arrFacetValue[0].trim();

				if (arrFacetValue[1] == null) {
					continue; /*
							 * This is a case where there is no template
							 * associated with Rights
							 */
				}

				String strFacetValue = arrFacetValue[1].trim();

				LOGGER.debug("strFacetName ={} strFacetValue={}", strFacetName,
						strFacetValue);

				// ////////////////////////////////////////////////////////////////////////////////////////////////

				if (strFacetName.equalsIgnoreCase(FACET_RIGHTF)
						|| strFacetName.equalsIgnoreCase(FACET_HI_RES_PERM)
						|| strFacetName.equalsIgnoreCase(FACET_INTERNALCHRG)
						|| strFacetName.equalsIgnoreCase(FACET_EXTERNALCHRG)
						|| strFacetName.equalsIgnoreCase(FACET_RIGHTS_BLANK)
						|| strFacetName
								.equalsIgnoreCase(FACET_RIGHTS_WITH_BLANK)) {

					OrFilterBuilder orFilterBuilder = FilterBuilders.orFilter();

					if ((objSQB.getUserRole().equalsIgnoreCase(USERROLE_IAM) || objSQB
							.getUserRole().equalsIgnoreCase(USERROLE_SA))
							&& strFacetName
									.equalsIgnoreCase(FACET_RIGHTS_BLANK)) {

						strFacetValue = DEFAULT_PERMISSION_TEMPLATE_ID;

					} else if ((objSQB.getUserRole().equalsIgnoreCase(
							USERROLE_IAM) || objSQB.getUserRole()
							.equalsIgnoreCase(USERROLE_SA))
							&& strFacetName
									.equalsIgnoreCase(FACET_RIGHTS_WITH_BLANK)) {

						orFilterBuilder.add(FilterBuilders.termFilter(
								PrsnLibImage.C_PERMISSIONS_TEMPLATE_ID,
								DEFAULT_PERMISSION_TEMPLATE_ID));
					}
					if ((objSQB.getUserRole().equalsIgnoreCase(USERROLE_IAM) || objSQB
							.getUserRole().equalsIgnoreCase(USERROLE_SA))
							&& strFacetName.equalsIgnoreCase(FACET_HI_RES_PERM)) {
						srchReqFilterBld.must(FilterBuilders
								.queryFilter(QueryBuilders.matchAllQuery()));
					} else {
						if (StringUtils.isNotBlank(strFacetValue)) {
							StringTokenizer tkns = new StringTokenizer(
									strFacetValue, PIPE_SEPARATOR);

							while (tkns.hasMoreElements()) {

								String permTemp = tkns.nextToken();
								orFilterBuilder.add(FilterBuilders.termFilter(
										PrsnLibImage.C_PERMISSIONS_TEMPLATE_ID,
										permTemp.trim()));
							}

							if (strFacetName
									.equalsIgnoreCase(FACET_HI_RES_PERM)) {
								srchReqFilterBld
										.must(FilterBuilders
												.andFilter(orFilterBuilder)
												.add(FilterBuilders
														.orFilter(
																FilterBuilders
																		.missingFilter(
																				PrsnLibImage.C_EXCLUSIVITY_END_DATE)
																		.nullValue(
																				true))
														.add(FilterBuilders
																.rangeFilter(
																		PrsnLibImage.C_EXCLUSIVITY_END_DATE)
																.lt(System
																		.currentTimeMillis()))));

							} else {
								srchReqFilterBld.must(orFilterBuilder);
							}
						}
					}

				} else if (strFacetName.equalsIgnoreCase(FACET_RIGHTF_EXCLUDE)
						|| strFacetName
								.equalsIgnoreCase(FACET_HI_RES_PERM_EXCLUDE)
						|| strFacetName
								.equalsIgnoreCase(FACET_INTERNALCHRG_EXCLUDE)
						|| strFacetName
								.equalsIgnoreCase(FACET_EXTERNALCHRG_EXCLUDE)) {

					OrFilterBuilder orFilterBuilder = FilterBuilders.orFilter();
					StringTokenizer tkns = new StringTokenizer(strFacetValue,
							PIPE_SEPARATOR);
					while (tkns.hasMoreElements()) {

						String permTemp = tkns.nextToken();
						orFilterBuilder.add(FilterBuilders.termFilter(
								PrsnLibImage.C_PERMISSIONS_TEMPLATE_ID,
								permTemp.trim()));
					}

					if (strFacetName
							.equalsIgnoreCase(FACET_HI_RES_PERM_EXCLUDE)) {

						srchReqFilterBld
								.mustNot(FilterBuilders
										.andFilter(orFilterBuilder)
										.add(FilterBuilders
												.orFilter(
														FilterBuilders
																.missingFilter(
																		PrsnLibImage.C_EXCLUSIVITY_END_DATE)
																.nullValue(true))
												.add(FilterBuilders
														.rangeFilter(
																PrsnLibImage.C_EXCLUSIVITY_END_DATE)
														.lt(System
																.currentTimeMillis()))));
					} else {
						if (strFacetName.equalsIgnoreCase(FACET_RIGHTF_EXCLUDE)) {
							orFilterBuilder.add(FilterBuilders.termFilter(
									PrsnLibImage.C_PERMISSIONS_TEMPLATE_ID,
									DEFAULT_PERMISSION_TEMPLATE_ID));
						}

						srchReqFilterBld.mustNot(orFilterBuilder);
					}

				} else if (strFacetName.equalsIgnoreCase(FACET_LOW_RES)) {

					srchReqFilterBld.must(FilterBuilders.rangeFilter(
							PrsnLibImage.C_EXCLUSIVITY_END_DATE).lte(
							System.currentTimeMillis()));

				} else {

					org.anks2089.bean.Facet facet = null;

					if (null != objSQB.getFacetList()) {
						for (org.anks2089.bean.Facet tempFacet : objSQB
								.getFacetList()) {
							if (strFacetName.equalsIgnoreCase(tempFacet
									.getName())) {
								facet = tempFacet;
								break;
							}
						}
					}

					if (null == facet) {
						continue;
					}

					if ("find_missing".equalsIgnoreCase(facet.getType())) {
						if (String.valueOf(true)
								.equalsIgnoreCase(strFacetValue)) {
							srchReqFilterBld.must(FilterBuilders
									.existsFilter(AdvSearchOptionTable
											.parseCondition(
													objSQB.getBasicKeyword(),
													facet.getKey())
											.getAttributeName()));

						} else if (String.valueOf(false).equalsIgnoreCase(
								strFacetValue)) {
							srchReqFilterBld.must(FilterBuilders.missingFilter(
									AdvSearchOptionTable.parseCondition(
											objSQB.getBasicKeyword(),
											facet.getKey()).getAttributeName())
									.nullValue(true));

						}
					} else if ("facet_with_blank".equalsIgnoreCase(facet
							.getType())) {
						if (FACET_KEY_BLANK.equalsIgnoreCase(strFacetValue)) {
							srchReqFilterBld.must(FilterBuilders.missingFilter(
									AdvSearchOptionTable.parseCondition(
											objSQB.getBasicKeyword(),
											facet.getKey()).getAttributeName())
									.nullValue(true));
						} else if (strFacetValue.contains(FACET_KEY_NOT_)) {
							strFacetValue = strFacetValue.replace(
									FACET_KEY_NOT_, BLANK_TERM);
							srchReqFilterBld.mustNot(FilterBuilders.termFilter(
									AdvSearchOptionTable.parseCondition(
											objSQB.getBasicKeyword(),
											facet.getKey()).getAttributeName(),
									QueryParser.escape(strFacetValue)));
						} else {
							srchReqFilterBld.must(FilterBuilders.termFilter(
									AdvSearchOptionTable.parseCondition(
											objSQB.getBasicKeyword(),
											facet.getKey()).getAttributeName(),
									QueryParser.escape(strFacetValue)));
						}
					} else {
						AdvSearchOptionTable option = AdvSearchOptionTable
								.parseCondition(objSQB.getBasicKeyword(),
										facet.getKey());
						if (option.isParent()) {
							srchReqFilterBld.must(FilterBuilders.termFilter(
									option.getAttributeName(),
									QueryParser.escape(strFacetValue)));
						} else {
							if (StringUtils.endsWithIgnoreCase(strFacetValue,
									FACET_KEY_NO)) {
								srchReqFilterBld.mustNot(FilterBuilders
										.hasChildFilter(ES_NODE_USAGE,
												QueryBuilders.matchAllQuery()));
							} else if (StringUtils.endsWithIgnoreCase(
									strFacetValue, FACET_KEY_YES)) {
								srchReqFilterBld.must(FilterBuilders
										.hasChildFilter(ES_NODE_USAGE,
												QueryBuilders.matchAllQuery()));
							}
						}

					}
				}

				// ////////////////////////////////////////////////////////////////////////////////////////////////
			}
		}

		return srchReqFilterBld;
	}

	/**
	 * Refine search filters.
	 * 
	 * @param objSQB
	 *            SearchQueryBean containing search criteria.
	 * @return FilterBuilder for refine search.
	 */
	private static FilterBuilder appendRefineSearch(final SearchQueryBean objSQB) {
		FilterBuilder objparamSB = null;
		String strFilterTerms = objSQB.getFilterTerms();
		LOGGER.trace("Refine Search Term : {}", strFilterTerms);

		if (StringUtils.isNotBlank(strFilterTerms)) { // strFilterTerms == null
														// ||
														// strFilterTerms.trim().length()
														// == 0
			objparamSB = prepareBasicSearch(objSQB, strFilterTerms);
		}

		return objparamSB;
	}

	/**
	 * Sorting criteria.
	 * 
	 * @param objSQB
	 *            SearchQueryBean containing search criteria.
	 * @return FieldSortBuilder.
	 */
	private static FieldSortBuilder getSortOrder(final SearchQueryBean objSQB) {
		FieldSortBuilder fieldSortBld = null;
		String strSortBy = null;

		LOGGER.debug("appending orderBy clause {}", objSQB.getSortAttribute());
		if (StringUtils.isNotEmpty(objSQB.getSortAttribute())) {
			strSortBy = getSortByDCTMfield(objSQB.getSortAttribute());
			SortOrder strSortType = (StringUtils.equalsIgnoreCase(
					objSQB.getSortType(), SORT_ORDER_DESC)) ? SortOrder.DESC
					: SortOrder.ASC;
			fieldSortBld = new FieldSortBuilder(strSortBy);
			fieldSortBld.order(strSortType);
		}
		return fieldSortBld;
	}

	/***
	 * Returns DCTM equivalent field Name.
	 * 
	 * @param strColumnName
	 *            sorting key.
	 * @return String actual attribute name.
	 */
	private static String getSortByDCTMfield(final String strColumnName) {
		String sortField = PrsnLibImage.OBJECT_NAME;
		if (StringUtils.startsWithIgnoreCase(strColumnName,
				SORT_KEY_DATE_INGESTED)) {
			sortField = PrsnLibImage.C_INGEST_DATE;
		} else if (strColumnName.equalsIgnoreCase(SORT_KEY_LAST_MODIFIED)) {
			sortField = PrsnLibImage.R_MODIFY_DATE;
		} else if (strColumnName.equalsIgnoreCase(SORT_KEY_FILE_NAME)) {
			sortField = PrsnLibImage.OBJECT_NAME;
		} else if (strColumnName.equalsIgnoreCase(SORT_KEY_ORIGINAL_FILE_NAME)) {
			sortField = PrsnLibImage.C_ORIGINAL_FILE_NAME;
		} else if (strColumnName.equalsIgnoreCase(SORT_KEY_COLLECTION_TITLE)) {
			sortField = PrsnLibImage.C_COLLECTION_TITLE;
		} else if (strColumnName.equalsIgnoreCase(SORT_KEY_COLLECTION_ID)) {
			sortField = PrsnLibImage.C_COLLECTION_ID + DOT_SEPARATOR
					+ ATTRIBUTE_FIELD_UNTOUCHED_STRING;
		} else if (strColumnName.equalsIgnoreCase(SORT_KEY_ASSET_CREATION_DATE)) {
			sortField = PrsnLibImage.C_ASSET_CREATION_DATE;
		}
		return sortField;
	}

	/**
	 * Executes search and return the resultant result into resultitem beans.
	 * 
	 * @param client
	 *            Client
	 * @param objSQB
	 *            SearchQueryBean containing search criteria.
	 * @return list of result item bean.
	 */
	public List<ResultItemBean> getSearchResultAll(final Client client,
			final SearchQueryBean objSQB) {

		SearchRequestBuilder srchReqBld = prepareSearchQuery(client, objSQB);

		int startindex = Integer.parseInt(objSQB.getStartIndex());
		int endindex = Integer.parseInt(objSQB.getMaxResult());

		if (startindex < 2) {
			startindex = 0;
		}

		LOGGER.debug("StartIndex : {}, Size : {}", startindex, endindex);

		srchReqBld.addField(PrsnLibImage.R_OBJECT_ID)
				.addField(PrsnLibImage.ACL_NAME)
				.addField(PrsnLibImage.C_ADMIN_BUSINESS)
				.addField(PrsnLibImage.C_ASSET_ID).setFrom(startindex)
				.setSize(endindex);
		// .setExplain(true);

		SearchResponse response = execute(srchReqBld);
		SearchResultBean srb = new SearchResultBean();

		srb.setResultCount((int) response.getHits().totalHits());

		srb.setLstSearchResult(readSearchResultResponse(response));

		return srb.getLstSearchResult();
	}

	/**
	 * Method to prepare SearchTerm.
	 * 
	 * @param objSQB
	 *            SearchQueryBean containing search criteria.
	 * @param searchTerm
	 *            search term.
	 * @return BoolFilterBuilder filter for basic search.
	 */
	public static FilterBuilder prepareBasicSearch(
			final SearchQueryBean objSQB, final String searchTerm) {
		return SimpleSearchFilterUtil.prepareBasicSearchFilter(objSQB,
				searchTerm);
	}

	/**
	 * Develops the AND-ed filter for Exclusion.
	 * 
	 * @param objSQB
	 *            SearchQueryBean containing search criteria.
	 * @return AndFilterBuilder AND-ed filters of exclusion list.
	 */
	private static AndFilterBuilder appendExclusionFilter(
			final SearchQueryBean objSQB) {
		AndFilterBuilder filterBuilder = null;

		Map<String, String> exclusionlist = objSQB.getExclusionList();
		if (null != exclusionlist && !exclusionlist.isEmpty()) {

			Iterator<java.util.Map.Entry<String, String>> iterator = exclusionlist
					.entrySet().iterator();
			while (iterator.hasNext()) {
				java.util.Map.Entry<String, String> exclude = iterator.next();

				if (StringUtils.isNotBlank(exclude.getKey())
						&& StringUtils.isNotBlank(exclude.getValue())) {
					AdvSearchOptionTable option = AdvSearchOptionTable
							.parseCondition(objSQB.getBasicKeyword(),
									exclude.getKey());
					String[] valueArray = StringUtils.split(exclude.getValue(),
							COMMA_SEPARATER);

					for (String value : valueArray) {
						FilterBuilder excludeFilter = null;
						if (option.isParent()) {
							if (option.isRepeating()) {
								excludeFilter = FilterBuilders.nestedFilter(
										option.getAttributeName(),
										FilterBuilders.termFilter(
												option.getSearchableAttr(),
												StringUtils.lowerCase(value)));
							} else {
								excludeFilter = FilterBuilders.termFilter(
										option.getAttributeName(),
										StringUtils.lowerCase(value));
							}
						} else {
							excludeFilter = FilterBuilders
									.hasChildFilter(
											option.getObjectType(),
											QueryBuilders.filteredQuery(
													QueryBuilders
															.matchAllQuery(),
													FilterBuilders.termFilter(
															option.getAttributeName(),
															StringUtils
																	.lowerCase(value))));
						}
						if (null != excludeFilter) {
							if (null == filterBuilder) {
								filterBuilder = FilterBuilders
										.andFilter(FilterBuilders
												.notFilter(excludeFilter));
							} else {
								filterBuilder.add(FilterBuilders
										.notFilter(excludeFilter));
							}
						}
					}
				}
			}
		}
		return filterBuilder;
	}

	/**
	 * Method will be used to convert date format into long for ES range query
	 * formation.
	 * 
	 * @param dateObj
	 *            as String
	 * @return long
	 */
	private static long getSearchDateLongValue(final String dateObj) {
		long dateData = 0L;
		try {
			// create SimpleDateFormat object with source string date format
			SimpleDateFormat sdfSource = new SimpleDateFormat(
					APPLICATION_DATE_FORMAT);

			// parse the string into Date object
			Date date = sdfSource.parse(dateObj);

			dateData = date.getTime();
		} catch (ParseException pse) {
			LOGGER.error(pse.toString(), pse);
		}
		return dateData;
	}

	/**
	 * To get the Status of bulk task action.
	 * 
	 * @param client
	 *            ES Client. preferred used transport client.
	 * @param templateId
	 *            task id.
	 * @return String
	 */
	public static String getStatusFromES(final Client client,
			final String templateId) {

		SearchRequestBuilder srchReqBld = client
				.prepareSearch(ELASTICSEARCH_INDEX);
		srchReqBld.setSearchType(SearchType.QUERY_THEN_FETCH);
		srchReqBld.setQuery(
				QueryBuilders.queryString(templateId).field(OBJECT_ID))
				.addField(MESSAGE);

		String status = null;
		SearchResponse response = execute(srchReqBld);
		for (final SearchHit hit : response.getHits()) {
			final Iterator<SearchHitField> iterator = hit.iterator();
			while (iterator.hasNext()) {
				final SearchHitField hitField = iterator.next();

				for (final Object value : hitField.getValues()) {
					if (hitField.getName().equals(MESSAGE)) {
						status = String.valueOf(value);
					}
				}
			}
		}
		return status;
	}

	/**
	 * Sets the message into the provided id of bulktemplate object of ES index.
	 * 
	 * @param client
	 *            ES Client. preferred used transport client.
	 * @param templateId
	 *            String
	 * @param message
	 *            String
	 * @return boolean
	 * @throws ElasticSearchException
	 *             ElasticSearchException
	 * @throws IOException
	 *             IOException
	 */
	public static boolean setStatusInES(final Client client,
			final String templateId, final String message)
			throws ElasticSearchException, IOException {
		client.prepareIndex(ELASTICSEARCH_INDEX, ES_NODE_BULKTEMPLATE,
				templateId)
				.setSource(
						XContentFactory.jsonBuilder().startObject()
								.field(OBJECT_ID, templateId)
								.field(MESSAGE, message).endObject()).execute()
				.actionGet();
		return true;
	}

	/**
	 * Method intended to keep client connection active.
	 * 
	 * @param client
	 *            ES Client. preferred used transport client.
	 * @return true if successfully runs a default query.
	 */
	public static boolean wakeUp(final Client client) {
		boolean isTimedOut = execute(
				client.prepareSearch(ELASTICSEARCH_INDEX)
						.setSearchType(SearchType.QUERY_THEN_FETCH)
						.setQuery(QueryBuilders.matchAllQuery()).setSize(1))
				.isTimedOut();
		return !isTimedOut;
	}
}

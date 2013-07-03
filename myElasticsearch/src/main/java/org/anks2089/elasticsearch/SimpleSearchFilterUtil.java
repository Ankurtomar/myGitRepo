/**
 ************************************************************************************************ 
 * Copyright (C) 2012 
 * Pearson Plc. All Rights Reserved 
 * Project : Asset Library 
 * File : SimpleSearchFilterUtil.java
 * Created By : Ankur_Tomar
 * Creation Time : Jan 12, 2013
 * 
 * Description :
 *              
 * 
 ************************************************************************************************ 
 */

package org.anks2089.elasticsearch;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.anks2089.bean.SearchQueryBean;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.elasticsearch.index.query.AndFilterBuilder;
import org.elasticsearch.index.query.BoolFilterBuilder;
import org.elasticsearch.index.query.FilterBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder.Type;
import org.elasticsearch.index.query.OrFilterBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.index.query.QueryStringQueryBuilder.Operator;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ankur_Tomar
 * 
 */
public final class SimpleSearchFilterUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSearchFilterUtil.class);

    /**
     * private constructor.
     */
    private SimpleSearchFilterUtil() {
        super();
    }

    /**
     * Recursive method to create plural based search filter for Repeating/Nested attributes.
     * 
     * @param basicAttr
     *            basicAttr list of basic search attribute.
     * @param previousToken
     *            filter for previous search term use in recursive calls.
     * @param tokens
     *            search term tokens.
     * @param current
     *            occurence of token.
     * @return filter for current token.
     */
    private static FilterBuilder termHandler(final List<String> basicAttr, final FilterBuilder previousToken,
            final String[] tokens, final int current) {
        FilterBuilder returnFilter = null;
        FilterBuilder filter = null;
        FilterBuilder filterToAdd = null;

        int next = current + 1;

        LOGGER.trace("Inside parser - {} {}", Arrays.toString(tokens), String.valueOf(current));

        if (tokens.length > next) {
            String[] token = new String[2];
            token[0] = tokens[current];
            token[1] = tokens[next];
            boolean ranDeep = false;

            if (StringUtils.equals(IElasticSearchUtils.OPERATOR_OR, token[0])) {
                LOGGER.trace("Found OR operator");
                filterToAdd = getTokenFilter(basicAttr, token[1]);

                if (previousToken instanceof OrFilterBuilder && null != filterToAdd) {
                    filter = ((OrFilterBuilder) previousToken).add(filterToAdd);
                } else if ((null != filterToAdd && null != previousToken)) {
                    filter = (FilterBuilder) FilterBuilders.orFilter(previousToken).add(filterToAdd);
                } else {
                    filter = filterToAdd;
                }
                next++;
            } else if (StringUtils.equals(IElasticSearchUtils.OPERATOR_NOT, token[0])
                    || StringUtils.equals(IElasticSearchUtils.OPERATOR_AND, token[0])) {

                LOGGER.trace("Found AND/NOT operator");
                filterToAdd = getTokenFilter(basicAttr, token[1]);

                if (StringUtils.equals(IElasticSearchUtils.OPERATOR_NOT, token[0])) {
                    filterToAdd = FilterBuilders.notFilter(filterToAdd);
                }

                if (previousToken instanceof AndFilterBuilder && null != filterToAdd) {
                    LOGGER.trace("Adding search filter for current token into AND-ed filter");
                    filter = ((AndFilterBuilder) previousToken).add(filterToAdd);
                } else if ((null != filterToAdd && null != previousToken)) {
                    filter = (FilterBuilder) FilterBuilders.andFilter(previousToken).add(filterToAdd);
                } else {
                    filter = filterToAdd;
                }
                next++;
            } else {
                LOGGER.trace("Found token - {}", token[0]);
                filterToAdd = getTokenFilter(basicAttr, token[0]);
                FilterBuilder previous = previousToken;

                if (tokens.length > (next + 1)
                        && StringUtils.equals(IElasticSearchUtils.OPERATOR_OR, tokens[next + 1])) {

                    previous = filterToAdd;
                    filterToAdd = getTokenFilter(basicAttr, token[1]);

                    LOGGER.trace("recursive call - {}", tokens[next + 1]);
                    filterToAdd = termHandler(basicAttr, filterToAdd, tokens, next + 1);
                    ranDeep = true;
                }

                if (previous instanceof AndFilterBuilder && null != filterToAdd) {
                    LOGGER.trace("adding to already existing");
                    filter = ((AndFilterBuilder) previous).add(filterToAdd);
                } else if ((null != filterToAdd && null != previous)) {
                    LOGGER.trace("creating new existing");
                    filter = FilterBuilders.andFilter(previous).add(filterToAdd);
                } else {
                    LOGGER.trace("No need to create.");
                    filter = filterToAdd;
                }
            }
            if (ranDeep) {
                returnFilter = filter;
            } else {
                returnFilter = termHandler(basicAttr, filter, tokens, next);

            }

        } else {
            if (tokens.length > current) {
                filterToAdd = getTokenFilter(basicAttr, tokens[current]);
            }

            // To handle last token
            if (previousToken == null) {
                returnFilter = filterToAdd;
            } else if ((tokens.length - 1) == current) {
                if (previousToken instanceof OrFilterBuilder && null != filterToAdd) {
                    returnFilter = ((OrFilterBuilder) previousToken).add(filterToAdd);
                } else if (previousToken instanceof AndFilterBuilder && null != filterToAdd) {
                    returnFilter = ((AndFilterBuilder) previousToken).add(filterToAdd);
                } else {
                    returnFilter = FilterBuilders.andFilter(previousToken).add(filterToAdd);
                }
            } else {
                returnFilter = previousToken;
            }
        }
        return returnFilter;
    }

    /**
     * Returns the OR-ed filter for the basic search.
     * 
     * @param basicAttr
     *            basicAttr list of basic search attribute.
     * @param term
     *            search term.
     * @return OrFilterBuilder object.
     */
    private static FilterBuilder getTokenFilter(final List<String> basicAttr, final String term) {

        OrFilterBuilder returnFilter = null;

        String searchTerm = StringUtils.lowerCase(term);

        // isAnalyzed is a boolean flag to check if the search token is free of any special characters
        boolean isAnalyzed = true;
        for (String strTemp : IElasticSearchUtils.SPECIAL_CHARS) {
            if (StringUtils.containsAny(searchTerm, strTemp)) {
                isAnalyzed = false;
                break;
            }
        }

        // strUnanalyzeSfx is a string suffix to add into ES index attributes if search token contains special
        // characters
        String strUnanalyzeSfx = IElasticSearchUtils.DOT_SEPARATOR
                + IElasticSearchUtils.ATTRIBUTE_FIELD_UNTOUCHED_STRING;

        QueryStringQueryBuilder attrFilter = QueryBuilders.queryString(searchTerm).defaultOperator(Operator.AND);

        returnFilter = FilterBuilders.orFilter(FilterBuilders.queryFilter(attrFilter));

        FilterBuilder filter = null;

        for (String attr : basicAttr) {
            AdvSearchOptionTable option = AdvSearchOptionTable.parseCondition(basicAttr, attr);
            final String strAttribute = option.getAttributeName();
            final String strSearchAttr = (isAnalyzed) ? strAttribute : (strAttribute + strUnanalyzeSfx);
            if (option.isRepeating()) {
                filter = FilterBuilders.queryFilter(QueryBuilders.queryString(searchTerm).field(strSearchAttr));
                returnFilter.add(FilterBuilders.nestedFilter(strAttribute, filter));
            } else {
                attrFilter.field(strSearchAttr);
            }
        }

        return FilterBuilders.boolFilter().must(returnFilter);
    }

    /**
     * Generates basic search filters for non combined basic search term.
     * 
     * @param basicAttr
     *            list of basic search attribute.
     * @param term
     *            search term.
     * @return basic search term filter.
     */
    private static FilterBuilder getBasicSearchFilter(final List<String> basicAttr, final String term) {

        final String[] tokens = StringUtils.split(term);
        return termHandler(basicAttr, null, tokens, 0);
    }

    /**
     * Prepares the basic search filter for search term.
     * 
     * @param objSQB
     *            searchQueryBean
     * @param searchTerm
     *            search term
     * @return filter for basic search.
     */
    public static FilterBuilder prepareBasicSearchFilter(final SearchQueryBean objSQB, final String searchTerm) {

        // changes for DE1686 starts
        // FilterBuilder filterBuilderBasic = null;
        // changes for DE1686 ends

        // int iArraySize = BASIC_SEARCH_REPEATING_ATTR.length;
        String term = searchTerm;
        String finalTerm = null;
        BoolFilterBuilder basicFilter = null;
        // NestedFilterBuilder[] nestFilterRepeatAttr = new NestedFilterBuilder[iArraySize];
        OrFilterBuilder orFilterBuilder = null;
        LOGGER.debug(term);

        if (StringUtils.isNotBlank(term)) {
            basicFilter = FilterBuilders.boolFilter();
            boolean needOperator = false;
            StringBuffer finalSearchTerm = new StringBuffer();

            if (term.startsWith(IElasticSearchUtils.CHAR_STRING_FOR_DOUBLEQUOTE)) {
                term = IElasticSearchUtils.WHITESPACE_STRING + term;
            }

            final String[] doubleQuoteToken = StringUtils.split(term, IElasticSearchUtils.CHAR_STRING_FOR_DOUBLEQUOTE);
            int count = 0;
            String opr = null;
            String operator = null;
            LOGGER.trace("doubleQuoteToken: {}", doubleQuoteToken);

            for (String token : doubleQuoteToken) {
                count++;
                LOGGER.trace("Token - {}, count - {}", token, count);

                if (count % 2 != 0) {

                    final StringTokenizer spaceHandler = new StringTokenizer(token);
                    boolean hasSingleQuote = false;
                    boolean hasContainsOpr = false;

                    while (spaceHandler.hasMoreTokens()) {
                        String token2 = spaceHandler.nextToken().trim();
                        finalSearchTerm.trimToSize();

                        LOGGER.trace("singleQuote - {}, contain - {}", hasSingleQuote, hasContainsOpr);
                        LOGGER.trace("SearchTerm token2 - {}", token2);

                        if (!hasSingleQuote && !hasContainsOpr) {
                            if (token2.equalsIgnoreCase(IElasticSearchUtils.OPERATOR_AND)) {

                                if (finalSearchTerm.length() > 0) {
                                    finalSearchTerm.append(IElasticSearchUtils.WHITESPACE_STRING);
                                    finalSearchTerm.append(IElasticSearchUtils.OPERATOR_AND);
                                } else {
                                    opr = IElasticSearchUtils.OPERATOR_AND;
                                }
                                operator = IElasticSearchUtils.OPERATOR_AND;
                                if (needOperator) {
                                    needOperator = false;
                                }
                                continue;
                            } else if (token2.equalsIgnoreCase(IElasticSearchUtils.OPERATOR_NOT)) {

                                finalSearchTerm.append(IElasticSearchUtils.WHITESPACE_STRING);
                                finalSearchTerm.append(IElasticSearchUtils.OPERATOR_NOT);

                                operator = IElasticSearchUtils.OPERATOR_NOT;
                                if (needOperator) {
                                    needOperator = false;
                                }
                                continue;
                            } else if (token2.equalsIgnoreCase(IElasticSearchUtils.OPERATOR_OR)) {

                                if (finalSearchTerm.length() > 0) {
                                    finalSearchTerm.append(IElasticSearchUtils.WHITESPACE_STRING);
                                    finalSearchTerm.append(IElasticSearchUtils.OPERATOR_OR);
                                } else {
                                    opr = IElasticSearchUtils.OPERATOR_OR;
                                }
                                operator = IElasticSearchUtils.OPERATOR_OR;
                                if (needOperator) {
                                    needOperator = false;
                                }
                                continue;
                            } else {
                                if (count >= 1 && needOperator) {
                                    operator = IElasticSearchUtils.OPERATOR_AND;
                                    if (needOperator) {
                                        needOperator = false;
                                    }
                                }
                            }
                        }

                        if (token2.contains(IElasticSearchUtils.SINGLEQUOTE_STRING)
                                && ((token2.startsWith(IElasticSearchUtils.SINGLEQUOTE_STRING) && (!hasSingleQuote)) || (token2
                                        .endsWith(IElasticSearchUtils.SINGLEQUOTE_STRING) && hasSingleQuote))) {
                            finalSearchTerm.append(IElasticSearchUtils.WHITESPACE_STRING);
                            hasSingleQuote = (!hasSingleQuote);
                            finalSearchTerm.append(token2);

                            if (token2.endsWith(IElasticSearchUtils.SINGLEQUOTE_STRING)) {
                                hasSingleQuote = false;
                                needOperator = true;
                            }
                        }

                        else if ((token2.startsWith(IElasticSearchUtils.CHAR_FOR_CONTAINS[0]) && (!hasContainsOpr))
                                || (token2.endsWith(IElasticSearchUtils.CHAR_FOR_CONTAINS[1]) && hasContainsOpr)) {
                            hasContainsOpr = (!hasContainsOpr);
                            StringBuffer containsClause = new StringBuffer(token2);
                            LOGGER.trace("Contains clause : {}", containsClause.toString());
                            if (spaceHandler.hasMoreTokens()
                                    && !(token2.endsWith(IElasticSearchUtils.CHAR_FOR_CONTAINS[1]))) {
                                while (hasContainsOpr) {
                                    if (spaceHandler.hasMoreTokens()) {
                                        token2 = spaceHandler.nextToken().trim();
                                        containsClause.append(IElasticSearchUtils.WHITESPACE_STRING);
                                        containsClause.append(token2);
                                    }
                                    if (token2.endsWith(IElasticSearchUtils.CHAR_FOR_CONTAINS[1])
                                            || !spaceHandler.hasMoreTokens()) {
                                        hasContainsOpr = false;
                                        needOperator = (needOperator) ? true : false;
                                    }
                                }
                            } else {
                                hasContainsOpr = false;
                                needOperator = (needOperator) ? true : false;
                            }

                            if (StringUtils.isNotBlank(operator)) {
                                final String tmp = finalSearchTerm.toString();
                                if (StringUtils.isNotBlank(tmp) && StringUtils.lastIndexOf(tmp, operator) > -1) {
                                    LOGGER.trace("Replacing extra operator : {} - operator - {}",
                                            finalSearchTerm.toString(), operator);
                                    finalSearchTerm.replace(finalSearchTerm.lastIndexOf(operator),
                                            finalSearchTerm.length(), IElasticSearchUtils.BLANK_TERM);
                                }
                            } else {
                                operator = IElasticSearchUtils.OPERATOR_AND;
                            }

                            String containsClauseStr = containsClause.toString();
                            containsClauseStr = StringUtils.removeStart(containsClauseStr,
                                    IElasticSearchUtils.CHAR_FOR_CONTAINS[0]);
                            containsClauseStr = StringUtils.removeEnd(containsClauseStr,
                                    IElasticSearchUtils.CHAR_FOR_CONTAINS[1]);

                            LOGGER.trace("Contains clause : {}", containsClauseStr);

                            FilterBuilder containsFilter = prepareKeywordFilter(IElasticSearchUtils.WILDCARD_STRING
                                    + containsClauseStr + IElasticSearchUtils.WILDCARD_STRING,
                                    objSQB.getBasicKeyword(), 2);

                            if (StringUtils.endsWithIgnoreCase(operator, IElasticSearchUtils.OPERATOR_AND)) {
                                basicFilter.must(containsFilter);
                                continue;
                            } else if (StringUtils.endsWithIgnoreCase(operator, IElasticSearchUtils.OPERATOR_NOT)) {
                                basicFilter.mustNot(containsFilter);
                                continue;
                            } else if (StringUtils.endsWithIgnoreCase(operator, IElasticSearchUtils.OPERATOR_OR)) {
                                if (null == orFilterBuilder) {
                                    orFilterBuilder = FilterBuilders.orFilter(containsFilter);
                                } else {
                                    orFilterBuilder.add(containsFilter);
                                }
                                continue;
                            }
                        } else {
                            finalSearchTerm.append(IElasticSearchUtils.WHITESPACE_STRING);
                            finalSearchTerm.append(QueryParser.escape(token2));
                            needOperator = true;
                        }
                    }

                    LOGGER.trace("operator: {}", operator);

                } else {
                    if (StringUtils.isNotBlank(operator)) {
                        String tmp = finalSearchTerm.toString();
                        if (StringUtils.isNotBlank(tmp) && StringUtils.lastIndexOf(tmp, operator) > -1) {
                            finalSearchTerm.replace(finalSearchTerm.lastIndexOf(operator), finalSearchTerm.length(),
                                    IElasticSearchUtils.BLANK_TERM);
                        }
                    } else {
                        operator = IElasticSearchUtils.OPERATOR_AND;
                    }
                    LOGGER.trace("operator: {}", operator);
                    // FilterBuilder exactMatchFilter = prepareKeywordFilter(token.trim(), BASIC_SEARCH_ATTR_GROUP, 1);

                    // FilterBuilder exactMatchFilter = FilterBuilders.queryFilter(QueryBuilders.matchQuery("_all",
                    // QueryParser.escape(token.trim())).type(Type.PHRASE));

                    // ANDed to resolve issues causing DE1790 and DE1850.
                    AndFilterBuilder exactMatchFilter = FilterBuilders.andFilter(FilterBuilders
                            .queryFilter(QueryBuilders.matchQuery("_all",
                                    QueryParser.escape(StringUtils.lowerCase(token.trim()))).type(Type.PHRASE)));
                    exactMatchFilter.add(prepareKeywordFilter(
                            IElasticSearchUtils.WILDCARD_STRING + StringUtils.lowerCase(token.trim())
                                    + IElasticSearchUtils.WILDCARD_STRING, objSQB.getBasicKeyword(), 2));
                    //

                    if (StringUtils.endsWithIgnoreCase(operator, IElasticSearchUtils.OPERATOR_AND)) {
                        basicFilter.must(exactMatchFilter);
                        continue;
                    } else if (StringUtils.endsWithIgnoreCase(operator, IElasticSearchUtils.OPERATOR_NOT)) {
                        basicFilter.mustNot(exactMatchFilter);
                        continue;
                    } else if (StringUtils.endsWithIgnoreCase(operator, IElasticSearchUtils.OPERATOR_OR)) {
                        if (null == orFilterBuilder) {
                            orFilterBuilder = FilterBuilders.orFilter(exactMatchFilter);
                        } else {
                            orFilterBuilder.add(exactMatchFilter);
                        }
                        continue;
                    }
                    needOperator = (needOperator) ? true : false;
                }

            }

            finalTerm = finalSearchTerm.toString().trim();
            LOGGER.trace("finalTerm: {}", finalTerm);

            if (StringUtils.isNotBlank(finalTerm)) {

                // FilterBuilder filter =
                // FilterBuilders.queryFilter(QueryBuilders.queryString(groupSearchTerm(finalTerm))
                // .defaultOperator(Operator.AND).field("_all"));

                // combined the changes done for DE1686 start.
                // OR-ed the filters for repeating and and non-repeating attributes.
                // QueryStringQueryBuilder nonRepeatAttrFilter = QueryBuilders.queryString(groupSearchTerm(finalTerm))
                // .defaultOperator(Operator.AND);
                // for (String strNonRepeatAttr : BASIC_SEARCH_NONREPEATING_ATTR) {
                // LOGGER.debug("Non repeating attribute: {} added to search filter.", strNonRepeatAttr);
                // nonRepeatAttrFilter.field(strNonRepeatAttr);
                // }
                //
                // OrFilterBuilder orfilterBasic = FilterBuilders
                // .orFilter(FilterBuilders.queryFilter(nonRepeatAttrFilter));
                //
                // for (int i = 0; i < iArraySize; i++) {
                // LOGGER.debug("Nested filter created for repeating attribute: {}", BASIC_SEARCH_REPEATING_ATTR[i]);
                // NestedFilterBuilder nestedFilter = FilterBuilders.nestedFilter(BASIC_SEARCH_REPEATING_ATTR[i],
                // QueryBuilders.queryString(groupSearchTerm(finalTerm)).field(BASIC_SEARCH_REPEATING_ATTR[i])
                // .defaultOperator(Operator.AND));
                // orfilterBasic.add(nestedFilter);
                // }

                FilterBuilder orfilterBasic = SimpleSearchFilterUtil.getBasicSearchFilter(objSQB.getBasicKeyword(),
                        finalTerm);

                if (StringUtils.endsWithIgnoreCase(opr, IElasticSearchUtils.OPERATOR_OR)) {
                    LOGGER.trace("if (StringUtils.endsWithIgnoreCase(opr, IElasticSearchUtils.OPERATOR_OR)) {");
                    if (null == orFilterBuilder) {
                        orFilterBuilder = FilterBuilders.orFilter(orfilterBasic);
                    } else {
                        orFilterBuilder.add(orfilterBasic);
                    }
                    basicFilter.mustNot(orfilterBasic);
                } else {
                    basicFilter.must(orfilterBasic);
                }
                // DE1686 end.

                // if (StringUtils.endsWithIgnoreCase(opr, IElasticSearchUtils.OPERATOR_OR)) {
                // LOGGER.debug("if (StringUtils.endsWithIgnoreCase(opr, IElasticSearchUtils.OPERATOR_OR)) {");
                // if (null == orFilterBuilder) {
                // orFilterBuilder = FilterBuilders.orFilter(filter);
                // } else {
                // orFilterBuilder.add(filter);
                // }
                // } else if (StringUtils.endsWithIgnoreCase(opr, IElasticSearchUtils.OPERATOR_NOT)) {
                // basicFilter.mustNot(filter);
                // } else {
                // basicFilter.must(filter);
                // }
            } else {
                LOGGER.debug("size of normal search term is zero.");
            }
        }

        if (null != orFilterBuilder) {
            if (null != basicFilter) {
                orFilterBuilder.add(basicFilter);
            }
            basicFilter = FilterBuilders.boolFilter().must(orFilterBuilder);
        }

        // if (StringUtils.isNotBlank(finalTerm)) {
        // OrFilterBuilder orfilterBasic = FilterBuilders.orFilter(basicFilter);
        // // filterBuilderBasic = FilterBuilders.orFilter(basicFilter);
        // for (NestedFilterBuilder nestFiltBuilder : nestFilterRepeatAttr) {
        // // filterBuilderBasic = FilterBuilders.orFilter(filterBuilderBasic, nestFiltBuilder);
        // orfilterBasic.add(nestFiltBuilder);
        // }
        // filterBuilderBasic = orfilterBasic;
        // } else {
        // filterBuilderBasic = basicFilter;
        // }

        return basicFilter;
    }

    /**
     * Prepare OR-ed filters to form search query filter for array of attributes. 0 - Normal 1 - Exact match 2 -
     * Wildcard 3 - Has value
     * 
     * @param term
     *            search term.
     * @param attrGroup
     *            array of attributes to be searched.
     * @param operator
     *            type of query.
     * @return OrFilterBuilder
     */
    public static OrFilterBuilder prepareKeywordFilter(final String term, final List<String> attrGroup,
            final int operator) {
        OrFilterBuilder orFilterBuilder = null;
        LOGGER.trace("operator - {}", operator);
        String searchTerm = StringUtils.lowerCase(term);

        switch (operator) {
        case 0: // Normal
            FilterBuilder normalFilter = null;
            for (String attribute : attrGroup) {
                // String[] words = attribute.split("-");
                // boolean isRepeating = StringUtils.equalsIgnoreCase(words[3], REPEATING_FLAG_CHAR);

                AdvSearchOptionTable option = AdvSearchOptionTable.parseCondition(attrGroup, attribute);

                if (option.isRepeating()) {
                    normalFilter = FilterBuilders.nestedFilter(
                            option.getAttributeName(),
                            QueryBuilders.queryString(searchTerm).field(option.getSearchableAttr())
                                    .defaultOperator(Operator.AND).analyzeWildcard(true));
                } else {
                    normalFilter = FilterBuilders.queryFilter(QueryBuilders.queryString(searchTerm)
                            .defaultOperator(Operator.AND).field(option.getSearchableAttr()));
                }
                if (null == orFilterBuilder) {
                    orFilterBuilder = FilterBuilders.orFilter(normalFilter);
                } else {
                    orFilterBuilder.add(normalFilter);
                }
            }
            break;

        case 1: // Exact match

            FilterBuilder advExactFilter = null;
            for (String attribute : attrGroup) {

                final AdvSearchOptionTable option = AdvSearchOptionTable.parseCondition(attrGroup, attribute);
                if (option.isRepeating()) {
                    advExactFilter = FilterBuilders.nestedFilter(option.getAttributeName(),
                            QueryBuilders.termQuery(option.getSearchableAttr(), searchTerm)

                    // matchQuery(option.getSearchableAttr(), term).type(Type.PHRASE).operator(
                    // MatchQueryBuilder.Operator.AND)
                            );
                } else {
                    advExactFilter = FilterBuilders.queryFilter(QueryBuilders
                            .matchQuery(option.getSearchableAttr(), searchTerm).type(Type.PHRASE)
                            .operator(MatchQueryBuilder.Operator.AND));
                }
                if (null == orFilterBuilder) {
                    orFilterBuilder = FilterBuilders.orFilter(advExactFilter);
                } else {
                    orFilterBuilder.add(advExactFilter);
                }
            }
            break;

        case 2: // Wildcard

            FilterBuilder containsFilter = null;
            for (String attribute : attrGroup) {
                // String[] words = attribute.split("-");
                // boolean isRepeating = StringUtils.equalsIgnoreCase(words[3], REPEATING_FLAG_CHAR);

                AdvSearchOptionTable option = AdvSearchOptionTable.parseCondition(attrGroup, attribute);
                if (option.isRepeating()) {
                    containsFilter = FilterBuilders.nestedFilter(option.getAttributeName(), QueryBuilders.boolQuery()
                            .must(new WildcardQueryBuilder(option.getSearchableAttr(), searchTerm)));
                } else {
                    containsFilter = FilterBuilders.queryFilter(QueryBuilders.boolQuery().must(
                            new WildcardQueryBuilder(option.getSearchableAttr(), searchTerm)));
                }
                if (null == orFilterBuilder) {
                    orFilterBuilder = FilterBuilders.orFilter(containsFilter);
                } else {
                    orFilterBuilder.add(containsFilter);
                }
            }
            break;

        case 3: // Has value

            FilterBuilder hasValueFilter = null;
            for (String attribute : attrGroup) {

                // String[] words = attribute.split("-");
                // boolean isRepeating = StringUtils.equalsIgnoreCase(words[3], REPEATING_FLAG_CHAR);

                AdvSearchOptionTable option = AdvSearchOptionTable.parseCondition(attrGroup, attribute);
                if (option.isRepeating()) {
                    hasValueFilter = FilterBuilders.nestedFilter(option.getAttributeName(),
                            FilterBuilders.existsFilter(option.getSearchableAttr()));

                    // FilterBuilders.andFilter(existsFilter(option.getSearchableAttr())).add(
                    // FilterBuilders.notFilter(FilterBuilders.termFilter(option.getSearchableAttr(),
                    // BLANK_TERM))));

                } else {
                    hasValueFilter = FilterBuilders.existsFilter(option.getSearchableAttr());

                    // hasValueFilter = FilterBuilders.andFilter(existsFilter(option.getSearchableAttr())).add(
                    // FilterBuilders.notFilter(FilterBuilders.termFilter(option.getSearchableAttr(),
                    // BLANK_TERM)));
                }

                if (null == orFilterBuilder) {
                    orFilterBuilder = FilterBuilders.orFilter(hasValueFilter);
                } else {
                    orFilterBuilder.add(hasValueFilter);
                }
            }
            break;
        default:
            break;
        }
        return orFilterBuilder;
    }

}

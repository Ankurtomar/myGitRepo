package org.anks2089.elasticsearch;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import lombok.extern.slf4j.Slf4j;

import org.anks2089.bean.ResultItemBean;
import org.anks2089.bean.SearchQueryBean;
import org.anks2089.bean.SearchResultBean;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;

import com.github.tlrx.elasticsearch.test.EsSetup;
import com.github.tlrx.elasticsearch.test.provider.JSONProvider;

@Slf4j
public enum ESClientTestUtil {
	INSTANCE;
	public String clustername = "test";

	/** Using a local node & client **/
	// private final Settings settings = ImmutableSettings.settingsBuilder()
	// .put("cluster.name", clustername)
	// .put(IElasticSearchUtils.ELASTICSEARCH_SETTING, true).build();

	/** Using a existing node & client **/
	private final Settings settings = ImmutableSettings.settingsBuilder()
			.put(IElasticSearchUtils.ELASTICSEARCH_SETTING, true)
			.put("cluster.name", clustername).build();

	Node node;
	private Client client;

	private String EXCEL_DATA_FILE = "config/data.xls";

	// create SimpleDateFormat object with desired date format
	private SimpleDateFormat sdfDestination = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss");

	private HSSFWorkbook workBook;
	private EsSetup esSetup;

	private void setUp(final String index) throws Exception {
		// Load excel workbook
		InputStream istream = null;
		/** Creating Input Stream **/
		istream = ClassLoader.getSystemResourceAsStream(EXCEL_DATA_FILE);

		/** Creating a POIFSFileSystem object **/
		POIFSFileSystem excelFileSystem = new POIFSFileSystem(istream);

		/** Creating a workbook using the File System **/
		if (null == workBook) {
			workBook = new HSSFWorkbook(excelFileSystem);
		}
		/** Using a existing node & client **/
		// client = new TransportClient(settings)
		// .addTransportAddress(new InetSocketTransportAddress(
		// "localhost", 8080));

		node = nodeBuilder().local(true).settings(settings).node();
		client = node.client();

		esSetup = new EsSetup(client);
		esSetup.execute(
				EsSetup.deleteAll(),
				EsSetup.createIndex(IElasticSearchUtils.ELASTICSEARCH_INDEX)
						.withSettings(
								EsSetup.fromClassPath("config/index.json"))
						.withMapping("assets",
								EsSetup.fromClassPath("config/asset.json"))
						.withMapping("lightbox",
								EsSetup.fromClassPath("config/lightbox.json"))
						.withMapping("usage",
								EsSetup.fromClassPath("config/usage.json"))
						.withData(prepareData()));

		log.debug("Index exists : {}",
				esSetup.exists(IElasticSearchUtils.ELASTICSEARCH_INDEX));
		log.debug("Total Document : {}",
				esSetup.count(IElasticSearchUtils.ELASTICSEARCH_INDEX));

		// createMyIndex(index);
		// setMapping(index, "assets",
		// Streams.copyToStringFromClasspath("/config/asset.json"));
		// setMapping(index, "lightbox",
		// Streams.copyToStringFromClasspath("/config/lightbox.json"));
		// setMapping(index, "usage",
		// Streams.copyToStringFromClasspath("/config/usage.json"));
		// setData(index, prepareData());
	}

	public void stopServer() {
		client.close();
		node.close();
		node.stop();
	}

	// private void setData(final String index, final JSONProvider prepareData)
	// {
	// client.index(indexRequest(index).source(prepareData.toJson()));
	// }
	//
	public Client getESClient(final String index) throws Exception {
		if (null == client) {
			setUp(index);
		}
		return client;
	}
	//
	// public void createMyIndex(final String index) {
	// log.info("creating index [{}]", index);
	//
	// boolean existence = client.admin().indices().prepareExists(index)
	// .execute().actionGet().exists();
	// if (!existence) {
	// boolean acknw = client
	// .admin()
	// .indices()
	// .create(createIndexRequest(index).settings(
	// settingsBuilder().loadFromClasspath(
	// "/config/index.json").put(
	// "index.numberOfReplicas", 0))).actionGet()
	// .getAcknowledged();
	// log.info("{}", acknw);
	// }
	// log.info("Running Cluster Health");
	// ClusterHealthResponse clusterHealth = client.admin().cluster()
	// .health(clusterHealthRequest().waitForGreenStatus())
	// .actionGet();
	// log.info("Done Cluster Health, status " + clusterHealth.getStatus());
	// Assert.assertFalse(clusterHealth.isTimedOut());
	// Assert.assertEquals(clusterHealth.getStatus(),
	// ClusterHealthStatus.GREEN);
	// }
	//
	// public void setMapping(final String index, final String documentType,
	// final String mappingJson) {
	// client.admin()
	// .indices()
	// .putMapping(
	// putMappingRequest(index).type(documentType).source(
	// mappingJson)).actionGet();
	// }

	public SearchQueryBean setBasicSearchAttr(final SearchQueryBean sqb) {
		List<String> basicKeywordList = new ArrayList<String>();
		basicKeywordList.add("prsn_lib_asset-c_caption-str-F");
		basicKeywordList.add("prsn_lib_asset-c_description-str-F");
		basicKeywordList.add("prsn_lib_asset-keywords-str-T");
		basicKeywordList.add("prsn_lib_asset-c_legacy_asset_id-str-F");
		basicKeywordList.add("prsn_lib_asset-c_viewpoint-str-T");
		basicKeywordList.add("prsn_lib_asset-c_grouping-str-T");
		basicKeywordList.add("prsn_lib_asset-c_age_and_gender-str-T");
		basicKeywordList.add("prsn_lib_asset-c_comp_and_tech-str-T");
		basicKeywordList.add("prsn_lib_asset-c_time_and_place-str-T");
		basicKeywordList.add("prsn_lib_asset-c_collection_id-str-F");
		basicKeywordList.add("prsn_lib_asset-c_asset_id-str-F");
		sqb.setBasicKeyword(basicKeywordList);
		return sqb;
	}

	public SearchQueryBean setReturnSearchAttr(final SearchQueryBean sqb) {
		List<String> returnList = new ArrayList<String>();
		returnList.add("prsn_lib_asset-r_object_id-str-F");
		returnList.add("prsn_lib_asset-c_asset_id-str-F");
		returnList.add("prsn_lib_asset-c_caption-str-F");
		returnList.add("prsn_lib_asset-c_rendition_path-str-F");
		returnList.add("prsn_lib_asset-acl_name-str-F");
		returnList.add("prsn_lib_asset-c_permissions_template_id-str-F");
		returnList.add("prsn_lib_asset-c_restrictions_text-str-F");
		returnList.add("prsn_lib_asset-c_exclusivity_end_date-date-F");
		sqb.setReturnAttr(returnList);
		return sqb;
	}

	public SearchQueryBean setGettyAttr(final SearchQueryBean sqb) {
		List<String> gettyList = new ArrayList<String>();
		gettyList.add("prsn_lib_asset-keywords-str-T");
		gettyList.add("prsn_lib_asset-c_viewpoint-str-T");
		gettyList.add("prsn_lib_asset-c_grouping-str-T");
		gettyList.add("prsn_lib_asset-c_age_and_gender-str-T");
		gettyList.add("prsn_lib_asset-c_comp_and_tech-str-T");
		gettyList.add("prsn_lib_asset-c_time_and_place-str-T");

		sqb.setGettyKeyword(gettyList);
		return sqb;
	}

	public <T> boolean checkResult(final List<T> expected, final List<T> result) {
		boolean status = false;
		if (expected.size() == result.size()) {
			status = expected.containsAll(result);
		}
		return status;
	}

	public boolean checkCount(final int expected, final int result) {
		boolean status = false;
		if (expected == result) {
			status = true;
		}
		return status;
	}

	/**
	 * Test method for
	 * {@link com.pearson.al.common.util.elasticsearch.SimpleSearchFilterUtil#getBasicSearchFilter(java.lang.String)}
	 * .
	 */
	public <T> List<T> readResult(final SearchResponse response) {

		List<T> result = new ArrayList<T>();
		for (final SearchHit hit : response.getHits()) {
			result.add((T) hit.getId());
		}
		log.trace(result.toString());
		return result;
	}

	/**
	 * Test method for
	 * {@link com.pearson.al.common.util.elasticsearch.SimpleSearchFilterUtil#getBasicSearchFilter(java.lang.String)}
	 * .
	 */
	public <T> List<T> readResult(final SearchResultBean response) {

		List<T> result = new ArrayList<T>();
		for (final ResultItemBean hit : response.getLstSearchResult()) {
			if (StringUtils.isNotBlank(hit.getAssetName())) {
				result.add((T) hit.getAssetName());
			}
		}
		log.trace(result.toString());
		return result;
	}

	public List<List<String>> sheetReader(final String sheetName)
			throws Exception {

		Sheet sheet = null;

		if (null == workBook) {
			log.error("Workbook obj. is not instantiated");
			return null;
		}
		/** Fetching the first sheet from workbook **/
		sheet = workBook.getSheet(sheetName);

		Iterator<Row> rowIt = sheet.iterator();

		List<List<String>> data = new ArrayList<List<String>>();
		List<String> list = null;
		Row row = null;

		while (rowIt.hasNext()) {

			list = new ArrayList<String>();
			row = rowIt.next();
			Iterator<Cell> cellIt = row.cellIterator();
			while (cellIt.hasNext()) {
				list.add(cellIt.next().getStringCellValue());
			}
			data.add(list);
		}
		return data;
	}

	private List<String> generateData() throws Exception {
		List<String> list = new ArrayList<String>();

		List<List<String>> data = null;
		Iterator<List<String>> dataIt = null;
		List<String> attrList = null;
		List<String> dataItem = null;
		String sheetName = "Data";

		// Assets
		data = sheetReader(sheetName);
		dataIt = data.iterator();
		dataIt.next();
		attrList = dataIt.next();
		try {
			while (dataIt.hasNext()) {

				dataItem = dataIt.next();

				if (StringUtils.isNotBlank(dataItem.get(0))) {
					list.add("\n\n{ \"index\" : { \"_index\" : \""
							+ IElasticSearchUtils.ELASTICSEARCH_INDEX
							+ "\", \"_type\" : \""
							+ IElasticSearchUtils.ES_NODE_ASSETS
							+ "\", \"_id\" : \"" + dataItem.get(0) + "\" } }");
					list.add("\n" + getXContent(attrList, dataItem).string());
				}
			}

			// Lightbox
			sheetName = "lightbox data";
			data = sheetReader(sheetName);
			dataIt = data.iterator();
			dataIt.next();
			attrList = dataIt.next();

			while (dataIt.hasNext()) {

				dataItem = dataIt.next();

				if (StringUtils.isNotBlank(dataItem.get(0))) {
					list.add("\n\n{ \"index\" : { \"_index\" : \""
							+ IElasticSearchUtils.ELASTICSEARCH_INDEX
							+ "\", \"_type\" : \""
							+ IElasticSearchUtils.ES_NODE_LIGHTBOX
							+ "\", \"_id\" : \"" + dataItem.get(1)
							+ "\", \"_parent\" : \"" + dataItem.get(0)
							+ "\" } }");
					list.add("\n" + getXContent(attrList, dataItem).string());
				}
			}

			// Usage
			sheetName = "Usage data";
			data = sheetReader(sheetName);
			dataIt = data.iterator();
			dataIt.next();
			attrList = dataIt.next();

			while (dataIt.hasNext()) {

				dataItem = dataIt.next();

				if (StringUtils.isNotBlank(dataItem.get(0))) {
					list.add("\n\n{ \"index\" : { \"_index\" : \""
							+ IElasticSearchUtils.ELASTICSEARCH_INDEX
							+ "\", \"_type\" : \""
							+ IElasticSearchUtils.ES_NODE_USAGE
							+ "\", \"_id\" : \"" + dataItem.get(1)
							+ "\", \"_parent\" : \"" + dataItem.get(0)
							+ "\" } }");
					list.add("\n" + getXContent(attrList, dataItem).string());
				}
			}
		} catch (Exception e) {
			log.error("Problem in Sheet - {}, row number - {}", sheetName,
					data.indexOf(dataItem));
		}

		return list;
	}

	private XContentBuilder getXContent(List<String> attrList,
			List<String> value) throws IOException, ParseException {
		XContentBuilder contentBuilder = jsonBuilder().startObject();

		Iterator<String> attrIt = attrList.iterator();
		Iterator<String> valueIt = value.iterator();
		while (attrIt.hasNext() && valueIt.hasNext()) {
			String temp = attrIt.next();
			if (StringUtils.isNotBlank(temp)) {
				log.trace("Attr - {}", temp);

				if (StringUtils.equalsIgnoreCase("parent", temp)) {
					valueIt.next();
				} else {
					SearchQueryBean sqb = new SearchQueryBean();
					sqb = setBasicSearchAttr(sqb);

					AdvSearchOptionTable option = AdvSearchOptionTable
							.parseCondition(sqb.getBasicKeyword(), temp);

					if (option.isRepeating()) {
						StringTokenizer st = new StringTokenizer(
								valueIt.next(), ",");
						if (st.hasMoreElements()) {
							contentBuilder
									.startArray(option.getAttributeName());
							while (st.hasMoreTokens()) {
								contentBuilder
										.startObject()
										.field(option.getAttributeName(),
												st.nextToken().trim())
										.endObject();
							}
							contentBuilder.endArray();
						}
					} else {
						String str = valueIt.next();

						if (StringUtils.equalsIgnoreCase("date",
								option.getAttributeType())) {

							contentBuilder
									.field(option.getAttributeName(),
											(StringUtils.isNotBlank(str)) ? sdfDestination
													.parse(str) : null);
						} else {
							contentBuilder.field(option.getAttributeName(),
									(StringUtils.isNotBlank(str)) ? str : null);
						}
					}

				}
			}

		}
		contentBuilder.endObject();
		log.trace(contentBuilder.string());
		return contentBuilder;
	}

	private JSONProvider prepareData() throws Exception {

		StringBuffer str = new StringBuffer();
		Iterator<String> itStr = generateData().iterator();
		while (itStr.hasNext()) {
			str.append(itStr.next());
		}
		log.trace("{}", str.toString());
		return new Layer(str.toString());
	}

}

class Layer implements JSONProvider {

	private String string;

	public Layer(final String temp) {
		string = temp;
	}

	public String toJson() {
		return string;
	}

}

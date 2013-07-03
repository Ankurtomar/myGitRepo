package org.anks2089.es_attachment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.codehaus.plexus.util.StringUtils;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@Slf4j
@RunWith(value = Parameterized.class)
public class AttachmentRequestTest {

	private static String directoryLoc = "C:\\Users\\ankur_tomar\\Music\\abc";
	private static Client client;
	private static String index = "test-attachment";

	public AttachmentRequestTest(final String param) {

	}

	@BeforeClass
	public static void beforeClass() throws IOException, Exception {
		log.info("Ingesting of location : {}", directoryLoc);

		if (!ESClientTestUtil.INSTANCE.exists) {
			ESClientTestUtil.INSTANCE.setData(client, index, directoryLoc);
		}
		log.info("All files ingested");
	}

	@Test
	public void test() throws Exception {

		Thread.sleep(3000);
		QueryBuilder query = QueryBuilders.queryString("Licensor");

		SearchRequestBuilder searchBuilder = client.prepareSearch()
				.setQuery(query).addField("_objectname")
				.addHighlightedField("file");
		SearchResponse search = searchBuilder.execute().actionGet();
		log.debug("Search response - {}", search.toString());
	}

	@Parameters
	public static Collection<Object[]> getData() throws Exception {
		client = ESClientTestUtil.INSTANCE.getESClient(index);
		List<Object[]> rfile = new ArrayList<Object[]>();
		rfile.add(new Object[] { "" });
		return rfile;
	}

	@AfterClass
	public static void afterClass() throws InterruptedException, IOException {
		log.info("To stop the ES server, write 'exit'.");
		BufferedReader breader = new BufferedReader(new InputStreamReader(
				System.in));
		while (!StringUtils.equalsIgnoreCase("exit", breader.readLine())) {
			Thread.sleep(10000);
		}
	}

}

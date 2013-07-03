package org.anks2089.es_attachment;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.io.File;
import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.io.Streams;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.node.Node;
import org.junit.Assert;

@Slf4j
public enum ESClientTestUtil {
	INSTANCE;
	public String clustername = "test";
	public boolean exists;

	/** Using a local node & client **/
	// private final Settings settings = ImmutableSettings.settingsBuilder()
	// .put("cluster.name", clustername)
	// .put("client.transport.sniff", true).build();

	/** Using a existing node & client **/
	private final Settings settings = ImmutableSettings.settingsBuilder()
			.put("client.transport.sniff", true)
			.put("cluster.name", clustername).build();

	Node node;
	private Client client;

	private void setUp(final String index) throws Exception {

		/** Using a existing node & client **/
		// client = new TransportClient(settings)
		// .addTransportAddress(new InetSocketTransportAddress(
		// "localhost", 8080));

		node = nodeBuilder().local(false).settings(settings).node();
		client = node.client();

		exists = createMyIndex(index);
		if (!exists) {
			setMapping(
					index,
					"assets",
					Streams.copyToStringFromClasspath("/attachment/schema.json"));
		}
	}

	public void stopServer() {
		client.close();
		node.close();
		node.stop();
	}

	public void setData(final Client client, final String index,
			final String path) throws IOException {
		List<File> files = (List<File>) FileUtils.listFiles(new File(path),
				null, true);
		for (File file : files) {
			final String data64 = org.elasticsearch.common.Base64
					.encodeBytes(FileUtils.readFileToByteArray(file));
			XContentBuilder json = jsonBuilder().startObject()
					.field("_objectname", file.getName())
					.field("_path", file.getAbsolutePath())
					.field("file", data64).endObject();

			IndexRequest req = Requests.indexRequest(index).type("assets")
					.source(json);

			log.info("Object name: {} id: {}", file.getName(), client
					.index(req).actionGet().getId());
		}
	}

	public Client getESClient(final String index) throws Exception {
		if (null == client) {
			setUp(index);
		}
		return client;
	}

	public boolean createMyIndex(final String index) {
		log.info("creating index [{}]", index);

		boolean existence = client.admin().indices().prepareExists(index)
				.execute().actionGet().exists();
		if (existence) {
			log.debug("[{}] Index already exist", index);
		} else {
			boolean acknw = client
					.admin()
					.indices()
					.create(Requests.createIndexRequest(index)
							.settings(
									ImmutableSettings
											.settingsBuilder()
											.loadFromClasspath(
													"/attachment/index.json")
											.put("index.numberOfReplicas", 0)))
					.actionGet().getAcknowledged();
			log.info("Created new Index: {}", acknw);
		}
		log.info("Running Cluster Health");
		ClusterHealthResponse clusterHealth = client.admin().cluster()
				.health(Requests.clusterHealthRequest().waitForGreenStatus())
				.actionGet();
		log.info("Done Cluster Health, status " + clusterHealth.getStatus());
		Assert.assertFalse(clusterHealth.isTimedOut());
		Assert.assertEquals(clusterHealth.getStatus(),
				ClusterHealthStatus.GREEN);
		return existence;
	}

	public void setMapping(final String index, final String documentType,
			final String mappingJson) {
		client.admin()
				.indices()
				.putMapping(
						Requests.putMappingRequest(index).type(documentType)
								.source(mappingJson)).actionGet();
	}

}

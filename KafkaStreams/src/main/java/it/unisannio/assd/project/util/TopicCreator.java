package it.unisannio.assd.project.util;

import java.util.Arrays;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopicCreator {

	private static final Logger log = LoggerFactory.getLogger(TopicCreator.class);

	private static String bootstrapServers = "localhost:9092";

	public static void createTopic(String topicName,int numPartitions, short replicationFactor) {
		try (AdminClient client = AdminClient.create(setConfig())) {
			if (!topicExists(topicName)) {
				log.info("Creating topic {}", topicName);
				CreateTopicsResult result = client.createTopics(Arrays.asList(
						new NewTopic(topicName, numPartitions, replicationFactor)
						));
				try {
					result.all().get();
				} catch ( InterruptedException | ExecutionException e ) {
					throw new IllegalStateException(e);
				}
			}
		}
	}

	public static void setBootstrapServers(String bootstrapServers) {
		TopicCreator.bootstrapServers = bootstrapServers;
	}

	private static Properties setConfig() {
		Properties properties = new Properties();
		properties.put("bootstrap.servers", bootstrapServers);
		properties.put("connections.max.idle.ms", 10000);
		properties.put("request.timeout.ms", 5000);
		return properties;
	}

	private static boolean topicExists(String topicName) {
		try (AdminClient client = AdminClient.create(setConfig())) {
			ListTopicsResult topics = client.listTopics();
			try {
				Set<String> currentTopicList = topics.names().get();
				return currentTopicList.contains(topicName);
			} catch (InterruptedException | ExecutionException e) {
				throw new IllegalStateException(e);
			}
		}
	}

}

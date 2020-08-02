package it.unisannio.artemisourceconnector;

import it.unisannio.util.ArtemisProvider;
import it.unisannio.util.Message;
import org.apache.activemq.artemis.api.core.ActiveMQException;
import org.apache.activemq.artemis.api.core.client.ClientConsumer;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;

abstract class Dequeuer implements Comparable<Dequeuer> {

    private final Logger log = LoggerFactory.getLogger(Dequeuer.class);

    protected final ArtemisProvider provider;
    protected final String topicPrefix;
    protected final String queueName;
    protected SchemaMapping schemaMapping;

    // Mutable state

    protected long lastUpdate;
    protected ClientSession clientSession;
    protected ClientConsumer consumer;
    protected Queue<Message> messages;
    private String loggedQueueName;

    public Dequeuer(ArtemisProvider provider, String queueName, String topicPrefix) {
        this.provider = provider;
        this.queueName = queueName;
        this.topicPrefix = topicPrefix;
        this.lastUpdate = 0;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public ClientConsumer getOrCreateConsumer(ClientSession clientSession) throws Exception {
        if (consumer != null)
            return consumer;
        createConsumer(clientSession);
        return consumer;
    }

    protected abstract void createConsumer(ClientSession clientSession) throws Exception;

    public boolean querying() {
        return messages != null;
    }

    public void maybeStartQuery(ClientSession clientSession) throws Exception {
    	log.info("Check maybe query!!!!!!!!!!!!!!!!!!!!!!!!!");
        if (messages == null) {
        	log.info("Check if maybe query!!!!!!!!!!!!!!!!!!!!!!!!!");
            this.clientSession = clientSession;
            consumer = getOrCreateConsumer(clientSession);
            messages = dequeue();
            schemaMapping = SchemaMapping.create(queueName);
        }
    }

    protected abstract Queue<Message> dequeue() throws Exception;

    public boolean next() throws Exception {
        return !messages.isEmpty();
    }

    public abstract SourceRecord extractRecord() throws Exception;

    public void reset(long now) {
        resetArrayListQuietly();
        closeConsumerQuietly();
        releaseLocksQuietly();
        // TODO: Can we cache this and quickly check that it's identical for the next query
        // instead of constructing from scratch since it's almost always the same
        schemaMapping = null;
        lastUpdate = now;
    }

    private void releaseLocksQuietly() {
        if (clientSession != null) {
            try {
                clientSession.close();
            } catch (ActiveMQException e) {
                log.warn("Error while closing session, session locks may still be held", e);
            }
        }
        clientSession = null;
    }

    private void closeConsumerQuietly() {
        if (consumer != null) {
            try {
                consumer.close();
            } catch (Exception ignored) {
                // intentionally ignored
            }
        }
        consumer = null;
    }

    private void resetArrayListQuietly() {
        if (messages != null) {
            try {
                messages = null;
            } catch (Exception ignored) {
                // intentionally ignored
            }
        }
    }

    protected void recordQuery(String queue) {
        if (queue != null && !queue.equals(loggedQueueName)) {
            // For usability, log the statement at INFO level only when it changes
            log.info("Begin using Artemis consumer on queue: {}", queueName);
            loggedQueueName = queueName;
        }
    }

    @Override
    public int compareTo(Dequeuer other) {
        if (this.lastUpdate < other.lastUpdate) {
            return -1;
        } else if (this.lastUpdate > other.lastUpdate) {
            return 1;
        } else {
            return this.queueName.compareTo(other.queueName);
        }
    }
}
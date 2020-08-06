package it.unisannio.artemisourceconnector;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.api.core.client.ClientMessage;
import org.apache.activemq.artemis.api.core.client.ClientRequestor;
import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.activemq.artemis.api.core.management.ManagementHelper;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.netty.buffer.ByteBuf;
import it.unisannio.util.ArtemisProvider;
import it.unisannio.util.ExplicitCrowdSensingMessage;
import it.unisannio.util.ImplicitCrowdSensingMessage;
import it.unisannio.util.Message;
import it.unisannio.util.TopicBuilder;

public class BulkDequeuer extends Dequeuer {
    private static final Logger log = LoggerFactory.getLogger(BulkDequeuer.class);
    private int maxMsgs;

    public BulkDequeuer(ArtemisProvider provider, String queueName, String topicPrefix, int maxMsgs) {
        super(provider, queueName, topicPrefix);
        this.maxMsgs = maxMsgs;
    }

    @Override
    protected void createConsumer(ClientSession clientSession) throws Exception {
        recordQuery(queueName);
        log.info("{} created consumer on {}", this, queueName);
        consumer = clientSession.createConsumer(new SimpleString(queueName));
    }

    @Override
    protected Queue<Message> dequeue() throws Exception {
        Queue<Message> messages = new LinkedList<>();
        ClientRequestor requester = new ClientRequestor(clientSession, "activemq.management");
        ClientMessage message = clientSession.createMessage(false);
        String resourceName = "queue." + queueName;
        log.info("Resource Name {}", resourceName);
        ManagementHelper.putAttribute(message, resourceName, "messageCount");
        ClientMessage reply = requester.request(message);
        long countL = (long) ManagementHelper.getResult(reply);
        log.info("COUNT {}", countL);
        int count = (int) countL;
        if (count > maxMsgs)
            count = maxMsgs;
        requester.close();
        for (int i = 0; i < count; i++) {
            ClientMessage cm = consumer.receive();
            ByteBuf buffer = cm.getBodyBuffer().byteBuf();
            char[] chars = new char[buffer.readableBytes()];
            int a = 0;
            while (buffer.isReadable())
            	chars[a++] = (char) buffer.readByte();
            String content = new String(chars);
            log.info("################################### " + content + " ###################################");
            Message mex = null;
            ObjectMapper objectMapper = new ObjectMapper();
            if (queueName.contains("ics"))
            	mex = objectMapper.readValue(content, ImplicitCrowdSensingMessage.class);
            else if (queueName.contains("ecs"))
            	mex = objectMapper.readValue(content, ExplicitCrowdSensingMessage.class);
            
            messages.add(mex);
            cm.acknowledge();
        }
        clientSession.commit();
        return messages;
    }

    @Override
    public SourceRecord extractRecord() throws Exception {
        Message m = messages.poll();
        Struct record = new Struct(schemaMapping.schema());
        for (SchemaMapping.FieldSetter setter : schemaMapping.fieldSetters()) {
            try {
                setter.setField(record, m);
            } catch (IOException e) {
                log.warn("Error mapping fields into Connect record", e);
                throw new ConnectException(e);
            } catch (Exception e) {
                log.warn("Artemis error mapping fields into Connect record", e);
                throw new DataException(e);
            }
        }
        // TODO: key from primary key? partition?
        final String topic;
        final Map<String, String> partition;
        String name = queueName; // backwards compatible
        partition = Collections.singletonMap(ArtemisConnectorConstants.QUEUE_NAME_KEY, name);
        if (topicPrefix.isEmpty())
            topic = TopicBuilder.validateTopicName(name);
        else
            topic = TopicBuilder.validateTopicName(topicPrefix + name);
        return new SourceRecord(partition, null, topic, record.schema(), record);
    }

    @Override
    public String toString() {
        return "BulkQueueQuerier{" + "queue='" + queueName + '\'' + ", topicPrefix='" + topicPrefix + '\'' + '}';
    }
}

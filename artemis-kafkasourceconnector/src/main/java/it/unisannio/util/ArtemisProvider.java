package it.unisannio.util;

import it.unisannio.artemisourceconnector.ArtemisConnectorConfig;
import org.apache.activemq.artemis.api.core.client.*;
import org.apache.activemq.artemis.api.core.management.ManagementHelper;
import org.apache.activemq.artemis.api.core.management.ResourceNames;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.types.Password;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ArtemisProvider implements ClientSessionProvider {
    private static final String ACTIVEMQMANAGEMENT = "activemq.management.";
    private static final String ACTIVENOTIFICATIONS = "activemq.notifications";
    private static final String DLQ = "DLQ";

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final AbstractConfig config;
    private final Queue<ClientSession> clientsessions = new ConcurrentLinkedQueue<>();
    protected final String artemisUrl;
    private ServerLocator locator;
    private ClientSessionFactory factory;

    public ArtemisProvider(AbstractConfig config) throws Exception {
        this.config = config;
        this.artemisUrl = config.getString(ArtemisConnectorConfig.CONNECTION_URL_CONFIG);
        this.locator = ActiveMQClient.createServerLocator(artemisUrl);
        this.factory = locator.createSessionFactory();
    }

    @Override
    public ClientSession getClientSession() throws Exception {
        String username = config.getString(ArtemisConnectorConfig.CONNECTION_USER_CONFIG);
        Password artemisPassword = config.getPassword(ArtemisConnectorConfig.CONNECTION_PASSWORD_CONFIG);
        Properties properties = new Properties();
        if (username != null)
            properties.setProperty("user", username);
        if (artemisPassword != null)
            properties.setProperty("password", artemisPassword.value());
        ClientSession session = this.factory.createSession(true,false);
        session.start();
        clientsessions.add(session);
        log.info("Session has been added");
        return session;
    }

    @Override
    public void close() {
        ClientSession clientSession;
        while ((clientSession = clientsessions.poll()) != null) {
            try {
                clientSession.close();
            } catch (Throwable e) {
                log.warn("Error while closing connection to {}", artemisUrl, e);
            }
        }
    }

    @Override
    public String identifier() {
        return artemisUrl;
    }

    public List<String> queueIds(ClientSession clientSession) throws Exception {
        log.info("Requesting queues names...");
        ClientRequestor requester = new ClientRequestor(clientSession, "activemq.management");
        ClientMessage message = clientSession.createMessage(false);
        ManagementHelper.putAttribute(message, ResourceNames.BROKER, "queueNames"); // "topicNames" for topics
        ClientMessage reply = requester.request(message);
        Object[] queueNames = (Object[]) ManagementHelper.getResult(reply);
        List<String> queueIds = new ArrayList<>();
        for (Object queueName: queueNames)
            if (includeQueue((String) queueName))
                queueIds.add((String) queueName);
        requester.close();
        return queueIds;
    }

    protected boolean includeQueue(String queueName) {
        if(queueName.startsWith(ACTIVEMQMANAGEMENT))
            return false;
        else if(queueName.equals(DLQ))
            return false;
        else if(queueName.startsWith(ACTIVENOTIFICATIONS))
            return false;
        else
            return true;
    }

    public ExpressionBuilder expressionBuilder() {
        return new ExpressionBuilder();
    }
}

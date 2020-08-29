package it.unisannio.util;

import org.apache.activemq.artemis.api.core.client.ClientSession;
import org.apache.kafka.connect.errors.ConnectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachedClientSessionProvider implements ClientSessionProvider {

	private static final Logger log = LoggerFactory.getLogger(CachedClientSessionProvider.class);

	private final ClientSessionProvider provider;
	private final int maxConnectionAttempts;
	private final long connectionRetryBackoff;

	private int count = 0;
	private ClientSession clientsession;

	public CachedClientSessionProvider(ClientSessionProvider provider, int maxConnectionAttempts, long connectionRetryBackoff) {
		this.provider = provider;
		this.maxConnectionAttempts = maxConnectionAttempts;
		this.connectionRetryBackoff = connectionRetryBackoff;
	}

	@Override
	public synchronized ClientSession getClientSession() {
		try {
			if (clientsession == null)
				newClientSession();
		} catch (Exception e) {
			throw new ConnectException(e);
		}
		return clientsession;
	}

	private void newClientSession() throws Exception {
		int attempts = 0;
		while (attempts < maxConnectionAttempts) {
			try {
				++count;
				log.info("Attempting to open connection #{} to {}", count, provider);
				clientsession = provider.getClientSession();
				onConnect(clientsession);
				return;
			} catch (Exception e) {
				attempts++;
				if (attempts < maxConnectionAttempts) {
					log.info("Unable to connect to database on attempt {}/{}. Will retry in {} ms.", attempts,
							maxConnectionAttempts, connectionRetryBackoff, e);
					try {
						Thread.sleep(connectionRetryBackoff);
					} catch (InterruptedException ie) {
						//this is ok because just woke up early
					}
				} else {
					throw e;
				}
			}
		}
	}

	@Override
	public synchronized void close() {
		if (clientsession != null) {
			try {
				log.info("Closing client session #{} to {}", count, provider);
				clientsession.close();
			} catch (Exception e) {
				log.warn("Ignoring error closing client sessions", e);
			} finally {
				clientsession = null;
				provider.close();
			}
		}
	}
	
	@Override
	public String identifier() {
		return provider.identifier();
	}

	protected void onConnect(ClientSession clientsession) throws Exception { }

}
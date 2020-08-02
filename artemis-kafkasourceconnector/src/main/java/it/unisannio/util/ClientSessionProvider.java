package it.unisannio.util;

import org.apache.activemq.artemis.api.core.client.ClientSession;

/**
 * A provider of Artemis {@link ClientSession} instances.
 */
public interface ClientSessionProvider extends AutoCloseable {

	/**
   	* Create a connection.
   	* @return the clientsession; never null
   	* @throws Exception if there is a problem getting the connection
   	*/
	ClientSession getClientSession() throws Exception;

  	/**
   	* Determine if the specified connection is valid.
   	*
   	* @param clientsession the artemis clientsession; may not be null
   	* @param timeout    The time in seconds to wait for the database operation used to validate
   	*                   the connection to complete.  If the timeout period expires before the
   	*                   operation completes, this method returns false.  A value of 0 indicates a
   	*                   timeout is not applied to the database operation.
   	* @return true if it is valid, or false otherwise
   	* @throws Exception if there is an error with the artemis connection
   	*/
  	//boolean isClientSessionValid(ClientSession clientsession,int timeout) throws Exception;

  	/**
  	 * Close this clientsession provider.
  	 */
  	void close();

  	/**
  	 * Get the publicly viewable identifier for this connection provider and / or the database.
  	 *The resulting value should not contain any secrets or passwords.
  	 *
  	 * @return the identifier; never null
  	 */
  	default String identifier() {return toString();}
}
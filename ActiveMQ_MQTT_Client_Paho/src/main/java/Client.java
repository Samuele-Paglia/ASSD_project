
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class Client {

    public static void main(String[] args) throws Exception {
    	
    	final String brokerURI   = "tcp://172.18.10.145:30040";
    	
    	MqttClient client = new MqttClient( 
    			brokerURI, // URI 
//    			"samu123",
    			MqttClient.generateClientId(), // ClientId 
    		    new MemoryPersistence()); // Persistence
    	
    	MqttConnectOptions options = new MqttConnectOptions();
    	options.setCleanSession(false);
    	options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
    	client.connect(options);
    	
    	if (client.isConnected())
    		System.out.println("Client connected!");
    	
    	String topic = "kafkaTestMongodb";
    	String content = "Si va a Berlino Beppe! Si va a Berlino Beppe! Si va a Berlino Beppe!";
    	int qos = 1;
//    	client.subscribe(topic);
//    	class MqttPostPropertyMessageListener implements IMqttMessageListener {
//            public void messageArrived(String var1, MqttMessage var2) throws Exception {
//                System.out.println("reply topic  : " + var1);
//                System.out.println("reply payload: " + var2.toString());
//            }
//        }
//    	
//    	client.subscribe(topic, new MqttPostPropertyMessageListener());
    	
    	int i = 0;
    	while (i<1) {
    		client.publish( 
    				topic, // topic 
    				content.getBytes(), // payload 
    				qos, // QoS 
    				false); // retained?
    		i++;
    	}

    	client.disconnect();
    	while(client.isConnected());
//    	client.close();
        



    }
    
    
}

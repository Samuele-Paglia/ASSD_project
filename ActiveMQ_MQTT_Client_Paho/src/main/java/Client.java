
import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class Client {

    public static void main(String[] args) throws Exception {
    	
//    	final String brokerURI   = "tcp://172.18.10.145:30040";
    	final String brokerURI   = "tcp://127.0.0.1:61616";
    	
    	
    	MqttClient client = new MqttClient( 
    			brokerURI, // URI 
    			"mike",
//    			MqttClient.generateClientId(), // ClientId 
    		    new MemoryPersistence()); // Persistence
    	
    	MqttConnectOptions options = new MqttConnectOptions();
    	options.setCleanSession(false);
    	options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
    	client.connect(options);
    	
    	if (client.isConnected())
    		System.out.println("Client connected! ");
    	
//    	String topic = "ecs-topic";
    	long date = new Date().getTime();
    	
//    	String content = "{\"id\": \"ADNd345DfdDAF34AD347hHgNd345DfdDAFT3\", \"type\": \"device\", \"long\" : \"-73.85607\", \"lat\": \"41.848447\", \"timestamp\" : \"" + date + "\" }";
    	String topic = "ics-topic";
    	String content = "{\"uuidReceiver\": \"1b1ace19-805c-bce1-9d8a-78cbbd1dcbcb\", \"uuidSender\": \"4edb83dd-1ecc-6472-cf6e-21ed82bade69\", \"rssi\": -79, \"txPower\": -59, \"timestamp\" : \"" + date + "\" }";
    	
    	
    	int qos = 1;
    	client.subscribe(topic);
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

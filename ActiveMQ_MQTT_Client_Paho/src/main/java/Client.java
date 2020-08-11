
import java.util.Date;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class Client {

    public static void main(String[] args) throws Exception {
    	
    	final String brokerURI   = "tcp://172.18.10.145:30040";
//    	final String brokerURI   = "tcp://127.0.0.1:61616";
    	
    	
    	MqttClient client = new MqttClient( 
    			brokerURI, // URI 
    			"samu",
//    			MqttClient.generateClientId(), // ClientId 
    		    new MemoryPersistence()); // Persistence
    	
    	MqttConnectOptions options = new MqttConnectOptions();
    	options.setCleanSession(false);
    	options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
    	client.connect(options);
    	
    	if (client.isConnected())
    		System.out.println("Client connected! ");
    	
    	long date = new Date().getTime();
    	
//    	String topic = "ics-topic";
//    	String content = "{\"uuidReceiver\": \"1b1ace19-805c-bce1-9d8b-78cbbd1dcbca\", \"uuidSender\": \"4edb83dd-1ecc-6473-cf6e-21fd82bade67\", \"rssi\": -79, \"txPower\": -59, \"timestamp\" : \"" + date + "\" }";
//    	String topic = "ecs-topic";   	
//    	String content = "{\"id\": \"ADNd345DfdDAF34AD347hHgNd345DfdDAFT3\", \"type\": \"device\", \"long\" : \"-73.85607\", \"lat\": \"41.848447\", \"timestamp\" : \"" + date + "\" }";
    	String topic = "tcn-topic";
    	String content = "fd8deb9d91a13e144ca5b0ce14e289532e040fe0bf922c6e3dadb1e4e2333c"
				+ "78df535b90ac99bec8be3a8add45ce77897b1e7cb1906b5cff1097d3cb142f"
				+ "d9d002000a00000c73796d70746f6d206461746131078ec5367b67a8c793b7"
				+ "40626d81ba904789363137b5a313419c0f50b180d8226ecc984bf073ff89cb"
				+ "d9c88fea06bda1f0f368b0e7e88bbe68f15574482904";
    	
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

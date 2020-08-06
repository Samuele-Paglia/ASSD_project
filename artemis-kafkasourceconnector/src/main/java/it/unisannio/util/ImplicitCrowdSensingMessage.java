package it.unisannio.util;

public class ImplicitCrowdSensingMessage implements Message {
	
	private String uuidReceiver;
	private String uuidSender;
	private int rssi;
	private int txPower;
	private long timestamp;
	
	public ImplicitCrowdSensingMessage() { }
	
	public ImplicitCrowdSensingMessage(String uuidReceiver, String uuidSender, int rssi, int txPower, long timestamp) {
		this.uuidReceiver = uuidReceiver;
		this.uuidSender = uuidSender;
		this.rssi = rssi;
		this.txPower = txPower;
		this.timestamp = timestamp;
	}

	public String getUuidReceiver() {
		return uuidReceiver;
	}

	public String getUuidSender() {
		return uuidSender;
	}

	public int getRssi() {
		return rssi;
	}

	public int getTxPower() {
		return txPower;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "ImplicitCrowdSensingMessage [uuidReceiver=" + uuidReceiver + ", uuidSender=" + uuidSender + ", rssi="
				+ rssi + ", txPower=" + txPower + ", timestamp=" + timestamp + "]";
	}
	
}

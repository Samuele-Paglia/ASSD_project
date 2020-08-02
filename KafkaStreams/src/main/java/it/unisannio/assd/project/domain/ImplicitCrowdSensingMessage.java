package it.unisannio.assd.project.domain;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("myFilter")
public class ImplicitCrowdSensingMessage {
	
	private String uuidReceiver;
	private String uuidSender;
	private int rssi;
	private int txPower;
	private float proximityIndex;
	private long timestamp;
	
	public ImplicitCrowdSensingMessage() { }
	
	public ImplicitCrowdSensingMessage(String uuidReceiver, String uuidSender, int rssi, int txPower, long timestamp) {
		super();
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
	
	public void calculateProximityIndex() {
		this.proximityIndex = this.rssi * this.txPower;
	}

	public float getProximityIndex() {
		return proximityIndex;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "ImplicitCrowdSensingMessage [uuidReceiver=" + uuidReceiver + ", uuidSender=" + uuidSender + ", rssi="
				+ rssi + ", txPower=" + txPower + ", proximityIndex=" + proximityIndex + ", timestamp=" + timestamp
				+ "]";
	}


	
	
}

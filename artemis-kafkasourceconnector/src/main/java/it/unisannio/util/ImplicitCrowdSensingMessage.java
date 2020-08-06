package it.unisannio.util;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class ImplicitCrowdSensingMessage implements Message {
	
	private String uuidReceiver;
	private String uuidSender;
	private int rssi;
	private int txPower;
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date timestamp;
	
	public ImplicitCrowdSensingMessage() { }
	
	public ImplicitCrowdSensingMessage(String uuidReceiver, String uuidSender, int rssi, int txPower, Date timestamp) {
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

	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "ImplicitCrowdSensingMessage [uuidReceiver=" + uuidReceiver + ", uuidSender=" + uuidSender + ", rssi="
				+ rssi + ", txPower=" + txPower + ", timestamp=" + timestamp + "]";
	}
	
}

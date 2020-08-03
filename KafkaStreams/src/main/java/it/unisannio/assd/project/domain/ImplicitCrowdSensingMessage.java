package it.unisannio.assd.project.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.unisannio.assd.project.util.CustomDateSerializer;



@JsonFilter("parametersFilter")
public class ImplicitCrowdSensingMessage {
	
	private String uuidReceiver;
	private String uuidSender;
	private int rssi;
	private int txPower;
	private float proximityIndex;
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date timestamp;
	
	public ImplicitCrowdSensingMessage() { }
	
	public ImplicitCrowdSensingMessage(String uuidReceiver, String uuidSender, int rssi, int txPower, Date timestamp) {
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
//	TODO: Maybe to delete. Move the proximity index calculus in the stream process
	public void calculateProximityIndex() {
		this.proximityIndex = this.rssi * this.txPower;
	}

	public float getProximityIndex() {
		return proximityIndex;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "ImplicitCrowdSensingMessage [uuidReceiver=" + uuidReceiver + ", uuidSender=" + uuidSender + ", rssi="
				+ rssi + ", txPower=" + txPower + ", proximityIndex=" + proximityIndex + ", timestamp=" + timestamp
				+ "]";
	}	
	
}

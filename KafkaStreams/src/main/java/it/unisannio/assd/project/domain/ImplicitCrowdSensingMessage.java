package it.unisannio.assd.project.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("parametersFilter")
public class ImplicitCrowdSensingMessage implements Message {
	
	private String uuidReceiver;
	private String uuidSender;
	private double proximityIndex;
//	@JsonSerialize(using = CustomDateSerializer.class)
	private Date timestamp;
	
	public ImplicitCrowdSensingMessage() { }

	public ImplicitCrowdSensingMessage(String uuidReceiver, String uuidSender, double proximityIndex, Date timestamp) {
		super();
		this.uuidReceiver = uuidReceiver;
		this.uuidSender = uuidSender;
		this.proximityIndex = proximityIndex;
		this.timestamp = timestamp;
	}

	public String getUuidReceiver() {
		return uuidReceiver;
	}

	public String getUuidSender() {
		return uuidSender;
	}

	public double getProximityIndex() {
		return proximityIndex;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "ImplicitCrowdSensingMessage [uuidReceiver=" + uuidReceiver + ", uuidSender=" + uuidSender
				+ ", proximityIndex=" + proximityIndex + ", timestamp=" + timestamp + "]";
	}
	
	
	

	
}

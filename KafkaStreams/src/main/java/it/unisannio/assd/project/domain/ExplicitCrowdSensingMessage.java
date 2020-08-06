package it.unisannio.assd.project.domain;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.unisannio.assd.project.util.CustomDateSerializer;

@JsonFilter("parametersFilterECS")
public class ExplicitCrowdSensingMessage implements Message {
	
	private String id;
	private String type;
	private double longitude;
	private double latitude;
	@JsonSerialize(using = CustomDateSerializer.class)
	private long timestamp;
	
	public ExplicitCrowdSensingMessage() { }

	public ExplicitCrowdSensingMessage(String id, String type, double longitude, double latitude, long timestamp) {
		this.id = id;
		this.type = type;
		this.longitude = longitude;
		this.latitude = latitude;
		this.timestamp = timestamp;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "ExplicitCrowdSensingMessage [id=" + id + ", type=" + type + ", longitude=" + longitude + ", latitude="
				+ latitude + ", timestamp=" + timestamp + "]";
	}
	
}

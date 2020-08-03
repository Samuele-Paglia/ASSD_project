package it.unisannio.assd.project.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFilter;

@JsonFilter("parametersFilterECS")
public class ExplicitCrowdSensingMessage {
	
	private String id;
	@JsonAlias("long")
	private double longitude;
	@JsonAlias("lat")
	private double latitude;
	private Date timestamp;
	
	public ExplicitCrowdSensingMessage() { }

	public ExplicitCrowdSensingMessage(String id, double longitude, double latitude, Date timestamp) {
		this.id = id;
		this.longitude = longitude;
		this.latitude = latitude;
		this.timestamp = timestamp;
	}

	public String getId() {
		return id;
	}

	public double getLongitude() {
		return longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "ExplicitCrowdSensingMessage [id=" + id + ", longitude=" + longitude + ", latitude=" + latitude
				+ ", timestamp=" + timestamp + "]";
	}
	
}

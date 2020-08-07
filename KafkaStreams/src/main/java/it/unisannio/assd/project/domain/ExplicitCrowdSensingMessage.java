package it.unisannio.assd.project.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.unisannio.assd.project.util.CustomDateSerializer;

@JsonAppend(
		attrs = @JsonAppend.Attr(value = "location")
		)
@JsonFilter("parametersFilterECS")
public class ExplicitCrowdSensingMessage implements Message {
	
	private String id;
	private String type;
	private double longitude;
	private double latitude;
	@JsonSerialize(using = CustomDateSerializer.class)
	private Date timestamp;
	
	public ExplicitCrowdSensingMessage() { }

	public ExplicitCrowdSensingMessage(String id, String type, double longitude, double latitude, Date timestamp) {
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

	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		return "ExplicitCrowdSensingMessage [id=" + id + ", type=" + type + ", longitude=" + longitude + ", latitude="
				+ latitude + ", timestamp=" + timestamp + "]";
	}
	
}

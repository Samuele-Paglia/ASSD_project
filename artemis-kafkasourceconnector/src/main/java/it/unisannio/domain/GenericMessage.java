package it.unisannio.domain;

public class GenericMessage implements Message {
	
	private String payload;

	public GenericMessage(String payload) {
		this.payload = payload;
	}

	public String getPayload() {
		return payload;
	}

}

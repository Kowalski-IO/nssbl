package io.kowalski.nssc.models;

import java.io.Serializable;

public class SlackEvent implements Serializable {

	private static final long serialVersionUID = -1905457317422979510L;
	
	private String botID;
	private SlackEventType eventType;
	
	public final String getQueueName() {
		return botID + "_" + eventType.name();
	}

	public final String getBotID() {
		return botID;
	}

	public final void setBotID(final String botID) {
		this.botID = botID;
	}

	public final SlackEventType getEventType() {
		return eventType;
	}

	public final void setEventType(final SlackEventType eventType) {
		this.eventType = eventType;
	}
}

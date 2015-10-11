package io.kowalski.nssbl.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SlackEvent implements Serializable {

	private static final long serialVersionUID = -1905457317422979510L;

	private String botID;
	private SlackEventType eventType;

	@JsonProperty("chanel")
	private String channelID;

	@JsonProperty("team")
	private String teamID;

	@JsonProperty("ts")
	private String timestamp;

	@JsonProperty("user")
	private String userID;
	
	@JsonProperty("type")
	private String rawType;
	
	@JsonProperty("text")
	private String body;
	
	@Override
	public final String toString() {
		return userID + ": " + body;
	}

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

	public final String getChannelID() {
		return channelID;
	}

	public final void setChannelID(final String channelID) {
		this.channelID = channelID;
	}

	public final String getTeamID() {
		return teamID;
	}

	public final void setTeamID(final String teamID) {
		this.teamID = teamID;
	}

	public final String getTimestamp() {
		return timestamp;
	}

	public final void setTimestamp(final String timestamp) {
		this.timestamp = timestamp;
	}

	public final String getUserID() {
		return userID;
	}

	public final void setUserID(final String userID) {
		this.userID = userID;
	}

	public final String getRawType() {
		return rawType;
	}

	public final void setRawType(final String rawType) {
		this.rawType = rawType;
	}

	public final String getBody() {
		return body;
	}

	public final void setBody(final String body) {
		this.body = body;
	}
}

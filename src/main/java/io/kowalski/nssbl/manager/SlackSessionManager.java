package io.kowalski.nssbl.manager;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import io.kowalski.nssbl.models.SlackSession;

public class SlackSessionManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(SlackSessionManager.class);
	private static final OkHttpClient client = new OkHttpClient();
	private static final ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
	}

	private final String realTimeMessagingURL = "https://slack.com/api/rtm.start?token=";
	private final String realTimeMessagingToken;

	private SlackSession slackSession;

	@Inject
	public SlackSessionManager(@Named("slackToken") final String realTimeMessagingToken) {
		this.realTimeMessagingToken = realTimeMessagingToken;
	}

	public final Optional<SlackSession> retreiveSession() {
		if (slackSession == null) {
			createSession();
		}
		return Optional.ofNullable(slackSession);
	}
	
	public final void refreshSession() {
		createSession();
	}

	private void createSession() {
		Request request = new Request.Builder().url(realTimeMessagingURL + realTimeMessagingToken).build();
		try {
			Response response = client.newCall(request).execute();
			slackSession = mapper.readValue(response.body().string(), SlackSession.class);
		} catch (IOException e) {
			LOGGER.error("Unable to instantiate Slack Session", e);
		}
	}
}

package io.kowalski.nssbl.manager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import io.kowalski.nssbl.models.SlackEvent;
import io.kowalski.nssbl.models.SlackEventType;

public class WebSocketManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketManager.class);

	private static final ObjectMapper mapper;

	static {
		mapper = new ObjectMapper();
		mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
	}

	private final ClientEndpointConfig configuration = ClientEndpointConfig.Builder.create().build();
	private final Set<SlackEventType> ingoredSlackEventTypes;
	private final SlackSessionManager sessionManager;
	private final String botID;

	private Session websocketSession;
	private BlockingQueue<SlackEvent> queue;

	@Inject
	public WebSocketManager(final SlackSessionManager sessionManager, @Named("botID") final String botID,
			final Set<SlackEventType> ingoredSlackEventTypes) {
		this.sessionManager = sessionManager;
		this.botID = botID;
		this.ingoredSlackEventTypes = ingoredSlackEventTypes;
	}

	public final Optional<Session> retreiveWebSocketSession() {
		if (!sessionManager.retreiveSession().isPresent()) {
			throw new IllegalStateException("Slack session could not be instatiated. Aborting...");
		}
		String websocketURL = sessionManager.retreiveSession().get().getUrl();
		ClientManager client = ClientManager.createClient();

		if (websocketSession == null) {
			try {
				client.connectToServer(new Endpoint() {
					@Override
					public void onOpen(Session session, EndpointConfig config) {
						websocketSession = session;
						websocketSession.addMessageHandler(new MessageHandler.Whole<String>() {
							@Override
							public void onMessage(final String message) {
								Optional<SlackEvent> slackEvent = buildSlackEvent(message);
								if (slackEvent.isPresent()) {
									if (slackEvent.get().getEventType().equals(SlackEventType.HELLO)) {
										LOGGER.info("Successfully connected to Slacker RTM.");
									} else if (!ingoredSlackEventTypes.contains(slackEvent.get().getEventType())) {
										queue.add(slackEvent.get());
									}
								}
							}
						});
					}
				}, configuration, new URI(websocketURL));
			} catch (DeploymentException | IOException | URISyntaxException e) {
				LOGGER.error(e.toString());
			}
		}

		return Optional.ofNullable(websocketSession);
	}

	private Optional<SlackEvent> buildSlackEvent(final String message) {
		SlackEvent slackEvent = null;
		try {
			slackEvent = mapper.readValue(message, SlackEvent.class);
			slackEvent.setBotID(botID);
			slackEvent.setEventType(SlackEventType.stringToEnum(slackEvent.getRawType()));
		} catch (IOException e) {
			LOGGER.error("Unable to parse slack event.", e);
		}

		return Optional.ofNullable(slackEvent);
	}

	public final SlackSessionManager getSessionManager() {
		return sessionManager;
	}

	public final void setQueue(final BlockingQueue<SlackEvent> queue) {
		this.queue = queue;
	}
}

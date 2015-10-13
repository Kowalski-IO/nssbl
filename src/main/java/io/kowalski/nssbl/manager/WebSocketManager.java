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
	
	private final long timeoutLimit = 3000L;
	
	private final ObjectMapper mapper;
	private final ClientEndpointConfig configuration;
	
	private final SlackSessionManager sessionManager;

	private final BlockingQueue<SlackEvent> slackEventQueue;
	private final Set<SlackEventType> ingoredSlackEventTypes;
	
	private final boolean autoReconnect;
	
	private final String botID;
	
	private Thread keepAliveThread;
	
	private volatile Session websocketSession;
	private volatile boolean shutdownRequested = false;
	private volatile long lastPing = -1;
	private volatile long lastPingAck = -1;
	private volatile long messageID = 0;
	
	@Inject
	public WebSocketManager(final SlackSessionManager sessionManager, @Named("botID") final String botID,
			final Set<SlackEventType> ingoredSlackEventTypes, final BlockingQueue<SlackEvent> slackEventQueue,
			@Named("autoReconnect") final boolean autoReconnect) {
		this.sessionManager = sessionManager;
		this.botID = botID;
		this.ingoredSlackEventTypes = ingoredSlackEventTypes;
		this.slackEventQueue = slackEventQueue;
		this.autoReconnect = autoReconnect;
		
		// Bootstrap util objects
		this.configuration = ClientEndpointConfig.Builder.create().build();
		this.mapper = new ObjectMapper();
		this.mapper.setPropertyNamingStrategy(PropertyNamingStrategy.CAMEL_CASE_TO_LOWER_CASE_WITH_UNDERSCORES);
	}
	
	public final void connect() throws IllegalStateException {
		this.retreiveWebSocketSession();
		this.dispatchKeepAliveThread();
	}
	
	public final void disconnect() {
		try {
			this.keepAliveThread.interrupt();
			this.websocketSession.close();
		} catch (IOException e) {
			LOGGER.error("Unable to close websocket.");
		} finally {
			websocketSession = null;
		}
	}

	public void retreiveWebSocketSession() throws IllegalStateException {
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
									if (slackEvent.get().getEventType().equals(SlackEventType.PONG)) {
										LOGGER.info("Pong received: " + slackEvent.get().getReplyTo());
										lastPingAck = System.currentTimeMillis();
									}
									else if (slackEvent.get().getEventType().equals(SlackEventType.HELLO)) {
										LOGGER.info("Successfully connected to Slacker RTM.");
									} else if (!ingoredSlackEventTypes.contains(slackEvent.get().getEventType())) {
										slackEventQueue.add(slackEvent.get());
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
		if (websocketSession == null) {
			LOGGER.error("Unable to connect to Slack RTM. Aborting...");
			throw new IllegalStateException("Unable to connect to Slack RTM. Aborting...");
		}
	}
	
	private void dispatchKeepAliveThread() {
		keepAliveThread = new Thread(new KeepAlive(), "Keep Alive");
		keepAliveThread.start();
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
	
	private synchronized long retreiveMessageId() {
		this.messageID = this.messageID + 1;
		return messageID;
	}

	public final SlackSessionManager getSessionManager() {
		return sessionManager;
	}
	
	private class KeepAlive implements Runnable {
		@Override
		public void run() {
			LOGGER.info("Initializing keep alive thread.");
			while (!shutdownRequested) {
				if (Math.abs(lastPingAck - lastPing) >= timeoutLimit || websocketSession == null) {
					lastPing = -1L;
					lastPingAck = -1L;
					LOGGER.info("Slack RTM connection lost.");
					try {
						if (websocketSession != null) {
							websocketSession.close();
						}
					} catch (IOException e) {
						LOGGER.error("Unable to close out existing websocket session. Nulling out and continuing.");
					} finally {
						websocketSession = null;
					}
					
					if (autoReconnect) {
						LOGGER.info("Auto rejoin enabled. Attempting rejoin...");
						retreiveWebSocketSession();
					} else {
						LOGGER.info("Auto rejoin disabled. Shutting down");
						break;
					}	
				} else {
					try {
						websocketSession.getBasicRemote().sendText("{\"type\":\"ping\",\"id\":" + retreiveMessageId() + "}");
						LOGGER.info("Ping sent!");
						lastPing = System.currentTimeMillis();
						Thread.sleep(10000);
					} catch (IOException | InterruptedException e) {
						LOGGER.error("Unable to send ping message. Shutting down...");
						break;
					}
				}
			}	
			LOGGER.info("Keep alive thread stopped.");
			return;
		}	
	}
}

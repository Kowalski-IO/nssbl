package io.kowalski.nssc.manager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class WebSocketManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketManager.class);
	
	private final ClientEndpointConfig configuration = ClientEndpointConfig.Builder.create().build();
	private final SlackSessionManager sessionManager;
	private final MessageBrokerManager messageBrokerManager;
	
	private Session websocketSession;

	@Inject
	public WebSocketManager(final SlackSessionManager sessionManager, final MessageBrokerManager messageBrokerManager) {
		this.sessionManager = sessionManager;
		this.messageBrokerManager = messageBrokerManager;
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
								System.out.println("Received message: " + message);
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

	public final SlackSessionManager getSessionManager() {
		return sessionManager;
	}
}

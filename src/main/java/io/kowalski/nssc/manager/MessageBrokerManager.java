package io.kowalski.nssc.manager;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import io.kowalski.nssc.models.SlackEvent;

public class MessageBrokerManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(MessageBrokerManager.class);

	private final ConnectionFactory factory;

	@Inject
	public MessageBrokerManager(@Named("messageBrokerHost") final String messageBrokerHost,
			@Named("messageBrokerPort") final int messageBrokerPort) {
		factory = new ConnectionFactory();
		factory.setHost(messageBrokerHost);
		factory.setPort(messageBrokerPort);
	}

	public final Optional<Connection> retreiveMessageBrokerConnection() {
		Connection messageBrokerConnection = null;
		try {
			messageBrokerConnection = factory.newConnection();
		} catch (IOException | TimeoutException e) {
			LOGGER.error("Unable to establish connection with Message Broker.", e);
		}
		return Optional.ofNullable(messageBrokerConnection);
	}

	public final void sendSlackEvent(final SlackEvent message) {
		
		Optional<Connection> optionalConnection = this.retreiveMessageBrokerConnection();
		
		if (!optionalConnection.isPresent()) {
			throw new IllegalStateException("No connection present for handling message. Aborting...");
		}
		
		try {
			Connection connection = optionalConnection.get();
			Channel channel = connection.createChannel();
			byte[] payload = SerializationUtils.serialize(message);
			channel.basicPublish("", message.getQueueName(), null, payload);
		} catch (IOException e) {
			LOGGER.error("Unable to publish message to broker.", e);
		}
	}
}

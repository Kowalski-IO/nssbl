package io.kowalski.nssbl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.google.inject.Inject;

import io.kowalski.nssbl.manager.WebSocketManager;
import io.kowalski.nssbl.models.SlackEvent;

public abstract class AbstractBot implements Runnable {
	
	protected final BlockingQueue<SlackEvent> queue;
	private final WebSocketManager socketManager;
	
	@Inject
	public AbstractBot(final WebSocketManager socketManager) {
		this.queue = new ArrayBlockingQueue<SlackEvent>(2048);
		this.socketManager = socketManager;
		this.socketManager.setQueue(queue);
	}
	
	@Override
	public void run() {
		socketManager.retreiveWebSocketSession().get();
	}
}

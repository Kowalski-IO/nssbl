package io.kowalski.nssbl.runnables;

import io.kowalski.nssbl.manager.WebSocketManager;

public class WebsocketRunnable implements Runnable {
	
	private final WebSocketManager socketManager;
	
	public WebsocketRunnable(final WebSocketManager socketManager) {
		this.socketManager = socketManager;
	}

	@Override
	public void run() {
		socketManager.connect();
		
	}
}

package io.kowalski.nssbl;

import java.io.IOException;

import javax.websocket.Session;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.kowalski.nssbl.guice.GuiceModule;
import io.kowalski.nssbl.manager.WebSocketManager;

public class App {
	
	private static final Injector injector = Guice.createInjector(new GuiceModule());
	private static final WebSocketManager socketManager = injector.getInstance(WebSocketManager.class);
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		Session session = socketManager.retreiveWebSocketSession().get();
		
		while (true) {
			Thread.sleep(1000);
		}	
	}
}

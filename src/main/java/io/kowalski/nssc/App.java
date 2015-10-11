package io.kowalski.nssc;

import java.io.IOException;

import javax.websocket.Session;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.kowalski.nssc.guice.GuiceModule;
import io.kowalski.nssc.manager.WebSocketManager;

public class App {
	
	private static final Injector injector = Guice.createInjector(new GuiceModule());
	private static final WebSocketManager socketManager = injector.getInstance(WebSocketManager.class);
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		Session session = socketManager.retreiveWebSocketSession().get();
		
		System.out.println(session.getProtocolVersion());
		
		while (true) {
			Thread.sleep(1000);
		}
		
	}
}

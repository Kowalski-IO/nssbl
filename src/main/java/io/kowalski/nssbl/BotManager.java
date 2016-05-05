package io.kowalski.nssbl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.google.inject.Guice;
import com.google.inject.Injector;

import io.kowalski.nssbl.guice.GuiceModule;
import io.kowalski.nssbl.manager.SlackSessionManager;
import io.kowalski.nssbl.manager.WebSocketManager;
import io.kowalski.nssbl.models.SlackEvent;
import io.kowalski.nssbl.runnables.SlackSessionRunnable;
import io.kowalski.nssbl.runnables.WebsocketRunnable;

public class BotManager {

    private final Injector injector;
    private final SlackSessionManager sessionManager;
    private final WebSocketManager socketManager;

    private Thread slackSessionThread;
    private Thread webSocketThread;

    private volatile BlockingQueue<SlackEvent> eventQueue;

    public BotManager() {
        eventQueue = new ArrayBlockingQueue<>(2048);
        injector =  Guice.createInjector(new GuiceModule(eventQueue));

        sessionManager = injector.getInstance(SlackSessionManager.class);
        socketManager = injector.getInstance(WebSocketManager.class);
    }

    public void run() {
        slackSessionThread = new Thread(new SlackSessionRunnable(sessionManager), "Slack Session Thread");
        webSocketThread = new Thread(new WebsocketRunnable(socketManager), "Web Socket Thread");
        slackSessionThread.start();
        webSocketThread.start();
    }

    public synchronized final BlockingQueue<SlackEvent> getEventQueue() {
        return eventQueue;
    }
}

package io.kowalski.nssbl.guice;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import io.kowalski.nssbl.models.SlackEvent;
import io.kowalski.nssbl.models.SlackEventType;

public class GuiceModule extends AbstractModule {

    private final static Set<SlackEventType> ignoredEventTypes;
    private final BlockingQueue<SlackEvent> queue;

    static {
        ignoredEventTypes = new HashSet<SlackEventType>();
        ignoredEventTypes.add(SlackEventType.USER_TYPING);
        ignoredEventTypes.add(SlackEventType.PRESENCE_CHANGE);
    }

    public GuiceModule(final BlockingQueue<SlackEvent> queue) {
        this.queue = queue;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("slackToken")).to("xoxb-24639831251-mKST11OR5JFov6zi1Fgj7GXI");
        bindConstant().annotatedWith(Names.named("botID")).to("Scat");
        bindConstant().annotatedWith(Names.named("autoReconnect")).to(true);
        bind(Key.get(new TypeLiteral<BlockingQueue<SlackEvent>>(){})).toInstance(queue);
        bind(Key.get(new TypeLiteral<Set<SlackEventType>>(){})).toInstance(ignoredEventTypes);
    }
}

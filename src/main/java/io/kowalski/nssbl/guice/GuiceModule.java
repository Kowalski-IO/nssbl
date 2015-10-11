package io.kowalski.nssbl.guice;

import java.util.HashSet;
import java.util.Set;

import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import io.kowalski.nssbl.models.SlackEventType;

public class GuiceModule extends AbstractModule {
	
	private final static Set<SlackEventType> ignoredEventTypes;
	
	static {
		ignoredEventTypes = new HashSet<SlackEventType>();
		ignoredEventTypes.add(SlackEventType.USER_TYPING);
		ignoredEventTypes.add(SlackEventType.PRESENCE_CHANGE);
	}

	@Override
	protected void configure() {
		 bindConstant().annotatedWith(Names.named("slackToken")).to("xoxb-12095707300-T4CfPUksPaNKDDsHboBEQogS");
		 bindConstant().annotatedWith(Names.named("messageBrokerHost")).to("192.168.99.100");
		 bindConstant().annotatedWith(Names.named("messageBrokerPort")).to(32768);
		 bindConstant().annotatedWith(Names.named("botID")).to("senapi");
		 bind(Key.get(new TypeLiteral<Set<SlackEventType>>(){})).toInstance(ignoredEventTypes);
	}
}

package io.kowalski.nssc.guice;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class GuiceModule extends AbstractModule {

	@Override
	protected void configure() {
		 bindConstant().annotatedWith(Names.named("slackToken")).to("xoxb-12095707300-T4CfPUksPaNKDDsHboBEQogS");
		 bindConstant().annotatedWith(Names.named("messageBrokerHost")).to("192.168.99.100");
		 bindConstant().annotatedWith(Names.named("messageBrokerPort")).to(32768);
	}
}

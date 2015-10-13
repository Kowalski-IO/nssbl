package io.kowalski.nssbl.runnables;

import io.kowalski.nssbl.manager.SlackSessionManager;

public class SlackSessionRunnable implements Runnable {
	
	public final SlackSessionManager sessionManager;
	
	public SlackSessionRunnable(final SlackSessionManager sessionManager) {
		this.sessionManager = sessionManager;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
}

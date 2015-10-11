package io.kowalski.nssbl.models;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SlackSession implements Serializable {

	private static final long serialVersionUID = 6179443488079116596L;
	
	private boolean ok;
	
	private String url;
	
	private List<SlackUser> users;
	
	public SlackSession() {
		
	}
	
	public final boolean isOk() {
		return ok;
	}
	
	public final void setOk(final boolean ok) {
		this.ok = ok;
	}
	
	public final String getUrl() {
		return url;
	}

	public final void setUrl(final String url) {
		this.url = url;
	}

	public final List<SlackUser> getUsers() {
		return users;
	}
	
	public final void setUsers(final List<SlackUser> users) {
		this.users = users;
	}
}

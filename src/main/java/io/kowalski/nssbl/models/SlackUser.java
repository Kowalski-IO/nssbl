package io.kowalski.nssbl.models;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class SlackUser implements Serializable {

	private static final long serialVersionUID = -144311445282468225L;
	
	private String id;
	private String name;
	private String realName;
	private String status;
	private String tz;
	
	private boolean deleted;
	private boolean admin;
	private boolean bot;
	private boolean owner;
	private boolean primaryOwner;
	private boolean restricted;
	private boolean ultraRestricted;
	
	public SlackUser() {
	}
	
	public final String getId() {
		return id;
	}

	public final void setId(String id) {
		this.id = id;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final String getRealName() {
		return realName;
	}

	public final void setRealName(String realName) {
		this.realName = realName;
	}

	public final String getStatus() {
		return status;
	}

	public final void setStatus(String status) {
		this.status = status;
	}

	public final String getTz() {
		return tz;
	}

	public final void setTz(String tz) {
		this.tz = tz;
	}

	public final boolean isDeleted() {
		return deleted;
	}

	public final void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public final boolean isAdmin() {
		return admin;
	}

	public final void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public final boolean isBot() {
		return bot;
	}

	public final void setBot(boolean bot) {
		this.bot = bot;
	}

	public final boolean isOwner() {
		return owner;
	}

	public final void setOwner(boolean owner) {
		this.owner = owner;
	}

	public final boolean isPrimaryOwner() {
		return primaryOwner;
	}

	public final void setPrimaryOwner(boolean primaryOwner) {
		this.primaryOwner = primaryOwner;
	}

	public final boolean isRestricted() {
		return restricted;
	}

	public final void setRestricted(boolean restricted) {
		this.restricted = restricted;
	}

	public final boolean isUltraRestricted() {
		return ultraRestricted;
	}

	public final void setUltraRestricted(boolean ultraRestricted) {
		this.ultraRestricted = ultraRestricted;
	}

	public class Profile implements Serializable {

		private static final long serialVersionUID = -5062955203808711628L;
		
		private String email;
		private String firstName;
		private String lastName;
		private String phone;
		private String realName;
		private String skype;
		private String title;
		
		public Profile() {
			
		}

		public final String getEmail() {
			return email;
		}

		public final void setEmail(String email) {
			this.email = email;
		}

		public final String getFirstName() {
			return firstName;
		}

		public final void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public final String getLastName() {
			return lastName;
		}

		public final void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public final String getPhone() {
			return phone;
		}

		public final void setPhone(String phone) {
			this.phone = phone;
		}

		public final String getRealName() {
			return realName;
		}

		public final void setRealName(String realName) {
			this.realName = realName;
		}

		public final String getSkype() {
			return skype;
		}

		public final void setSkype(String skype) {
			this.skype = skype;
		}

		public final String getTitle() {
			return title;
		}

		public final void setTitle(String title) {
			this.title = title;
		}
	}	
}

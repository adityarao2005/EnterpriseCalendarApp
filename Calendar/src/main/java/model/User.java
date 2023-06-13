package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.api.services.classroom.Classroom;

import model.events.RCalendar;

// Represents the user/client of this application
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	// Fields
	private String username;
	private String name;
	private String password;
	private GoogleProfile profile;
	private List<RCalendar> calendars = new ArrayList<>(List.of(new RCalendar("DEFAULT")));

	// Constructors
	public User() {
	}

	public User(String username, String name, String password, GoogleProfile profile) {
		this.username = username;
		this.password = password;
		this.name = name;
		this.profile = profile;
	}

	// Nested static class - represents a google profile
	public static class GoogleProfile implements Serializable {
		private static final long serialVersionUID = 1L;

		// Holds a google token and a credential
		// Only token gets serialized, credential doesn't
		private String token;
		private transient Classroom classroom;

		// Constructors
		public GoogleProfile() {

		}

		public GoogleProfile(String token) {
			this.token = token;
		}

		// Getters and Setters
		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public Classroom getClassroom() {
			return classroom;
		}

		public void setClassroom(Classroom classroom) {
			this.classroom = classroom;
		}

		@Override
		public String toString() {
			return "GoogleProfile [token=" + token + ", classroom=" + classroom + "]";
		}

		@Override
		public int hashCode() {
			return Objects.hash(token);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			GoogleProfile other = (GoogleProfile) obj;
			return Objects.equals(token, other.token);
		}

	}

	// Getters and setters
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public GoogleProfile getProfile() {
		return profile;
	}

	public void setProfile(GoogleProfile profile) {
		this.profile = profile;
	}

	public List<RCalendar> getCalendars() {
		return calendars;
	}

	public void setCalendars(List<RCalendar> calendars) {
		this.calendars = calendars;
	}

	@Override
	public String toString() {
		return "User [username=" + username + ", name=" + name + ", password=" + password + ", profile=" + profile
				+ ", calendars=" + calendars + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(calendars, name, password, profile, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(calendars, other.calendars) && Objects.equals(name, other.name)
				&& Objects.equals(password, other.password) && Objects.equals(profile, other.profile)
				&& Objects.equals(username, other.username);
	}

}

package model.events;

import java.io.Serializable;
import java.util.List;

import model.Analogous;

public class RCalendar implements Serializable, Analogous<RCalendar> {
	private static final long serialVersionUID = 1L;

	private String name;
	private List<Reminder> reminders;
	private boolean classroomLoaded;

	public RCalendar() {
		super();
	}

	public RCalendar(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Reminder> getReminders() {
		return reminders;
	}

	public void setReminders(List<Reminder> reminders) {
		this.reminders = reminders;
	}

	public boolean isClassroomLoaded() {
		return classroomLoaded;
	}

	public void setClassroomLoaded(boolean classroomLoaded) {
		this.classroomLoaded = classroomLoaded;
	}

	public boolean identical(RCalendar calendar) {
		return calendar.name.equals(name);
	}
}

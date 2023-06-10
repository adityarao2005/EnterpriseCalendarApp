package model.events;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;

// Generic Reminder Superclass
public abstract class Reminder implements Serializable {
	private static final long serialVersionUID = 1L;
	// Fields
	private String name;
	private LocalTime reminder;
	private RCalendar calendar;

	// Zero Arg Constructors
	public Reminder() {
		super();
	}

	// Field Arg Constructor
	public Reminder(String name, LocalTime reminder) {
		super();
		this.name = name;
		this.reminder = reminder;
	}

	// Getters and Setters
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalTime getReminder() {
		return reminder;
	}

	public void setReminder(LocalTime reminder) {
		this.reminder = reminder;
	}

	public RCalendar getCalendar() {
		return calendar;
	}

	public void setCalendar(RCalendar calendar) {
		this.calendar = calendar;
	}

	@Override
	public String toString() {
		return String.format("%s [name=%s, reminder=%s, calendar=%s", this.getClass(), name, reminder, calendar);
	}

}

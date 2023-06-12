package model.events;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

// Generic Reminder Superclass
public abstract class Reminder implements Serializable, Appearable {
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

	public void from(Reminder reminder) {
		this.name = reminder.name;
		this.reminder = reminder.reminder;
		this.calendar = reminder.calendar;
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

	@Override
	public int hashCode() {
		return Objects.hash(calendar, name, reminder);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reminder other = (Reminder) obj;
		return Objects.equals(calendar, other.calendar) && Objects.equals(name, other.name)
				&& Objects.equals(reminder, other.reminder);
	}

}

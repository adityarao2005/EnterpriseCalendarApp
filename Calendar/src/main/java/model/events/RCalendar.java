package model.events;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javafx.scene.paint.Color;
import model.Analogous;

// Represents a calendar which has a collection of events
public class RCalendar implements Serializable, Analogous<RCalendar> {
	private static final long serialVersionUID = 1L;

	// Fields
	private String name;
	private List<Reminder> reminders = new ArrayList<>();
	private boolean classroomLoaded;
	// Removes serializability
	private transient Color color;

	// Constructors
	public RCalendar() {
		super();
	}

	public RCalendar(String name) {
		this.name = name;
	}

	// Getters and setters
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

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	// Helper methods
	public boolean identical(RCalendar calendar) {
		return calendar.name.equals(name);
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(classroomLoaded, name, reminders);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RCalendar other = (RCalendar) obj;
		return classroomLoaded == other.classroomLoaded && Objects.equals(name, other.name)
				&& Objects.equals(reminders, other.reminders);
	}

}

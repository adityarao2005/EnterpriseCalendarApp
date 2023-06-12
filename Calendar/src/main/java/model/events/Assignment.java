package model.events;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

import model.Analogous;

public class Assignment extends CompleteableReminder implements Analogous<Assignment> {
	private static final long serialVersionUID = 1L;

	private LocalDateTime due;
	private List<RTask> schedule;

	public Assignment() {
		super();
	}

	public Assignment(String name) {
		super();
		setName(name);
	}

	public Assignment(String name, LocalTime reminder, LocalDateTime due, boolean completed, List<RTask> schedule) {
		super(name, reminder, completed);
		this.due = due;
		this.schedule = schedule;
	}

	@Override
	public void from(Reminder reminder) {
		super.from(reminder);

		Assignment assignment = (Assignment) reminder;

		this.due = assignment.due;
		this.schedule = assignment.schedule;
	}

	public LocalDateTime getDue() {
		return due;
	}

	public void setDue(LocalDateTime due) {
		this.due = due;
	}

	public List<RTask> getSchedule() {
		return schedule;
	}

	public void setSchedule(List<RTask> schedule) {
		this.schedule = schedule;
	}

	@Override
	public boolean identical(Assignment other) {
		return this.getName().equals(other.getName());
	}

	@Override
	public LocalDateTime finishBy() {
		return due;
	}

	@Override
	public String toString() {
		return String.format("%s, due=%s, schedule=%s", super.toString(), due, schedule);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(due, schedule);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Assignment other = (Assignment) obj;
		return Objects.equals(due, other.due) && Objects.equals(schedule, other.schedule);
	}

	@Override
	public boolean occursOn(LocalDate date) {
		return due.toLocalDate().equals(date);
	}

	@Override
	public LocalTime appearsAt() {
		return due.toLocalTime();
	}

}

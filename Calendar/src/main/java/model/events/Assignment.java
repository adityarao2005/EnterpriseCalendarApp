package model.events;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

	public void from(Assignment assignment) {
		this.setName(assignment.getName());
		this.setReminder(assignment.getReminder());
		this.setDue(assignment.getDue());
		this.setCompleted(assignment.isCompleted());
		this.setSchedule(assignment.getSchedule());
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
}

package model.events;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public class RTask extends CompleteableReminder {
	private static final long serialVersionUID = 1L;

	private LocalTime from;
	private LocalTime to;
	private LocalDate date;
	private boolean skipped;
	private Assignment assignment;

	public RTask() {
		super();
	}

	public RTask(String name, LocalTime reminder, LocalTime from, LocalTime to, LocalDate date, boolean skipped,
			boolean completed) {
		super(name, reminder, completed);
		this.from = from;
		this.to = to;
		this.date = date;
		this.skipped = skipped;
	}

	@Override
	public void from(Reminder reminder) {
		super.from(reminder);

		RTask assignment = (RTask) reminder;

		this.from = assignment.from;
		this.to = assignment.to;
		this.date = assignment.date;
		this.assignment = assignment.assignment;
	}

	public LocalTime getFrom() {
		return from;
	}

	public void setFrom(LocalTime from) {
		this.from = from;
	}

	public LocalTime getTo() {
		return to;
	}

	public void setTo(LocalTime to) {
		this.to = to;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public boolean isSkipped() {
		return skipped;
	}

	public void setSkipped(boolean skipped) {
		this.skipped = skipped;
	}

	@Override
	public LocalDateTime finishBy() {
		return LocalDateTime.of(date, to);
	}

	@Override
	public String toString() {
		return String.format("%s, from=%s, to=%s, date=%s, skipped=%b", super.toString(), from, to, date, skipped);
	}

	public Assignment getAssignment() {
		return assignment;
	}

	public void setAssignment(Assignment assignment) {
		this.assignment = assignment;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(assignment, date, from, skipped, to);
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
		RTask other = (RTask) obj;
		return Objects.equals(assignment, other.assignment) && Objects.equals(date, other.date)
				&& Objects.equals(from, other.from) && skipped == other.skipped && Objects.equals(to, other.to);
	}

	@Override
	public boolean occursOn(LocalDate date) {
		return date.equals(this.date);
	}

	@Override
	public LocalTime appearsAt() {
		return from;
	}

}

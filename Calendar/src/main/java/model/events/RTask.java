package model.events;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class RTask extends CompleteableReminder {
	private static final long serialVersionUID = 1L;

	private LocalTime from;
	private LocalTime to;
	private LocalDate date;
	private boolean skipped;

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
}

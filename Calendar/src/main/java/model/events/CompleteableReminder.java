package model.events;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

public abstract class CompleteableReminder extends Reminder {
	private static final long serialVersionUID = 1L;

	private boolean completed;

	public CompleteableReminder() {
		super();
	}

	public CompleteableReminder(String name, LocalTime reminder, boolean completed) {
		super(name, reminder);
		this.completed = completed;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	@Override
	public void from(Reminder reminder) {
		super.from(reminder);

		CompleteableReminder assignment = (CompleteableReminder) reminder;

		this.completed = assignment.completed;
	}

	public abstract LocalDateTime finishBy();

	@Override
	public String toString() {
		return String.format("%s, completed=%b", super.toString(), completed);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(completed);
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
		CompleteableReminder other = (CompleteableReminder) obj;
		return completed == other.completed;
	}

}

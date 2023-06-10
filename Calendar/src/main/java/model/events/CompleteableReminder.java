package model.events;

import java.time.LocalDateTime;
import java.time.LocalTime;

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

	public abstract LocalDateTime finishBy();

	@Override
	public String toString() {
		return String.format("%s, completed=%b", super.toString(), completed);
	}

}

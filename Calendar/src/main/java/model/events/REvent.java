package model.events;

import java.time.LocalDateTime;
import java.time.LocalTime;

import model.occurrence.Occurrence;

public class REvent extends Reminder {
	private static final long serialVersionUID = 1L;

	private Occurrence occurence;

	public REvent() {
		super();
	}

	public REvent(String name, LocalTime reminder, Occurrence occurence) {
		super(name, reminder);
		this.occurence = occurence;
	}

	public Occurrence getOccurence() {
		return occurence;
	}

	public void setOccurence(Occurrence occurence) {
		this.occurence = occurence;
	}

}

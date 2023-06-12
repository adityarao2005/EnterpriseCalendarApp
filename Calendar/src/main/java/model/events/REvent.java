package model.events;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(occurence);
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
		REvent other = (REvent) obj;
		return Objects.equals(occurence, other.occurence);
	}

	@Override
	public boolean occursOn(LocalDate date) {
		return occurence.occursOn(date);
	}

}

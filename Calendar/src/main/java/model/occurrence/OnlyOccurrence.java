package model.occurrence;

import java.time.LocalDate;
import java.time.LocalTime;

public class OnlyOccurrence extends Occurrence {
	private static final long serialVersionUID = 1L;
	
	private LocalDate date;

	public OnlyOccurrence() {
		super();
	}

	public OnlyOccurrence(LocalTime time, LocalDate date) {
		super(time);
		this.date = date;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

}

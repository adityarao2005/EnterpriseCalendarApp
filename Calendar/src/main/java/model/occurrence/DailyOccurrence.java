package model.occurrence;

import java.time.LocalDate;
import java.time.LocalTime;

// Represents sometime which occurs everyday
public class DailyOccurrence extends Occurrence {
	private static final long serialVersionUID = 1L;

	// Constructors
	public DailyOccurrence() {
		super();
	}

	public DailyOccurrence(LocalTime time) {
		super(time);
	}

	// Helper methods
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}
	
	// Occurs everyday
	@Override
	public boolean occursOn(LocalDate date) {
		return true;
	}

	
}

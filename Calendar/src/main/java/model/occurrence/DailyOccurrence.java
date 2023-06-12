package model.occurrence;

import java.time.LocalDate;
import java.time.LocalTime;

public class DailyOccurrence extends Occurrence {
	private static final long serialVersionUID = 1L;

	public DailyOccurrence() {
		super();
	}

	public DailyOccurrence(LocalTime time) {
		super(time);
	}

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

	@Override
	public boolean occursOn(LocalDate date) {
		return true;
	}

	
}

package model.occurrence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

// Represents something which occurs every month
public class MonthlyOccurrence extends Occurrence {
	private static final long serialVersionUID = 1L;

	// Fields
	private int dayOfMonth;

	// Constructors
	public MonthlyOccurrence() {
		super();
	}

	public MonthlyOccurrence(LocalTime time, int dayOfMonth) {
		super(time);
		this.dayOfMonth = dayOfMonth;
	}

	// Getters and setters
	public int getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	// Helper methods
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(dayOfMonth);
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
		MonthlyOccurrence other = (MonthlyOccurrence) obj;
		return dayOfMonth == other.dayOfMonth;
	}

	@Override
	public boolean occursOn(LocalDate date) {
		return date.getDayOfMonth() == dayOfMonth;
	}

}

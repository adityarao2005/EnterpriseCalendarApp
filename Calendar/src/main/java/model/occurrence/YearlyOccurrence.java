package model.occurrence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.Objects;

// represents something which occurs yearly
public class YearlyOccurrence extends Occurrence {
	private static final long serialVersionUID = 1L;

	// Fields
	private int dayOfMonth;
	private Month month;

	// constructors
	public YearlyOccurrence() {
		super();
	}

	public YearlyOccurrence(LocalTime time, int dayOfMonth, Month month) {
		super(time);
		this.dayOfMonth = dayOfMonth;
		this.month = month;
	}

	// Getters and Setters
	public int getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	// Helper methods
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(dayOfMonth, month);
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
		YearlyOccurrence other = (YearlyOccurrence) obj;
		return dayOfMonth == other.dayOfMonth && month == other.month;
	}

	@Override
	public boolean occursOn(LocalDate date) {
		return dayOfMonth == date.getDayOfMonth() && month.equals(date.getMonth());
	}

}

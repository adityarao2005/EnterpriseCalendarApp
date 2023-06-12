package model.occurrence;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.Objects;

public class WeeklyOccurrence extends Occurrence {
	private static final long serialVersionUID = 1L;

	private EnumSet<DayOfWeek> daysOfWeek;

	public WeeklyOccurrence() {
		super();
	}

	public WeeklyOccurrence(LocalTime time, EnumSet<DayOfWeek> daysOfWeek) {
		super(time);
		this.daysOfWeek = daysOfWeek;
	}

	public EnumSet<DayOfWeek> getDaysOfWeek() {
		return daysOfWeek;
	}

	public void setDaysOfWeek(EnumSet<DayOfWeek> daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(daysOfWeek);
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
		WeeklyOccurrence other = (WeeklyOccurrence) obj;
		return Objects.equals(daysOfWeek, other.daysOfWeek);
	}

	@Override
	public boolean occursOn(LocalDate date) {
		return daysOfWeek.contains(date.getDayOfWeek());
	}

}

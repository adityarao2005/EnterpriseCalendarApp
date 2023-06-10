package model.occurrence;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.EnumSet;

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

}

package model.occurrence;

import java.time.LocalTime;

public class MonthlyOccurrence extends Occurrence {
	private static final long serialVersionUID = 1L;
	
	private int dayOfMonth;

	public MonthlyOccurrence() {
		super();
	}

	public MonthlyOccurrence(LocalTime time, int dayOfMonth) {
		super(time);
		this.dayOfMonth = dayOfMonth;
	}

	public int getDayOfMonth() {
		return dayOfMonth;
	}

	public void setDayOfMonth(int dayOfMonth) {
		this.dayOfMonth = dayOfMonth;
	}

}

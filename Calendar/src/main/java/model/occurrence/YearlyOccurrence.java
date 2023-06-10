package model.occurrence;

import java.time.LocalTime;
import java.time.Month;

public class YearlyOccurrence extends Occurrence {
	private static final long serialVersionUID = 1L;
	
	private int dayOfMonth;
	private Month month;

	public YearlyOccurrence() {
		super();
	}

	public YearlyOccurrence(LocalTime time, int dayOfMonth, Month month) {
		super(time);
		this.dayOfMonth = dayOfMonth;
		this.month = month;
	}

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

}

package model.occurrence;

import java.time.LocalTime;

public class DailyOccurrence extends Occurrence {
	private static final long serialVersionUID = 1L;

	public DailyOccurrence() {
		super();
	}

	public DailyOccurrence(LocalTime time) {
		super(time);
	}

}

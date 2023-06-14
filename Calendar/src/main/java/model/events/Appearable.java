package model.events;

import java.time.LocalDate;
import java.time.LocalTime;

// Interface to determine appearance on a date and get the time from it
public interface Appearable {
	// Check whether it occurs on a date
	public abstract boolean occursOn(LocalDate date);

	// Get the time of appearance
	public abstract LocalTime appearsAt();
}


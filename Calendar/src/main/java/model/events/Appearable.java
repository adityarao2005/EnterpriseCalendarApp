package model.events;

import java.time.LocalDate;
import java.time.LocalTime;

public interface Appearable {
	public abstract boolean occursOn(LocalDate date);

	public abstract LocalTime appearsAt();
}


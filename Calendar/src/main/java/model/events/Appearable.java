package model.events;

import java.time.LocalDate;

public interface Appearable {
	public abstract boolean occursOn(LocalDate date);

}

package model.occurrence;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

import model.events.Appearable;

// Occurrence superclass
public abstract class Occurrence implements Serializable, Appearable {
	private static final long serialVersionUID = 1L;

	private LocalTime time;

	public Occurrence() {
		super();
	}

	public Occurrence(LocalTime time) {
		super();
		this.time = time;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	@Override
	public int hashCode() {
		return Objects.hash(time);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Occurrence other = (Occurrence) obj;
		return Objects.equals(time, other.time);
	}
	

}

package model.occurrence;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class OnlyOccurrence extends Occurrence {
	private static final long serialVersionUID = 1L;

	private LocalDate date;

	public OnlyOccurrence() {
		super();
	}

	public OnlyOccurrence(LocalTime time, LocalDate date) {
		super(time);
		this.date = date;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + Objects.hash(date);
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
		OnlyOccurrence other = (OnlyOccurrence) obj;
		return Objects.equals(date, other.date);
	}

	@Override
	public boolean occursOn(LocalDate date) {
		return this.date.equals(date);
	}

}

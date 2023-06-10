package model.occurrence;

import java.io.Serializable;
import java.time.LocalTime;

// Occurrence superclass
public abstract class Occurrence implements Serializable {
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
	
	
}

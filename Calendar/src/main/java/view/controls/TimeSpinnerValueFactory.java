package view.controls;

import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.Locale;

import javafx.scene.control.SpinnerValueFactory;
import javafx.util.converter.LocalTimeStringConverter;

// A value factory for localtime
public class TimeSpinnerValueFactory extends SpinnerValueFactory<LocalTime> {

	// Constructor
	public TimeSpinnerValueFactory() {
		this.setConverter(new LocalTimeStringConverter(FormatStyle.SHORT));
		this.setValue(LocalTime.now());
	}

	// Decrement method
	@Override
	public void decrement(int steps) {
		if (getValue() == null)
			setValue(LocalTime.now());
		else {
			LocalTime time = (LocalTime) getValue();
			setValue(time.minusMinutes(steps));
		}
	}

	// Increment method
	@Override
	public void increment(int steps) {
		if (this.getValue() == null)
			setValue(LocalTime.now());
		else {
			LocalTime time = (LocalTime) getValue();
			setValue(time.plusMinutes(steps));
		}
	}

}
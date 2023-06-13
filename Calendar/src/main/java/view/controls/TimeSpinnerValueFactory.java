package view.controls;

import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.Locale;

import javafx.scene.control.SpinnerValueFactory;
import javafx.util.converter.LocalTimeStringConverter;

/**
 * A Spinner Value Factory for getting the Time
 * 
 * @author Raos
 *
 */
public class TimeSpinnerValueFactory extends SpinnerValueFactory<LocalTime> {

	/**
	 * Constructs the Spinnner Value Factory
	 */
	public TimeSpinnerValueFactory() {
		this.setConverter(new LocalTimeStringConverter(FormatStyle.SHORT));
		this.setValue(LocalTime.now());
	}

	@Override
	public void decrement(int steps) {
		if (getValue() == null)
			setValue(LocalTime.now());
		else {
			LocalTime time = (LocalTime) getValue();
			setValue(time.minusMinutes(steps));
		}
	}

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
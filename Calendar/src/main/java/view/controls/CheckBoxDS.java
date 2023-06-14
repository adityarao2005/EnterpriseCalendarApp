package view.controls;

import javafx.beans.property.SimpleBooleanProperty;

// A datastructure used for checkbox list view cells
public class CheckBoxDS<T> {
	// Feilds
	private T value;
	private final SimpleBooleanProperty checked = new SimpleBooleanProperty(true);

	// constructor
	public CheckBoxDS(T value) {
		this.value = value;
	}

	// Getter and Setters
	public T getValue() {
		return value;
	}

	public SimpleBooleanProperty checkedProperty() {
		return checked;
	}

	public boolean getChecked() {
		return checkedProperty().get();
	}

	public void setChecked(boolean checked) {
		checkedProperty().set(checked);
	}

	@Override
	public String toString() {
		return value.toString();
	}
}

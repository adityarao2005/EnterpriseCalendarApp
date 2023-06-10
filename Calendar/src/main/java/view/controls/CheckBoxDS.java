package view.controls;

import javafx.beans.property.SimpleBooleanProperty;

public class CheckBoxDS<T> {
	private T value;
	private final SimpleBooleanProperty checked = new SimpleBooleanProperty(true);

	public CheckBoxDS(T value) {
		this.value = value;
	}

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

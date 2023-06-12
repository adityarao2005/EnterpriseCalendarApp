package view.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import model.events.Reminder;
import view.controls.skin.SchedulerSkin;

public final class Scheduler extends Control {

	private final ObjectProperty<ObservableList<Reminder>> reminder = new SimpleObjectProperty<>(this, "reminder",
			FXCollections.observableArrayList());

	public ObjectProperty<ObservableList<Reminder>> reminderProperty() {
		return reminder;
	}

	public ObservableList<Reminder> getReminder() {
		return reminderProperty().get();
	}

	public void setReminder(ObservableList<Reminder> reminder) {
		reminderProperty().set(reminder);
	}

	@Override
	protected Skin<?> createDefaultSkin() {
		return new SchedulerSkin(this);
	}

}

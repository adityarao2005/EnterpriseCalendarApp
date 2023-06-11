package controller.spec;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalTime;
import java.util.stream.Collectors;

import application.Application;
import controller.EventModalController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.Alert.AlertType;
import model.events.RTask;
import model.events.Reminder;

public class TaskSpecController implements EventSpecController {
	@FXML
	private Spinner<LocalTime> startTimeSpinner;

	@FXML
	private Spinner<LocalTime> endTimeSpinner;

	@FXML
	private DatePicker datePicker;

	private RTask reminder;

	public TaskSpecController() {
	}

	public TaskSpecController(RTask reminder) {
		this.reminder = reminder;
	}

	@FXML
	private void initialize() {

		if (reminder != null) {
			startTimeSpinner.getValueFactory().setValue(reminder.getFrom());
			endTimeSpinner.getValueFactory().setValue(reminder.getTo());
			datePicker.setValue(reminder.getDate());
		}
	}

	@Override
	public Reminder createReminder() {
		RTask task = new RTask();

		task.setFrom(startTimeSpinner.getValue());
		task.setTo(endTimeSpinner.getValue());
		task.setDate(datePicker.getValue());

		// TODO: FIX STUFF, make sure from < to and that no other tasks intersect with
		// each other
		return task;
	}

}

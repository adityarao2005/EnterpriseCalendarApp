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

		return task;
	}

	// Utility method to create a task
	public static RTask createTaskFrom(RTask template) {
		// Declare the task
		RTask task = null;

		// While the task is empty
		while (task == null) {
			// Get the task from the dialog
			task = Application.getApplication().dialog("/view/EventModalView.fxml", "Create Task", clazz -> {
				// Create a new Event modal controller of task
				if (clazz == EventModalController.class) {
					return new EventModalController(template);
				}

				try {
					return clazz.getDeclaredConstructor().newInstance();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				}

				return null;
			});

			// If the task is empty
			if (task == null) {
				// Get the errors if anyn
				if (Application.getApplication().getErrors() != null) {

					// Alert the user of the errors
					Alert errorDialog = new Alert(AlertType.ERROR, "Error");

					errorDialog.setHeaderText("You have the following errors: \n - "
							+ Application.getApplication().getErrors().stream().collect(Collectors.joining("\n - ")));

					// Make the user acknowledge
					errorDialog.showAndWait().get();

					// Set the errors back to null
					Application.getApplication().setErrors(null);
				} else // If no errors found, then the client must have closed or dismissed this dialog
					return null;
			}
		}

		// Set the task
		return task;

	}

}

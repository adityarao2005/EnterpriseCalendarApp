package controller.spec;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.stream.Collectors;

import application.Application;
import controller.EventModalController;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.scene.control.Alert.AlertType;
import model.events.Assignment;
import model.events.RTask;
import model.events.Reminder;
import view.controls.ControlTable;

public class AssignmentSpecController implements EventSpecController {
	@FXML
	private Spinner<LocalTime> dueTimeSpinner;

	@FXML
	private DatePicker datePicker;

	@FXML
	private ControlTable<RTask> tasksTable;

	private Assignment assignment;

	public AssignmentSpecController() {

	}

	public AssignmentSpecController(Assignment assignment) {
		this.assignment = assignment;
	}

	@FXML
	private void initialize() {
		// Create a pop up dialog to create a task
		tasksTable.setOnListAddRequestProperty(() -> EventSpecController.createReminderFrom(new RTask()));

		// Set cell factory
		tasksTable.setCellFactory(view -> {
			ListCell<RTask> cell = new ListCell<>() {
				@Override
				public void updateItem(RTask task, boolean empty) {
					super.updateItem(task, empty);

					LocalTimeStringConverter converter = new LocalTimeStringConverter(FormatStyle.SHORT);

					setText(task == null || empty ? ""
							: String.format("%s from %s to %s", task.getName(), converter.toString(task.getFrom()),
									converter.toString(task.getTo())));
				}
			};

			return cell;
		});

		// Auto fill if auto fill provided
		if (assignment != null) {
			// If the assignment is classroom assignment
			if (assignment.getCalendar().isClassroomLoaded()) {

				// Disable the deadlines
				datePicker.setDisable(true);
				dueTimeSpinner.setDisable(true);

			}

			// Auto fill items
			tasksTable.getItems().addAll(assignment.getSchedule());
		}

	}

	@Override
	public Reminder createReminder() {

		Assignment assignment = new Assignment();

		assignment.setDue(LocalDateTime.of(datePicker.getValue(), dueTimeSpinner.getValue()));
		assignment.setSchedule(new ArrayList<>(tasksTable.getItems()));

		return assignment;
	}

}

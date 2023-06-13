package controller.spec;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.Spinner;
import javafx.util.converter.LocalTimeStringConverter;
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
		tasksTable.setOnListAddRequestProperty(
				() -> EventSpecController.createReminderFrom(new RTask(), tasksTable.getItems()));

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

			datePicker.setValue(assignment.getDue().toLocalDate());
			dueTimeSpinner.getValueFactory().setValue(assignment.getDue().toLocalTime());

			// Auto fill items
			tasksTable.getItems().addAll(assignment.getSchedule());
		}

	}

	@Override
	public Reminder createReminder(List<String> errors) {

		Assignment assignment = new Assignment();

		assignment.setDue(LocalDateTime.of(datePicker.getValue(), dueTimeSpinner.getValue()));

		assignment.setSchedule(new ArrayList<>(tasksTable.getItems()));

		for (RTask task : assignment.getSchedule()) {
			task.setAssignment(assignment);
		}

		if (assignment.getDue().isBefore(LocalDateTime.now())) {
			errors.add("The Due date of the assignment cannot be before today");
		}

		if (assignment.getSchedule().isEmpty()) {
			errors.add("The task schedule of the assignment cannot be empty");
		}

		return assignment;
	}

}

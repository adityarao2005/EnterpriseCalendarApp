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
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import model.events.Assignment;
import model.events.RTask;
import model.events.Reminder;
import view.controls.ControlTable;

// Controls the assignment portion of the EventModalController
public class AssignmentSpecController implements EventSpecController {
	// Fields
	// FXML Loader Dependancy Injection
	@FXML
	private Spinner<LocalTime> dueTimeSpinner;

	@FXML
	private DatePicker datePicker;

	@FXML
	private ControlTable<RTask> tasksTable;

	// The assignment
	private Assignment assignment;

	// No arg constructor
	public AssignmentSpecController() {

	}

	// c-tor
	public AssignmentSpecController(Assignment assignment) {
		this.assignment = assignment;
	}

	// Initialize method
	@FXML
	private void initialize() {
		// Create a pop up dialog to create a task
		tasksTable.setOnListAddRequestProperty(
				() -> EventSpecController.createReminderFrom(new RTask(), tasksTable.getItems()));

		// Set cell factory
		tasksTable.setCellFactory(view -> {
			// Create a new list cell
			ListCell<RTask> cell = new ListCell<>() {
				@Override
				public void updateItem(RTask task, boolean empty) {
					
					// Call super implementation
					super.updateItem(task, empty);

					// Create the converters
					LocalTimeStringConverter converterT = new LocalTimeStringConverter(FormatStyle.SHORT);
					LocalDateStringConverter converterD = new LocalDateStringConverter(FormatStyle.MEDIUM);

					// Set the text
					setText(task == null || empty ? ""
							: String.format("%s from %s to %s on %s", task.getName(), converterT.toString(task.getFrom()),
									converterT.toString(task.getTo()), converterD.toString(task.getDate())));
				}
			};

			// return the list cell
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

			// Assign the values
			datePicker.setValue(assignment.getDue().toLocalDate());
			dueTimeSpinner.getValueFactory().setValue(assignment.getDue().toLocalTime());

			// Auto fill items
			tasksTable.getItems().addAll(assignment.getSchedule());
		}

	}

	// Create the reminders
	@Override
	public Reminder createReminder(List<String> errors) {

		// Create the assignment
		Assignment assignment = new Assignment();
		assignment.setDue(LocalDateTime.of(datePicker.getValue(), dueTimeSpinner.getValue()));
		assignment.setSchedule(new ArrayList<>(tasksTable.getItems()));

		// Set the assignment
		for (RTask task : assignment.getSchedule()) {
			task.setAssignment(assignment);
		}

		// Validation code
		if (assignment.getDue().isBefore(LocalDateTime.now())) {
			errors.add("The Due date of the assignment cannot be before today");
		}

		if (assignment.getSchedule().isEmpty()) {
			errors.add("The task schedule of the assignment cannot be empty");
		}

		// Return the assignment
		return assignment;
	}

}

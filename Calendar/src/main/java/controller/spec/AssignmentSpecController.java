package controller.spec;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
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

	@FXML
	private void initialize() {
		// Set the list add to a pop up of a task creation dialog
		tasksTable.onListAddRequestProperty();
		// Set cell factory to make a cell which shows a certain text
		tasksTable.cellFactoryProperty();

	}

	@Override
	public Reminder createReminder() {

		Assignment assignment = new Assignment();

		assignment.setDue(LocalDateTime.of(datePicker.getValue(), dueTimeSpinner.getValue()));
		assignment.setSchedule(new ArrayList<>(tasksTable.getItems()));

		return assignment;
	}

}

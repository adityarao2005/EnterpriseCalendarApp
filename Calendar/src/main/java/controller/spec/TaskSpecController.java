package controller.spec;

import java.time.LocalTime;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import model.events.RTask;
import model.events.Reminder;

public class TaskSpecController implements EventSpecController {
	@FXML
	private Spinner<LocalTime> startTimeSpinner;

	@FXML
	private Spinner<LocalTime> endTimeSpinner;

	@FXML
	private DatePicker datePicker;

	@Override
	public Reminder createReminder() {
		RTask task = new RTask();

		task.setFrom(startTimeSpinner.getValue());
		task.setTo(endTimeSpinner.getValue());
		task.setDate(datePicker.getValue());

		return task;
	}

}

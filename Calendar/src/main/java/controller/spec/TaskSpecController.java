package controller.spec;

import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.List;

import application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.util.converter.LocalTimeStringConverter;
import model.events.RCalendar;
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
	public Reminder createReminder(List<String> errors) {
		RTask task = new RTask();

		task.setFrom(startTimeSpinner.getValue());
		task.setTo(endTimeSpinner.getValue());
		task.setDate(datePicker.getValue());

		// TODO: FIX STUFF, make sure from < to and that no other tasks intersect with
		// each other
		if (!task.getFrom().isBefore(task.getTo())) {
			errors.add("The start time must be before the end time");
		}

		// Get the list of tasks
		List<RTask> tasks = Application.getApplication().getCurrentUser().getCalendars().stream()
				.map(RCalendar::getReminders).flatMap(List::stream).filter(e -> e instanceof RTask).map(e -> (RTask) e)
				.toList();

		// Loop through all the tasks and check for intersection
		for (RTask cdTask : tasks) {
			// say we have a set [a,b] and a set [c,d]
			// how do we say sets [a,b] and set [c,d] are disjoint
			// well the only way is if set [c,d] is lower or greater than set [a, b]
			// since a < b and c < d there are two cases
			// Case 1: b < c
			// a ----- b c ----- d
			// if b < c then both sets are disjoint
			// Case 2: d < a
			// c ----- d a ---- b
			// if d < a then both sets are disjoint
			// If the sets are not disjoint, we send an error
			if (!(task.getFrom().isAfter(cdTask.getTo()) || task.getTo().isBefore(cdTask.getFrom()))
					&& cdTask.getDate().equals(task.getDate())) {
				// Create a time string converter
				LocalTimeStringConverter converter = new LocalTimeStringConverter(FormatStyle.SHORT);

				// Add the error
				errors.add(String.format("The task %s with time [%s, %s] collides with the current time [%s, %s]",
						cdTask.getName(), converter.toString(cdTask.getFrom()), converter.toString(cdTask.getTo()),
						converter.toString(task.getFrom()), converter.toString(task.getTo())));

				// Break
				break;
			}
		}

		// Return the task
		return task;
	}

}

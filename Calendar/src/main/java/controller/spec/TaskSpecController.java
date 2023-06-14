package controller.spec;

import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.stream.Stream;

import application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.util.converter.LocalTimeStringConverter;
import model.events.Assignment;
import model.events.RCalendar;
import model.events.RTask;
import model.events.Reminder;

// Task Specialization - Controls the tasks
public class TaskSpecController implements EventSpecController {
	// Fields
	// FXML CDI dependancy injection
	@FXML
	private Spinner<LocalTime> startTimeSpinner;

	@FXML
	private Spinner<LocalTime> endTimeSpinner;

	@FXML
	private DatePicker datePicker;

	// Reminders and other tasks
	private RTask reminder;
	private List<RTask> otherTasks;

	// Constructors
	public TaskSpecController() {
	}

	public TaskSpecController(RTask reminder, List<RTask> otherTasks) {
		this.reminder = reminder;
		this.otherTasks = otherTasks;
	}

	// Initialize method
	@FXML
	private void initialize() {

		// If there is a reminder then set the values
		if (reminder != null) {
			startTimeSpinner.getValueFactory().setValue(reminder.getFrom());
			endTimeSpinner.getValueFactory().setValue(reminder.getTo());
			datePicker.setValue(reminder.getDate());
		}
	}

	// Create the task
	@Override
	public Reminder createReminder(List<String> errors) {
		// Create a task
		RTask task = new RTask();

		// Set values
		task.setFrom(startTimeSpinner.getValue());
		task.setTo(endTimeSpinner.getValue());
		task.setDate(datePicker.getValue());

		// Validation check
		if (!task.getFrom().isBefore(task.getTo())) {
			errors.add("The start time must be before the end time");
			return task;
		}

		// Get the total amount of reminders
		List<Reminder> totalReminders = Application.getApplication().getCurrentUser().getCalendars().stream()
				.map(RCalendar::getReminders).flatMap(List::stream).toList();

		// Get the list of tasks
		List<RTask> tasks = Stream.concat(
				// Create a tasks list
				totalReminders.stream().filter(e -> e instanceof Assignment).map(e -> ((Assignment) e).getSchedule())
						.flatMap(List::stream),
				Stream.concat(totalReminders.stream().filter(e -> e instanceof RTask).map(e -> (RTask) e),
						otherTasks.stream()))
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

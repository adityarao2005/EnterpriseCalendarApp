package controller.spec;

import java.util.List;

import application.Application;
import controller.EventModalController;
import model.events.Reminder;

// Interface for all Event Specializations
public interface EventSpecController {

	// Creates the reminder
	public Reminder createReminder(List<String> errors);

	// Utility method to create a task
	public static <T extends Reminder> T createReminderFrom(T template, Object... otherData) {
		// Create an event dialog
		return Application.getApplication().dialog("/view/EventModalView.fxml", "Create Event", clazz -> {
			// Create a new Event modal controller of task
			if (clazz == EventModalController.class) {
				return new EventModalController(template, otherData);
			}

			// Create a regular instance
			try {
				return clazz.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});

	}

	// Utility method to create a task
	public static Reminder createReminderFrom() {
		// Create an event dialog
		return Application.getApplication().dialog("/view/EventModalView.fxml", "Create Event");

	}

}

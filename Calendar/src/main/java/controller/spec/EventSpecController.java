package controller.spec;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Collectors;

import javax.management.RuntimeErrorException;

import application.Application;
import controller.EventModalController;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.events.Assignment;
import model.events.Reminder;

public interface EventSpecController {

	public Reminder createReminder();

	// Utility method to create a task
	public static <T extends Reminder> T createReminderFrom(T template) {
		// Create an event dialog
		return Application.getApplication().dialog("/view/EventModalView.fxml", "Create Event", clazz -> {
			// Create a new Event modal controller of task
			if (clazz == EventModalController.class) {
				return new EventModalController(template);
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

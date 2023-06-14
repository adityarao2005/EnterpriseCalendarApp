package controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import application.Application;
import controller.spec.AssignmentSpecController;
import controller.spec.EventSpecController;
import controller.spec.ReminderSpecController;
import controller.spec.TaskSpecController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.events.Assignment;
import model.events.RCalendar;
import model.events.REvent;
import model.events.RTask;
import model.events.Reminder;

// Represents a modal for receiving an event from a form - Rendered in OOP structure in both here and FXML
public class EventModalController implements DialogController<Reminder>, Initializable {
	// Fields
	// Loaded using Dependancy Injection
	@FXML
	private ComboBox<String> typeCombo;
	@FXML
	private ComboBox<String> calendarCombo;
	@FXML
	private TextField nameField;
	@FXML
	private Spinner<LocalTime> reminderSpinner;
	@FXML
	private BorderPane eventSpecBox;
	private EventSpecController controller;

	// Data for the sub controllers
	private Reminder reminder;
	private Object[] otherData;

	// Constructor
	public EventModalController() {
	}

	// Overloaded constructor
	public EventModalController(Reminder reminder, Object... otherData) {
		this.reminder = reminder;
		this.otherData = otherData;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		calendarCombo.setItems(FXCollections.observableList(Application.getApplication().getCurrentUser().getCalendars()
				.stream().filter(Predicate.not(RCalendar::isClassroomLoaded)).map(RCalendar::getName).toList()));

		// Add a selection listener
		typeCombo.getSelectionModel().selectedItemProperty().addListener(l -> selectedType());

		calendarCombo.setValue(null);

		// Auto Fill the values and disable some other values
		if (reminder != null) {
			// Set value
			if (reminder.getCalendar() != null)
				calendarCombo.getSelectionModel().select(reminder.getCalendar().getName());

			// If it is an assignment
			if (reminder instanceof Assignment) {

				typeCombo.getSelectionModel().select("ASSIGNMENT");

				// If it is classroom loaded, disable the name editing
				if (((Assignment) reminder).getCalendar().isClassroomLoaded()) {
					nameField.setEditable(false);
				}

				// If it is a task, select task
			} else if (reminder instanceof RTask) {
				typeCombo.getSelectionModel().select("TASK");
			} else {
				// If it is a reminder, select reminder
				typeCombo.getSelectionModel().select("REMINDER");
			}

			// Auto fill name and reminder time
			if (reminder.getName() != null)
				nameField.setText(reminder.getName());
			
			if (reminder.getReminder() != null)
				reminderSpinner.getValueFactory().setValue(reminder.getReminder());

			// Disable type and calendars
			typeCombo.setDisable(true);
			calendarCombo.setDisable(true);

		}
	}

	// Return the reminder object
	@Override
	public Reminder getResult() {

		// Create a list of errors
		List<String> errors = new ArrayList<>();

		// Create the reminder
		Reminder reminder = controller.createReminder(errors);

		// Set the calendar
		reminder.setCalendar(Application.getApplication().getCurrentUser().getCalendars().stream()
				.filter(e -> e.getName().equals(calendarCombo.getSelectionModel().getSelectedItem())).findFirst()
				.orElse(null));

		// Set the calendar
		reminder.setName(nameField.getText());

		// If the name is null or the name is empty
		if (reminder.getName() == null ||reminder.getName().trim().isEmpty())
			errors.add("Name cannot be empty");

		// Set the reminder
		reminder.setReminder(reminderSpinner.getValue());

		// If the reminder
		if (reminder.getReminder() == null)
			errors.add("Reminder Time cannot be null/empty");

		// If there are errors, return null and set errors
		if (errors.size() > 0) {
			Application.getApplication().setErrors(errors);
			return null;
		}

		// Return the reminder
		return reminder;
	}

	// When the combobox is clicked and a new item is selected, run this
	@SuppressWarnings("unchecked")
	@FXML
	private void selectedType() {

		// Load the rest of the event modals
		FXMLLoader loader = new FXMLLoader();
		loader.setRoot(new VBox());

		// Go through the selected item of the combobox
		switch (typeCombo.getSelectionModel().getSelectedItem()) {
		// If its reminder, set the location of the view and add controller
		case "REMINDER": {
			loader.setLocation(this.getClass().getResource("/view/event-modal/ReminderModalView.fxml"));

			ReminderSpecController spec = new ReminderSpecController();

			if (reminder != null) {
				spec = new ReminderSpecController((REvent) reminder);
			}

			loader.setController(spec);

			break;
		}
		// If its task, set location of view and add controller (and add other data required)
		case "TASK": {
			loader.setLocation(this.getClass().getResource("/view/event-modal/TaskModalView.fxml"));

			TaskSpecController spec = new TaskSpecController();

			if (reminder != null) {
				spec = new TaskSpecController((RTask) reminder, (List<RTask>) otherData[0]);
			}

			loader.setController(spec);

			break;
		}
		// If its assignment, then set location of view and add controller
		case "ASSIGNMENT": {
			loader.setLocation(this.getClass().getResource("/view/event-modal/AssignmentModalView.fxml"));

			AssignmentSpecController spec = new AssignmentSpecController();

			if (reminder != null) {
				spec = new AssignmentSpecController((Assignment) reminder);
			}

			loader.setController(spec);

			break;
		}
		}

		// Set the center of the borderpane as whatever is loaded from the fxml
		try {
			eventSpecBox.setCenter(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Get the controller
		controller = loader.getController();
	}

}

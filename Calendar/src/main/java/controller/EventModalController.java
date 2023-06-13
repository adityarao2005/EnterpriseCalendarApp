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

public class EventModalController implements DialogController<Reminder>, Initializable {
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

	private Reminder reminder;
	private Object[] otherData;

	public EventModalController() {
	}

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
			nameField.setText(reminder.getName());
			reminderSpinner.getValueFactory().setValue(reminder.getReminder());

			// Disable type and calendars
			typeCombo.setDisable(true);
			calendarCombo.setDisable(true);

		}
	}

	@Override
	public Reminder getResult() {

		List<String> errors = new ArrayList<>();

		Reminder reminder = controller.createReminder(errors);

		// Set the calendar
		reminder.setCalendar(Application.getApplication().getCurrentUser().getCalendars().stream()
				.filter(e -> e.getName().equals(calendarCombo.getSelectionModel().getSelectedItem())).findFirst()
				.orElse(null));

		// Set the calendar
		reminder.setName(nameField.getText());

		if (reminder.getName().trim().isEmpty())
			errors.add("Name cannot be empty");

		// Set the reminder
		reminder.setReminder(reminderSpinner.getValue());

		if (reminder.getReminder() == null)
			errors.add("Reminder Time cannot be null/empty");

		// If there are errors, return null and set errors
		if (errors.size() > 0) {
			Application.getApplication().setErrors(errors);
			return null;
		}

		return reminder;
	}

	@SuppressWarnings("unchecked")
	@FXML
	private void selectedType() {

		FXMLLoader loader = new FXMLLoader();
		loader.setRoot(new VBox());

		switch (typeCombo.getSelectionModel().getSelectedItem()) {
		case "REMINDER": {
			loader.setLocation(this.getClass().getResource("/view/event-modal/ReminderModalView.fxml"));

			ReminderSpecController spec = new ReminderSpecController();

			if (reminder != null) {
				spec = new ReminderSpecController((REvent) reminder);
			}

			loader.setController(spec);

			break;
		}
		case "TASK": {
			loader.setLocation(this.getClass().getResource("/view/event-modal/TaskModalView.fxml"));

			TaskSpecController spec = new TaskSpecController();

			if (reminder != null) {
				spec = new TaskSpecController((RTask) reminder, (List<RTask>) otherData[0]);
			}

			loader.setController(spec);

			break;
		}
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

		try {
			eventSpecBox.setCenter(loader.load());
		} catch (IOException e) {
			e.printStackTrace();
		}

		controller = loader.getController();
	}

}

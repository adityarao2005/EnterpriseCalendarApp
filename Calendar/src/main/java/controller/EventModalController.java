package controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import application.Application;
import controller.spec.EventSpecController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.events.RCalendar;
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
	private VBox eventSpecBox;
	private EventSpecController controller;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		calendarCombo.setItems(FXCollections.observableList(Application.getApplication().getCurrentUser().getCalendars()
				.stream().filter(Predicate.not(RCalendar::isClassroomLoaded)).map(RCalendar::getName).toList()));

		selectedType();
	}

	@Override
	public Reminder getResult() {

		Reminder reminder = controller.createReminder();

		reminder.setCalendar(Application.getApplication().getCurrentUser().getCalendars().stream()
				.filter(e -> e.getName().equals(calendarCombo.getSelectionModel().getSelectedItem())).findFirst()
				.get());

		reminder.setName(nameField.getText());
		reminder.setReminder(reminderSpinner.getValue());

		return reminder;
	}

	@FXML
	private void selectedType() {
		FXMLLoader loader = new FXMLLoader();
		loader.setRoot(eventSpecBox);

		switch (typeCombo.getSelectionModel().getSelectedItem()) {
		case "REMINDER":
			loader.setLocation(this.getClass().getResource("/view/event-modal/ReminderModalView.fxml"));

			break;
		case "TASK":
			loader.setLocation(this.getClass().getResource("/view/event-modal/TaskModalView.fxml"));

			break;
		case "ASSIGNMENT":
			loader.setLocation(this.getClass().getResource("/view/event-modal/AssignmentModalView.fxml"));

			break;
		}

		try {
			loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		controller = loader.getController();
	}
}

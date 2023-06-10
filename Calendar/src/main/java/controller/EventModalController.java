package controller;

import java.net.URL;
import java.time.LocalTime;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class EventModalController<Reminder> implements DialogController<Reminder>, Initializable {
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
		
	}

	@Override
	public Reminder getResult() {

		return null;
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
			loader.setLocation(this.getClass().getResource("/view/event-modal/ReminderModalView.fxml"));
			
			break;
		case "ASSIGNMENT":
			loader.setLocation(this.getClass().getResource("/view/event-modal/ReminderModalView.fxml"));

			break;
		}
	}
}

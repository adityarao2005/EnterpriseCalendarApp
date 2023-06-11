package controller.spec;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.Month;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import model.events.REvent;
import model.events.Reminder;
import model.occurrence.DailyOccurrence;
import model.occurrence.MonthlyOccurrence;
import model.occurrence.OnlyOccurrence;
import model.occurrence.WeeklyOccurrence;
import model.occurrence.YearlyOccurrence;
import view.controls.EnumCheckBoxCallback;

public class ReminderSpecController implements EventSpecController {
	// FX DEFINES
	// Daily occurrence tab
	@FXML
	private HBox dailyOcc;

	// Weekly Occurrence tab
	@FXML
	private HBox weeklyOcc;
	@FXML
	private EnumCheckBoxCallback<DayOfWeek> weeklyList;

	// Monthly occurrence tab
	@FXML
	private HBox monthlyOcc;
	@FXML
	private Spinner<Integer> daysSpinnerM;

	// Yearly occurrence tab
	@FXML
	private HBox yearlyOcc;
	@FXML
	private Spinner<Integer> daysSpinnerY;
	@FXML
	private ComboBox<Month> monthCombo;

	// Only Occurrence tab
	@FXML
	private HBox onlyOcc;
	@FXML
	private DatePicker datePicker;

	// REG CONTROLS
	@FXML
	private Spinner<LocalTime> timeSpinner;
	@FXML
	private ComboBox<String> frequencyCombo;
	@FXML
	private BorderPane container;

	@FXML
	private void initialize() {
		selectFreq();
	}

	@FXML
	private void selectFreq() {
		// Get the right hbox
		HBox hbox = (switch (frequencyCombo.getSelectionModel().getSelectedItem()) {
		case "DAILY" -> dailyOcc;
		case "WEEKLY" -> weeklyOcc;
		case "MONTHLY" -> monthlyOcc;
		case "YEARLY" -> yearlyOcc;
		case "ONLY" -> onlyOcc;
		default -> throw new IllegalArgumentException(
				"Unexpected value: " + frequencyCombo.getSelectionModel().getSelectedItem());
		});

		// Remove the old one and add the new one
		container.getChildren().remove(container.getCenter());
		container.setCenter(hbox);
	}

	@Override
	public Reminder createReminder() {

		REvent event = new REvent();

		event.setOccurence(switch (frequencyCombo.getSelectionModel().getSelectedItem()) {
		case "DAILY" -> new DailyOccurrence(timeSpinner.getValue());
		case "WEEKLY" -> new WeeklyOccurrence(timeSpinner.getValue(), weeklyList.getEnums());
		case "MONTHLY" -> new MonthlyOccurrence(timeSpinner.getValue(), daysSpinnerM.getValue());
		case "YEARLY" -> new YearlyOccurrence(timeSpinner.getValue(), daysSpinnerY.getValue(),
				monthCombo.getSelectionModel().getSelectedItem());
		case "ONLY" -> new OnlyOccurrence(timeSpinner.getValue(), datePicker.getValue());
		default -> throw new IllegalArgumentException(
				"Unexpected value: " + frequencyCombo.getSelectionModel().getSelectedItem());
		});

		return event;
	}

}

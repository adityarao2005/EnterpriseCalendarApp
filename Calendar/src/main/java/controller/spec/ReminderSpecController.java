package controller.spec;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.YearMonth;
import java.util.EnumSet;
import java.util.List;

import javafx.beans.property.BooleanProperty;
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

// Reminder specialization - Controls reminder portion of the EventModalController
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

	// Reminder
	private REvent reminder;

	// Constructors
	public ReminderSpecController() {
	}

	public ReminderSpecController(REvent reminder) {
		this.reminder = reminder;
	}

	// Initialize method
	@FXML
	private void initialize() {
		// Select the first value of the combobox
		frequencyCombo.getSelectionModel().select(0);

		// Call select Frequency
		selectFreq();

		// Auto fill event if event is provided
		if (reminder != null) {
			// Auto fill values
			timeSpinner.getValueFactory().setValue(reminder.getOccurence().getTime());

			// Based on the occurrence of the event, auto fill the values and select the
			// proper value
			if (reminder.getOccurence() instanceof DailyOccurrence) {
				// If it is daily select daily
				frequencyCombo.getSelectionModel().select("DAILY");

			} else if (reminder.getOccurence() instanceof WeeklyOccurrence) {
				// if it is weekly select weekly
				frequencyCombo.getSelectionModel().select("WEEKLY");

				// Set the values of the enum set
				for (DayOfWeek dayOfWeek : EnumSet.allOf(DayOfWeek.class)) {
					// Set the values manually
					((BooleanProperty) weeklyList.call(dayOfWeek))
							.set(((WeeklyOccurrence) reminder.getOccurence()).getDaysOfWeek().contains(dayOfWeek));
				}

			} else if (reminder.getOccurence() instanceof MonthlyOccurrence) {
				// If it is monthly select monthly
				frequencyCombo.getSelectionModel().select("MONTHLY");

				// Set the day value
				daysSpinnerM.getValueFactory().setValue(((MonthlyOccurrence) reminder.getOccurence()).getDayOfMonth());

			} else if (reminder.getOccurence() instanceof YearlyOccurrence) {
				// If it is yearly set yearly
				frequencyCombo.getSelectionModel().select("YEARLY");

				// Set the month and day value
				daysSpinnerY.getValueFactory().setValue(((YearlyOccurrence) reminder.getOccurence()).getDayOfMonth());
				monthCombo.getSelectionModel().select((((YearlyOccurrence) reminder.getOccurence()).getMonth()));
			} else {
				// If it is only set only
				frequencyCombo.getSelectionModel().select("ONLY");

				// Set the date value
				datePicker.setValue(((OnlyOccurrence) reminder.getOccurence()).getDate());
			}

			// Disable the combobox
			frequencyCombo.setDisable(true);
		}
	}

	// Sets the base for the occurrence
	@FXML
	private void selectFreq() {
		// Get the right hbox
		HBox hbox = (switch (frequencyCombo.getValue()) {
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

	// Creates the reminder
	@Override
	public Reminder createReminder(List<String> errors) {

		// Declare and initialize
		REvent event = new REvent();

		// Based on the occurrence type, set the occurrence
		event.setOccurence(switch (frequencyCombo.getSelectionModel().getSelectedItem()) {
		case "DAILY" -> new DailyOccurrence(timeSpinner.getValue());
		case "WEEKLY" -> {
			if (weeklyList.getEnums().isEmpty())
				errors.add("There have to be days chosen");

			yield new WeeklyOccurrence(timeSpinner.getValue(), weeklyList.getEnums());
		}
		case "MONTHLY" -> {
			if (daysSpinnerM.getValue() < 1 || daysSpinnerM.getValue() > 31)
				errors.add("The maximum number of days in a month is 31 and the minimum day is 1");

			yield new MonthlyOccurrence(timeSpinner.getValue(), daysSpinnerM.getValue());
		}
		case "YEARLY" -> {

			if (YearMonth.of(2000, monthCombo.getValue().getValue()).lengthOfMonth() < daysSpinnerY.getValue()
					|| daysSpinnerY.getValue() < 0)
				errors.add(String.format("The maximum number of days in %s is %d and the minimum day is 1",
						monthCombo.getValue(), YearMonth.of(2000, monthCombo.getValue().getValue()).lengthOfMonth()));

			yield new YearlyOccurrence(timeSpinner.getValue(), daysSpinnerY.getValue(),
					monthCombo.getSelectionModel().getSelectedItem());
		}
		case "ONLY" -> {
			if (!LocalDateTime.of(datePicker.getValue(), timeSpinner.getValue()).isAfter(LocalDateTime.now()))
				errors.add("The occurrence of this event must occur after right now");

			yield new OnlyOccurrence(timeSpinner.getValue(), datePicker.getValue());
		}
		default -> throw new IllegalArgumentException(
				"Unexpected value: " + frequencyCombo.getSelectionModel().getSelectedItem());
		});

		// Return occurrence
		return event;
	}

}

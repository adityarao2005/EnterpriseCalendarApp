
package view.controls.skin;

import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ResourceBundle;

import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import view.controls.UCalendar;

// The Default Skin of the Calendar object
public class UCalendarSkin extends FXRootSkinBase<UCalendar, BorderPane> {

	// Fields
	// FXML Dependancy Injectioon
	@FXML
	private GridPane calendarGrid;
	@FXML
	private Button monthLeft;
	@FXML
	private Label month;
	@FXML
	private Button monthRight;
	@FXML
	private Button yearLeft;
	@FXML
	private Label year;
	@FXML
	private Button yearRight;

	// Buttons array
	private Button[][] buttons;
	private SimpleObjectProperty<YearMonth> yearMonth;

	// Constructor to load view
	public UCalendarSkin(UCalendar calendar) {
		super(calendar, UCalendarSkin.class.getResource("/view/controls/CalendarView.fxml"), BorderPane::new);
	}

	// Initializes the controller class.
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		// Yearmonth property
		yearMonth = new SimpleObjectProperty<>();
		// anchor pane for each button row and column
		buttons = new Button[6][7];
		// for each of the "button containers" add a reference of it in the
		// multidimensional array
		for (int row = 0; row < buttons.length; row++) {
			for (int column = 0; column < buttons[row].length; column++) {
				// Creates an anchorpane
				AnchorPane anchor = new AnchorPane();

				// Hold the buttons in the anchor pane
				buttons[row][column] = new Button();
				// Set layout args
				AnchorPane.setBottomAnchor(buttons[row][column], 0.0);
				AnchorPane.setTopAnchor(buttons[row][column], 0.0);
				AnchorPane.setLeftAnchor(buttons[row][column], 0.0);
				AnchorPane.setRightAnchor(buttons[row][column], 0.0);

				// Set action
				buttons[row][column].setOnAction(e -> {
					Button button = (Button) e.getTarget();
					this.getSkinnable().setCurrentDate((LocalDate) button.getUserData());
					this.getSkinnable().fireEvent(new ActionEvent(button, this.getSkinnable()));
				});

				// Add buttons to anchorpane and add anchorpane to grid
				anchor.getChildren().add(buttons[row][column]);
				calendarGrid.add(anchor, column, row + 2);
			}
		}

		// Set the yearMonth as the current yearmonth
		yearMonth.set(YearMonth.from(LocalDate.now()));

		// Add listener to the yearmonth
		yearMonth.addListener(this::refreshBasedOnYearMonth);

		// Add listener to the current date property
		this.getSkinnable().currentDateProperty().addListener(l -> {
			yearMonth.set(YearMonth.from(this.getSkinnable().getCurrentDate()));
		});
		// Set the current date as now
		this.getSkinnable().setCurrentDate(LocalDate.now());
	}

	// Utility method, can be used for anything but kept here for it's own purpose
	public static String toCamelCase(final String init) {
		if (init == null) {
			return null;
		}

		final StringBuilder ret = new StringBuilder(init.length());

		// make the words camel case by splitting it by every space and set the word in
		// uppercase
		for (final String word : init.split("\\s")) {
			if (!word.trim().isEmpty()) {
				ret.append(Character.toUpperCase(word.charAt(0)));
				ret.append(word.substring(1).toLowerCase());
			}
			if (!(ret.length() == init.length())) {
				ret.append(" ");
			}
		}

		return ret.toString();
	}

	// Refreshes the month and year
	private void refreshBasedOnYearMonth(Object l) {
		// Get the year month
		YearMonth month = yearMonth.get();

		// Get the first day of the month
		LocalDate firstDay = month.atDay(1);

		// Get the week of the first day
		DayOfWeek dayOfWeek = firstDay.getDayOfWeek();

		// Get the first day of the week
		LocalDate curr = firstDay.minusDays(fromDayOfTheWeek(dayOfWeek));

		year.setText(String.valueOf(month.getYear()));
		this.month.setText(month.getMonth().toString());

		// Go through the loop
		for (int row = 0; row < buttons.length; row++) {
			for (int column = 0; column < buttons[row].length; column++) {
				// Set the value of the button
				Button button = buttons[row][column];

				// Set the value of the button
				button.setText(String.valueOf(curr.getDayOfMonth()));

				// Set the value of the button
				button.setUserData(curr);

				// Make these buttons appear as if they aren't in this month
				if (curr.isBefore(firstDay)) {
					button.getStyleClass().add("-fx-calendar-cell-before");
				} else if (curr.isAfter(month.atEndOfMonth())) {
					button.getStyleClass().add("-fx-calendar-cell-after");
				} else {

					// Remove the other styles
					button.getStyleClass().removeAll("-fx-calendar-cell-before", "-fx-calendar-cell-after",
							"-fx-calendar-cell-selected");
				}

				// If this is the correct month
				if (curr.equals(this.getSkinnable().getCurrentDate())) {
					button.getStyleClass().add("-fx-calendar-cell-selected");
				}

				curr = curr.plusDays(1);
			}
		}
	}

	// DayOfWeek enum to Canadian/US/European standard
	private int fromDayOfTheWeek(DayOfWeek week) {
		return week.getValue() % 7;
	}

	// Action when month buttons are pressed
	@FXML
	private void onMonthPressed(ActionEvent event) {
		// month presses
		if (event.getTarget() == monthLeft) {
			yearMonth.set(yearMonth.get().minusMonths(1));
		} else if (event.getTarget() == monthRight) {
			yearMonth.set(yearMonth.get().plusMonths(1));
		}
	}

	// Action when year buttons are pressed
	@FXML
	private void onYearPressed(ActionEvent event) {
		// month presses
		if (event.getTarget() == yearLeft) {
			yearMonth.set(yearMonth.get().minusYears(1));
		} else if (event.getTarget() == yearRight) {
			yearMonth.set(yearMonth.get().plusYears(1));
		}
	}

}
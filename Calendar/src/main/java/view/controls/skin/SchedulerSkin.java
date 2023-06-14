
package view.controls.skin;

import view.controls.Scheduler;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.converter.LocalTimeStringConverter;
import model.events.RTask;
import model.events.Reminder;

// Skin for the Scheduler
public class SchedulerSkin extends FXRootSkinBase<Scheduler, ScrollPane> implements Initializable {

	// FXML dependancy injection
	@FXML
	private VBox scheduleContainer;

	// Houses list view based on time
	private HashMap<LocalTime, ListView<Reminder>> events;

	// Constructor
	public SchedulerSkin(Scheduler calendar) {
		super(calendar, SchedulerSkin.class.getResource("/view/controls/ScheduleView.fxml"), ScrollPane::new);
	}

	// Initialize the method
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Creates the events map
		events = new HashMap<>();

		// Go through all 24 hours and add a list view for the time
		for (int i = 0; i < 24; i++) {
			// Get the time from the index
			LocalTime time = LocalTime.of(i, 0);

			// Create a converter
			LocalTimeStringConverter converter = new LocalTimeStringConverter(FormatStyle.SHORT);

			// Load fxml
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getResource("/view/controls/TimeEvents.fxml"));
			BorderPane bpContainer = null;
			try {
				bpContainer = loader.load();
			} catch (IOException e) {
				e.printStackTrace();
				bpContainer = new BorderPane();
			}

			// Get the labels and list view and set the values
			Label label = ((Label) loader.getNamespace().get("timeLabel"));
			label.setText(converter.toString(time));

			events.put(time, (ListView<Reminder>) loader.getNamespace().get("listView"));
			events.get(time).setCellFactory(v -> new TaskCell());

			// Add the border pane to the schedule container
			scheduleContainer.getChildren().add(bpContainer);
		}

		// Create a change listener
		InvalidationListener changeListener = l -> {
			// Clear the reminders in the list
			for (ListView<Reminder> reminders : events.values()) {
				reminders.getItems().clear();
			}

			// Go through all the reminders
			for (Reminder reminder : this.getSkinnable().getReminder()) {
				// Trunticate it to hours and add it in its respective listview
				LocalTime time = reminder.appearsAt();

				LocalTime key = time.truncatedTo(ChronoUnit.HOURS);

				events.get(key).getItems().add(reminder);

			}

			// Sort the reminders
			for (ListView<Reminder> reminders : events.values()) {
				reminders.getItems().sort(Comparator.comparing(Reminder::appearsAt));
			}
		};

		// Add the listeners
		this.getSkinnable().getReminder().addListener(changeListener);
		this.getSkinnable().reminderProperty().addListener(changeListener);

	}

	// Get the events
	public HashMap<LocalTime, ListView<Reminder>> getEvents() {
		return events;
	}

	// Create the task cell for the listview
	public static class TaskCell extends ListCell<Reminder> {
		// Create a container to hold the tasks
		private HBox hbox = new HBox();
		// Dependancy injection
		@FXML
		private Label nameLabel;
		@FXML
		private Label timeLabel;
		// Create the color property
		private ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.LIGHTBLUE);

		// Constructor
		public TaskCell() {
			// Load the fxml
			FXMLLoader loader = new FXMLLoader();

			// Set the root and controller as this
			loader.setRoot(hbox);
			loader.setLocation(this.getClass().getResource("/view/controls/TaskView.fxml"));
			loader.setController(this);

			try {
				loader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			// Set style to override colour when colour changes
			colorProperty.addListener(
					(obs, old, newv) -> this.setStyle(String.format("-fx-background-color: rgb(%d, %d, %d)",
							(int) (newv.getRed() * 255), (int) (newv.getGreen() * 255), (int) (newv.getBlue() * 255))));
		}

		// When item updates run this method
		@Override
		public void updateItem(Reminder item, boolean empty) {
			// Call the super method
			super.updateItem(item, empty);

			// Set the texts
			setText("");

			// Set graphic is null if the item is empty
			if (item == null || empty) {
				setGraphic(null);
			} else {

				// Get the colour of the task if its parent is an assignment
				if (item instanceof RTask && ((RTask) item).getCalendar() == null) {
					colorProperty.set(((RTask) item).getAssignment().getCalendar().getColor());
				} else {
					// Otherwise set it as the calendars colour
					colorProperty.set(item.getCalendar().getColor());
				}

				// Show the task view
				nameLabel.setText(item.getName());
				timeLabel.setText(new LocalTimeStringConverter(FormatStyle.SHORT).toString(item.appearsAt()));

				setGraphic(hbox);
			}

		}
	}

}
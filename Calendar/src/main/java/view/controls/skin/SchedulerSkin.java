
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

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.converter.LocalTimeStringConverter;
import model.events.Reminder;

public class SchedulerSkin extends FXRootSkinBase<Scheduler, ScrollPane> implements Initializable {

	@FXML
	private VBox scheduleContainer;

	private HashMap<LocalTime, ListView<Reminder>> events;

	public SchedulerSkin(Scheduler calendar) {
		super(calendar, SchedulerSkin.class.getResource("/view/controls/ScheduleView.fxml"), ScrollPane::new);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		events = new HashMap<>();

		for (int i = 0; i < 24; i++) {
			LocalTime time = LocalTime.of(i, 0);

			LocalTimeStringConverter converter = new LocalTimeStringConverter(FormatStyle.SHORT);

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(this.getClass().getResource("/view/controls/TimeEvents.fxml"));
			BorderPane bpContainer = null;
			try {
				bpContainer = loader.load();
			} catch (IOException e) {
				e.printStackTrace();
				bpContainer = new BorderPane();
			}

			Label label = ((Label) loader.getNamespace().get("timeLabel"));
			label.setText(converter.toString(time));

			events.put(time, (ListView<Reminder>) loader.getNamespace().get("listView"));

			events.get(time).setCellFactory(v -> new TaskCell());

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

		//

	}

	public HashMap<LocalTime, ListView<Reminder>> getEvents() {
		return events;
	}

	public static class TaskCell extends ListCell<Reminder> {
		private HBox hbox = new HBox();
		@FXML
		private Label nameLabel;
		@FXML
		private Label timeLabel;

		public TaskCell() {
			FXMLLoader loader = new FXMLLoader();

			loader.setRoot(hbox);
			loader.setLocation(this.getClass().getResource("/view/controls/TaskView.fxml"));
			loader.setController(this);

			try {
				loader.load();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public void updateItem(Reminder item, boolean empty) {
			super.updateItem(item, empty);
			setText("");

			if (item == null || empty) {
				setGraphic(null);
			} else {

				// Show the task view
				nameLabel.setText(item.getName());
				timeLabel.setText(new LocalTimeStringConverter(FormatStyle.SHORT).toString(item.appearsAt()));

				setGraphic(hbox);
			}

		}
	}

}
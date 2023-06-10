package view.controls;

import java.io.IOException;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.converter.LocalTimeStringConverter;
import model.events.Reminder;

public class ScheduleViewController implements Initializable {
	@FXML
	private VBox scheduleContainer;

	private HashMap<LocalTime, ListView<Reminder>> events;

	public ScheduleViewController() {
		events = new HashMap<>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {

		for (int i = 0; i < 48; i++) {
			LocalTime time = LocalTime.of(i / 2, 30 * (i % 2));

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

			scheduleContainer.getChildren().add(bpContainer);
		}
	}

	public HashMap<LocalTime, ListView<Reminder>> getEvents() {
		return events;
	}

}

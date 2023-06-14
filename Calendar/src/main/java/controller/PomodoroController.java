package controller;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.Application;
import controller.dao.CalendarManifestController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.events.RTask;

public class PomodoroController implements DialogController<String> {
	@FXML
	private AnchorPane finishedScreen;

	@FXML
	private AnchorPane workingScreen;

	@FXML
	private Label timeLabel;

	@FXML
	private BorderPane container;

	@FXML
	private Spinner<Integer> workDuration;

	@FXML
	private Spinner<Integer> breakDuration;

	@FXML
	private DialogPane dialogPane;

	@FXML
	private ButtonType closeButton;

	@FXML
	private ListView<RTask> pendingList;
	@FXML
	private ListView<RTask> completedList;

	@FXML
	private Label titleLabel;

	private Node closeButtonNode;

	private boolean working;
	private int workDurationTime;
	private int breakDurationTime;
	private ObservableList<RTask> tasks;
	private ObjectProperty<RTask> currentTask = new SimpleObjectProperty<>();
	private ObjectProperty<Duration> durationProperty = new SimpleObjectProperty<>(Duration.ZERO);
	private ObjectProperty<Duration> elapsedDuration = new SimpleObjectProperty<>(Duration.ZERO);

	private Timeline timer;

	@FXML
	private void initialize() {
		closeButtonNode = dialogPane.lookupButton(closeButton);

		tasks = FXCollections.observableList(
				CalendarManifestController.getUncompletedTasks(Application.getApplication().getCurrentUser()));
		pendingList.setItems(tasks);

		Callback<ListView<RTask>, ListCell<RTask>> cellFactory = lv -> {
			return new ListCell<>() {

				@Override
				public void updateItem(RTask item, boolean empty) {
					super.updateItem(item, empty);

					if (item == null || empty) {
						setText("");
					} else {

						String value = null;

						if (item.getAssignment() != null)
							value = item.getName() + " - " + item.getAssignment().getName();
						else
							value = item.getName();

						setText(value);
					}
				}
			};
		};

		pendingList.setCellFactory(cellFactory);
		completedList.setCellFactory(cellFactory);

		timer = new Timeline(new KeyFrame(javafx.util.Duration.seconds(1), event -> updateTime()));
		timer.setCycleCount(Animation.INDEFINITE);
	}

	@FXML
	private void stopAction() {
		timer.stop();

		closeButtonNode.fireEvent(new ActionEvent(closeButtonNode, closeButtonNode));
	}

	@FXML
	private void skipAction() {
		timer.stop();

		if (working) {
			// Get the next task if available
			currentTask.set(tasks.get(1));
		} else {
			working = true;

		}

		performNextAction();

	}

	@FXML
	private void finishAction() {

		// Stop the timer
		timer.stop();

		if (working) {
			working = false;

			// Remove the task from one list and add to the other
			tasks.remove(currentTask.get());
			currentTask.get().setCompleted(true);
			completedList.getItems().add(currentTask.get());

		} else {

			working = true;
		}

		// Perform the next action
		performNextAction();
	}

	@FXML
	private void onBegin() {
		closeButtonNode.setDisable(true);

		container.setCenter(workingScreen);

		working = true;
		workDurationTime = workDuration.getValue();
		breakDurationTime = breakDuration.getValue();

		Bindings.bindBidirectional(timeLabel.textProperty(), durationProperty, new StringConverter<>() {

			@Override
			public String toString(Duration object) {
				return String.format("%02d:%02d", object.toMinutesPart(), object.toSecondsPart());
			}

			@Override
			public Duration fromString(String string) {
				// Get the duration from string
				int minute = 0;
				int second = 0;

				Matcher m = Pattern.compile("([0-9]*)[:]([0-9]*)").matcher(string);

				m.find();
				minute = Integer.parseInt(m.group(0));
				second = Integer.parseInt(m.group(1));

				return Duration.ofSeconds(minute * 60 + second);
			}
		});

		// Add a listener
		currentTask.addListener(
				l -> elapsedDuration.set(Duration.between(currentTask.get().getFrom(), currentTask.get().getTo())));

		// Get the first task and set as current task
		currentTask.set(tasks.get(0));

		performNextAction();
	}

	private void performNextAction() {
		// If we are working
		if (working) {
			// Get the values of the duration
			durationProperty.set(PomodoroController.min(Duration.ofMinutes(workDurationTime), elapsedDuration.get()));

			titleLabel.setText("Keep working! You are doing great!");

		} else {
			// Otherwise set the break time
			durationProperty.set(Duration.ofMinutes(breakDurationTime));

			titleLabel.setText("Take a break for a few minutes!");

		}

		// Play the timer
		timer.play();
	}

	@Override
	public String getResult() {
		return "ok'd";
	}

	private void updateTime() {
		// Set time
		durationProperty.set(durationProperty.get().minusSeconds(1));

		// If we are working
		if (working)
			elapsedDuration.set(elapsedDuration.get().minusSeconds(1));

		// If the time is 0
		if (durationProperty.get().isZero()) {

			// Stop the timer
			timer.stop();

			if (working) {
				// If the task is basically done
				if (elapsedDuration.get().isZero()) {
					// Ask the user whether he would like to add 5 more mins of his time to the
					// timer or whether he would be finished the task or whether he would want to
					// skip it
					List<ButtonType> buttonTypes = new ArrayList<>();

					// Create the finished button
					ButtonType FINISHED_BUTTON = ButtonType.FINISH;
					buttonTypes.add(FINISHED_BUTTON);

					// Create the skip button and add to list only if there is more than one event
					// left
					ButtonType SKIP_BUTTON = new ButtonType("Skip", ButtonData.OK_DONE);
					if (tasks.size() != 1)
						buttonTypes.add(SKIP_BUTTON);

					// Add the wait button
					ButtonType WAIT_BUTTON = new ButtonType("Add another 5 more minutes", ButtonData.CANCEL_CLOSE);
					buttonTypes.add(WAIT_BUTTON);

					// Create the alert
					Alert alert = new Alert(AlertType.INFORMATION, "The time for this task is up, would you like to:",
							buttonTypes.toArray(ButtonType[]::new));

					// Get the option
					ButtonType buttonType = alert.showAndWait().get();

					// If we are finished, move on to the chilling period
					if (buttonType.equals(FINISHED_BUTTON)) {
						working = false;

						// Remove the task from one list and add to the other
						tasks.remove(currentTask.get());
						currentTask.get().setCompleted(true);
						completedList.getItems().add(currentTask.get());
						// Add 5 more minutes
					} else if (buttonType.equals(WAIT_BUTTON)) {
						durationProperty.set(Duration.ofMinutes(5));
						elapsedDuration.set(Duration.ofMinutes(5));
					} else {
						// Get the next task if available
						currentTask.set(tasks.get(1));
					}
				}
				// If we were in the chilling period, we are now in the studying period
			} else {
				working = true;

				// Get the current task
				currentTask.set(tasks.get(0));
			}

			// Tell the user that they are done all their tasks
			if (tasks.isEmpty()) {
				container.setCenter(finishedScreen);

				// Enable the close button
				closeButtonNode.setDisable(false);

				return;
			}

			// Load the next option
			performNextAction();
		}
	}

	public static Duration min(Duration a, Duration b) {
		if (a.compareTo(b) < 0)
			return a;

		return b;
	}
}

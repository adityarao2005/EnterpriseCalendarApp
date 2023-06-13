package controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import application.Application;
import controller.dao.CalendarManifestController;
import controller.dao.GoogleConnectController;
import controller.spec.EventSpecController;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.util.StringConverter;
import model.User;
import model.events.Assignment;
import model.events.RCalendar;
import model.events.RTask;
import model.events.Reminder;
import view.controls.CheckBoxDS;
import view.controls.Scheduler;
import view.controls.UCalendar;

public class HomeController {
	// Fields

	// Uses FXML CDI (Dependancy Injection) to inject the fields into this class on
	// load
	@FXML
	private UCalendar dayChooser;

	@FXML
	private ListView<CheckBoxDS<RCalendar>> calendars;

	@FXML
	private ListView<RTask> todoList;

	@FXML
	private ListView<Assignment> assignmentsList;

	@FXML
	private Scheduler schedule;

	private User user;

	// Calendars view list
	private ObservableList<CheckBoxDS<RCalendar>> oCalendars;

	@FXML
	private void initialize() {
		onRefresh();
	}

	private void showCalendarList() {
		// Create the change event for the calendar list listener
		ListChangeListener<CheckBoxDS<RCalendar>> changeEvent = event -> {
			if (event.wasRemoved())
				user.getCalendars().removeAll(event.getRemoved().stream().map(CheckBoxDS::getValue).toList());
			if (event.wasAdded())
				user.getCalendars().addAll(event.getAddedSubList().stream().map(CheckBoxDS::getValue).toList());
		};

		// Create the observable list proxy and add the items
		oCalendars = FXCollections.observableArrayList(user.getCalendars().stream().map(CheckBoxDS::new).filter(e -> {

			// Just to add the listener while we are at it
			e.checkedProperty().addListener(l -> dateSelected());

			// Always return true
			return true;
		}).toList());

		oCalendars.addListener(changeEvent);

		calendars.setItems(oCalendars);
		calendars.setCellFactory(CheckBoxListCell.forListView(CheckBoxDS::checkedProperty));

	}

	private void showWorkTodoList() {
		// Set the items
		todoList.setItems(FXCollections.observableList(CalendarManifestController.getUncompletedTasks(user)));

		// Set the sell factory so that it acts as a wrapper for the tasks
		todoList.setCellFactory(CheckBoxListCell.forListView(e -> {
			return new SimpleBooleanProperty() {

				@Override
				public boolean get() {
					return e.isCompleted();
				}

				@Override
				public void set(boolean newValue) {
					if (newValue) {
						e.setCompleted(newValue);

						// Run this right after
						Platform.runLater(() -> todoList.setItems(
								FXCollections.observableList(CalendarManifestController.getUncompletedTasks(user))));
					}
				}

			};

			// Add a converter
		}, new StringConverter<>() {
			private Map<String, RTask> map = new HashMap<>();

			public String toString(RTask t) {
				if (t != null)
					map.put(t.getName(), t);

				return t == null ? null : t.getName();
			}

			public RTask fromString(String string) {
				return map.get(string);
			}
		}));

	}

	private void showAssignmentList() {
		// Set the items
		assignmentsList
				.setItems(FXCollections.observableList(CalendarManifestController.getUnattendedAssignments(user)));

		// Set the cell factory
		assignmentsList.setCellFactory(lv -> {
			// Create a new list cell where instead of performing the toString on load, itll
			// keep its name instead
			ListCell<Assignment> cell = new ListCell<>() {
				@Override
				public void updateItem(Assignment item, boolean empty) {
					super.updateItem(item, empty);

					setText(item == null || empty ? "" : item.getName());
				}
			};

			// Create a context/popup menu
			ContextMenu contextMenu = new ContextMenu();

			// Create and add a menu item
			MenuItem editItem = new MenuItem();
			editItem.setOnAction(event -> {
				// Get the assignment
				Assignment item = cell.getItem();

				// Get the new version
				Assignment value = EventSpecController.createReminderFrom(item);
				// If there is a version, copy it
				if (value != null) {
					item.from(value);

				}
				// Set the items
				assignmentsList.setItems(
						FXCollections.observableList(CalendarManifestController.getUnattendedAssignments(user)));

				// Refresh
				onRefresh();
			});

			// Add the item to the popup
			contextMenu.getItems().add(editItem);

			// Add a listener to the empty property so that when the listcell is not empty,
			// we would show the popup menu
			cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
				if (isNowEmpty) {
					cell.setContextMenu(null);
				} else {

					editItem.setText(String.format("Schedule Tasks for %s", cell.getItem().getName()));

					cell.setContextMenu(contextMenu);
				}
			});

			// Return the cell
			return cell;
		});
	}

	@FXML
	private void logoutAction() {

		Application.getApplication().setCurrentUser(null);
		// Navigate to the start screen
		Application.getApplication().navigate("/view/WelcomeView.fxml", "Welcome");
	}

	@FXML
	private void exitAction() {
		// Exit the application
		Platform.exit();

		// Close the program
		System.exit(0);
	}

	@FXML
	private void dateSelected() {

		Platform.runLater(() -> {
			// Get the list of tasks
			List<Reminder> tasks = oCalendars.stream().filter(CheckBoxDS::getChecked).map(CheckBoxDS::getValue)
					.map(RCalendar::getReminders).flatMap(List::stream).toList();

			List<Reminder> totalTasks = Stream
					.concat(tasks.stream(), tasks.stream().filter(e -> e instanceof Assignment)
							.map(e -> ((Assignment) e).getSchedule()).flatMap(List::stream))
					.toList();

			// Set the reminders
			List<Reminder> reminders = totalTasks.stream().filter(e -> e.occursOn(dayChooser.getCurrentDate()))
					.toList();
			System.out.println(dayChooser.getCurrentDate());
			System.out.println(reminders);

			schedule.getReminder().clear();
			schedule.getReminder().addAll(reminders);
		});
	}

	@FXML
	private void onCreateTask() {
		// Get an instance from a modal
		Reminder reminder = Application.getApplication().dialog("/view/EventModalView.fxml", "Create Event");

		// add
		if (reminder != null) {
			reminder.getCalendar().getReminders().add(reminder);
		}

		onRefresh();
	}

	@FXML
	private void onManage() {
		// Show a dialog
		Application.getApplication().dialog("/view/EventManagement.fxml", "Manage Events");

		// Refresh afterwards
		onRefresh();
	}

	@FXML
	private void onCreateCal() {
		// Create a text input dialog
		TextInputDialog dialog = new TextInputDialog();

		dialog.setHeaderText("What is the name of this calendar?");

		// Show it and get result
		String name = dialog.showAndWait().orElseGet(null);

		// If the result is not null
		if (name != null) {

			// Create and add the calendar
			user.getCalendars().add(new RCalendar(name));
		}

		onRefresh();
	}

	@FXML
	private void onRefresh() {
		// Get the current user
		user = Application.getApplication().getCurrentUser();

		// initialize the users calendar list if there arent any
		if (user.getCalendars() == null)
			user.setCalendars(new ArrayList<>());

		// activate the users google profile if available
		if (user.getProfile() != null) {
			try {
				GoogleConnectController.retrieveClassroom(user);

				CalendarManifestController.refreshGoogleCalendars(user);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		// Create the controls
		showCalendarList();

		showWorkTodoList();

		showAssignmentList();

		dateSelected();
	}

}

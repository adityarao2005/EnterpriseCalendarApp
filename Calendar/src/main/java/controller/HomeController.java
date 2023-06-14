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
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.StringConverter;
import model.User;
import model.events.Assignment;
import model.events.RCalendar;
import model.events.RTask;
import model.events.Reminder;
import view.controls.CheckBoxDS;
import view.controls.Scheduler;
import view.controls.UCalendar;

// Controls the Home/Main View
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
	private ListView<Assignment> unscheduledAssignmentsList;

	@FXML
	private ListView<Assignment> assignmentsList;

	@FXML
	private ListView<Assignment> completedAssignmentsList;

	@FXML
	private Scheduler schedule;

	private User user;

	@FXML
	private MenuItem signInMenu;

	// Calendars view list
	private ObservableList<CheckBoxDS<RCalendar>> oCalendars;

	// Colours for calendars, to differentiate which events come from which calendar
	private static final Color[] COLOURS = new Color[] { Color.ORANGE, Color.CYAN, Color.YELLOW, Color.GREENYELLOW };

	// Calls refresh on init
	@FXML
	private void initialize() {
		onRefresh();
	}

	// Shows the calendar list
	private void showCalendarList() {
		// Create the change event for the calendar list listener
		ListChangeListener<CheckBoxDS<RCalendar>> changeEvent = event -> {
			if (event.wasRemoved())
				user.getCalendars().removeAll(event.getRemoved().stream().map(CheckBoxDS::getValue).toList());
			if (event.wasAdded())
				user.getCalendars().addAll(event.getAddedSubList().stream().map(CheckBoxDS::getValue).toList());
		};

		// Create the observable list proxy and add the items
		SimpleIntegerProperty temp = new SimpleIntegerProperty(0);
		oCalendars = FXCollections.observableArrayList(user.getCalendars().stream().map(CheckBoxDS::new).filter(e -> {

			// Just to add the listener while we are at it
			e.checkedProperty().addListener(l -> dateSelected());

			e.getValue().setColor(COLOURS[temp.get() % COLOURS.length]);

			temp.set(temp.get() + 1);

			// Always return true
			return true;
		}).toList());

		oCalendars.addListener(changeEvent);

		// Create the cell factory
		calendars.setCellFactory(lv -> {
			// Create the cell
			ListCell<CheckBoxDS<RCalendar>> cell = CheckBoxListCell
					.<CheckBoxDS<RCalendar>>forListView(CheckBoxDS::checkedProperty).call(lv);

			// Bind the cell label
			cell.backgroundProperty().bind(Bindings.createObjectBinding(() -> {

				if (cell.getItem() == null) {
					return new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY));
				} else {

					return new Background(
							new BackgroundFill(cell.getItem().getValue().getColor(), CornerRadii.EMPTY, Insets.EMPTY));
				}

			}, cell.itemProperty()));

			return cell;
		});

		calendars.setItems(oCalendars);
	}

	// Shows todo list
	private void showWorkTodoList() {
		// Set the items
		todoList.setItems(FXCollections.observableList(CalendarManifestController.getUncompletedTasks(user)));

		// Set the sell factory so that it acts as a wrapper for the tasks
		todoList.setCellFactory(CheckBoxListCell.forListView(e -> {
			// Create a wrapper for the completed variable of the tasks
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
				String value = null;

				if (t != null) {
					if (t.getAssignment() != null)
						map.put(value = t.getName() + " - " + t.getAssignment().getName(), t);
					else
						map.put(value = t.getName(), t);
				}

				return value;
			}

			public RTask fromString(String string) {
				return map.get(string);
			}
		}));

	}

	// Show the assignment list
	private void showAssignmentList() {
		// Set the items
		unscheduledAssignmentsList
				.setItems(FXCollections.observableList(CalendarManifestController.getUnattendedAssignments(user)));

		assignmentsList
				.setItems(FXCollections.observableList(CalendarManifestController.getScheduledAssignments(user)));

		completedAssignmentsList
				.setItems(FXCollections.observableList(CalendarManifestController.getCompletedAssignments(user)));

		// Create a list cell callback function as a cellfactory
		final Callback<ListView<Assignment>, ListCell<Assignment>> ASSIGNMENT_CALLBACK = lv -> {

			// Create the cell and override its original implementation
			ListCell<Assignment> cell = new ListCell<>() {
				@Override
				public void updateItem(Assignment item, boolean empty) {
					// Call super method
					super.updateItem(item, empty);

					// set item
					setText(item == null || empty ? "" : item.getName());

					// Set the background
					if (item != null) {
						Color color = item.getCalendar().getColor();

						this.setStyle(
								String.format("-fx-background-color: rgb(%d, %d, %d)", (int) (color.getRed() * 255),
										(int) (color.getGreen() * 255), (int) (color.getBlue() * 255)));
					}
				}
			};

			// Return the cell
			return cell;
		};

		// Set cell factories
		assignmentsList.setCellFactory(ASSIGNMENT_CALLBACK);

		completedAssignmentsList.setCellFactory(ASSIGNMENT_CALLBACK);

		unscheduledAssignmentsList.setCellFactory(lv -> {
			// Create a new list cell where instead of performing the toString on load, itll
			// keep its name instead
			ListCell<Assignment> cell = ASSIGNMENT_CALLBACK.call(lv);

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
				unscheduledAssignmentsList.setItems(
						FXCollections.observableList(CalendarManifestController.getUnattendedAssignments(user)));

				// Refresh
				onRefresh();
			});

			// Add the item to the popup
			contextMenu.getItems().add(editItem);

			// Add a listener to the empty property so that when the listcell is not empty,
			// we would show the popup menu
			cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
				// If the cell is empty, remove the context menu
				if (isNowEmpty) {
					cell.setContextMenu(null);
				} else {

					// Otherwise set the text of the content menu to the task name and add context menu
					editItem.setText(String.format("Schedule Tasks for %s", cell.getItem().getName()));

					cell.setContextMenu(contextMenu);
				}
			});

			// Return the cell
			return cell;
		});
	}

	// Action for logout
	@FXML
	private void logoutAction() {

		Application.getApplication().setCurrentUser(null);
		// Navigate to the start screen
		Application.getApplication().navigate("/view/WelcomeView.fxml", "Welcome");
	}

	// Action for exit
	@FXML
	private void exitAction() {
		// Exit the application
		Platform.exit();

		// Close the program
		System.exit(0);
	}

	// When the date is selected from the calendar control, run this
	@FXML
	private void dateSelected() {

		// Delegate this to UI thread but not immediately
		Platform.runLater(() -> {
			// Get the list of reminders
			List<Reminder> tasks = oCalendars.stream().filter(CheckBoxDS::getChecked).map(CheckBoxDS::getValue)
					.map(RCalendar::getReminders).flatMap(List::stream).toList();

			// Get the total amount of tasks from the calendars and assignments
			List<Reminder> totalTasks = Stream
					.concat(tasks.stream(), tasks.stream().filter(e -> e instanceof Assignment)
							.map(e -> ((Assignment) e).getSchedule()).flatMap(List::stream))
					.toList();

			// Set the reminders
			List<Reminder> reminders = totalTasks.stream().filter(e -> e.occursOn(dayChooser.getCurrentDate()))
					.toList();

			// Clear and add reminders list
			schedule.getReminder().clear();
			schedule.getReminder().addAll(reminders);
		});
	}

	// Show popup dialog for creation of a task
	@FXML
	private void onCreateTask() {
		// Get an instance from a modal
		Reminder reminder = Application.getApplication().dialog("/view/EventModalView.fxml", "Create Event");

		// add
		if (reminder != null) {
			reminder.getCalendar().getReminders().add(reminder);
		}

		// Refresh the users screen
		onRefresh();
	}

	// Show popup dialog for management of task
	@FXML
	private void onManage() {
		// Show a dialog
		Application.getApplication().dialog("/view/EventManagement.fxml", "Manage Events");

		// Refresh afterwards
		onRefresh();
	}

	// Show text input dialog for creation of a new calendar
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

		// Refresh the GUI
		onRefresh();
	}

	// Refreshes GUI and user models
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

				signInMenu.setDisable(true);
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

	// Sign in with google
	@FXML
	private void signInWithGoogle() {
		// Create a google user
		try {
			GoogleConnectController.createGoogleUser(user);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// Refresh the GUI
		onRefresh();
	}

	// Show work on tasks dialog
	@FXML
	private void workOnTasks() {

		// Show a dialog
		Application.getApplication().dialog("/view/Pomodoro.fxml", "Lets Work!");

		// Refresh the GUI
		onRefresh();
	}

}

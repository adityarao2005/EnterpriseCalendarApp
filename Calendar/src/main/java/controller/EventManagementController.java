package controller;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import application.Application;
import controller.spec.EventSpecController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import model.events.Assignment;
import model.events.CompleteableReminder;
import model.events.RCalendar;
import model.events.REvent;
import model.events.RTask;
import model.events.Reminder;

// EventManagementController - Inherits the interface DialogController - Used to allow the user to manage all the tasks, events, and assignments that he/she has
public class EventManagementController implements DialogController<String> {

	// Get images from resource
	private static final Image TRASH = new Image(
			EventManagementController.class.getResourceAsStream("/images/trash.png"));
	private static final Image EDIT = new Image(
			EventManagementController.class.getResourceAsStream("/images/edit-solid.png"));

	// Fields
	// Loaded by FXMLLoader through Dependancy Injection
	@FXML
	private TreeTableView<Reminder> treeTableView;

	@FXML
	private TreeTableColumn<Reminder, String> typeColumn;

	@FXML
	private TreeTableColumn<Reminder, RCalendar> calendarColumn;

	@FXML
	private TreeTableColumn<Reminder, RCalendar> classroomLoaded;

	@FXML
	private TreeTableColumn<Reminder, Reminder> deleteColumn;

	@FXML
	private TreeTableColumn<Reminder, Reminder> editColumn;

	@FXML
	private TreeTableColumn<Reminder, Reminder> completedColumn;

	// Root node
	private TreeItem<Reminder> root;

	// Constructor
	public EventManagementController() {
	}

	// First method called by FXMLLoader
	@FXML
	private void initialize() {
		// Set root node
		root = new TreeItem<>(new REvent());

		// Set the root node
		treeTableView.setRoot(root);

		// Set cell value factory of the type column
		typeColumn.setCellValueFactory(value -> {
			// Get the event
			Reminder type = value.getValue().getValue();

			// If the event is a reminder then return "REMINDER"
			if (type instanceof REvent)
				return new ReadOnlyStringWrapper("REMINDER").getReadOnlyProperty();

			// If the event is a task then return "TASK"
			if (type instanceof RTask)
				return new ReadOnlyStringWrapper("TASK").getReadOnlyProperty();

			// If the event is a reminder then return "ASSIGNMENT"
			return new ReadOnlyStringWrapper("ASSIGNMENT").getReadOnlyProperty();
		});

		// Set cell factory for calendar column
		calendarColumn.setCellFactory(v -> {
			// Create a new treetablecell
			return new TreeTableCell<>() {
				// When the treetable updates all its cells, this method is called
				@Override
				public void updateItem(RCalendar item, boolean empty) {
					// Call super method
					super.updateItem(item, empty);

					// Set the text
					setText(item == null || empty ? "" : item.getName());
				}
			};
		});

		// Set cell factory for classroom loaded column
		classroomLoaded.setCellFactory(v -> {
			// Create a new Treetable cell
			return new TreeTableCell<>() {

				// When the treetable updates all its cells, this method is called
				@Override
				public void updateItem(RCalendar item, boolean empty) {
					// Call super method
					super.updateItem(item, empty);

					// Set the text
					setText(item == null || empty ? "" : (item.isClassroomLoaded() ? "Yes" : "No"));
				}
			};
		});

		// Set the icon so that its any value
		deleteColumn.setCellValueFactory(v -> v.getValue().valueProperty());

		// Set cell factory for delete column view
		deleteColumn.setCellFactory(v -> new ImageIconTTC(TRASH, this::deleteReminder));

		// Set the icon so that its any value
		editColumn.setCellValueFactory(v -> v.getValue().valueProperty());

		// Set cell factory for delete column view
		editColumn.setCellFactory(v -> new ImageIconTTC(EDIT, this::editReminder, true));

		// Set the value of the treetablecolumn
		completedColumn.setCellValueFactory(v -> v.getValue().valueProperty());

		// Set the value of the cellfactory
		completedColumn.setCellFactory(v -> new CheckBoxTTC((r, x) -> ((CompleteableReminder) r).setCompleted(x)));

		treeTableView.setRoot(root);

		// Load
		onReload();
	}

	// called to reload page
	@FXML
	private void onReload() {
		// Clear the roots
		root.getChildren().clear();

		// Go through all the reminders
		List<Reminder> reminders = Application.getApplication().getCurrentUser().getCalendars().stream()
				.map(RCalendar::getReminders).flatMap(List<Reminder>::stream).collect(Collectors.toList());

		// Go through all the reminders
		for (Reminder reminder : reminders) {
			// Create a node for the reminders
			TreeItem<Reminder> node = new TreeItem<>(reminder);

			// If the reminder is an assignment
			if (reminder instanceof Assignment assignment) {
				// Add the tasks
				for (RTask task : assignment.getSchedule()) {
					// Create a node for the reminders
					node.getChildren().add(new TreeItem<>(task));
				}
			}

			// Add the root
			root.getChildren().add(node);
		}
	}

	// Called to create a new event
	@FXML
	private void onCreate() {
		// Get an instance from a modal
		Reminder reminder = Application.getApplication().dialog("/view/EventModalView.fxml", "Create Event");

		// add if the reminder exists
		if (reminder != null) {
			reminder.getCalendar().getReminders().add(reminder);
		}
	}

	// Delete the reminder
	private void deleteReminder(Reminder reminder) {
		// If the reminder is a task which origins from an assignment
		if (reminder instanceof RTask && ((RTask) reminder).getAssignment() != null) {
			// Remove the task from its assignment
			((RTask) reminder).getAssignment().getSchedule().remove(reminder);
		} else {
			// Otherwise remove the event from the calendar
			reminder.getCalendar().getReminders().remove(reminder);
		}
	}

	// Edit the reminder
	private void editReminder(Reminder reminder) {

		// Get the neighbors of it from its parent if it is a task which origins from an
		// assignment
		List<RTask> otherTasks = (reminder instanceof RTask && ((RTask) reminder).getAssignment() != null)
				? ((RTask) reminder).getAssignment().getSchedule()
				: List.of();

		// Copy values from the value returned by the dialog
		reminder.from(EventSpecController.createReminderFrom(reminder, otherTasks));
	}

	// Not needed but good to have since this is acting as a dialog but might as
	// well
	@Override
	public String getResult() {
		return "ok'd";
	}

	// An Icon treetable cell - contains a clickable icon
	public static class ImageIconTTC extends TreeTableCell<Reminder, Reminder> {
		// Fields
		// Controls
		private HBox hbox = new HBox();
		private Button button;
		private Reminder lastItem;
		// Check whether we allow assignments or not
		private boolean allowAssignmentFromClassroom;

		// Constructor
		public ImageIconTTC(Image icon, Consumer<Reminder> handler) {
			// create an image viewer
			ImageView image = new ImageView(icon);
			// Create a button
			button = new Button();

			// Preserve the image ratio and set the fit height to 20
			image.setPreserveRatio(true);
			image.setFitHeight(20);

			// Set graphic
			button.setGraphic(image);

			// Add button to hbox and add action listener
			hbox.getChildren().addAll(button);
			button.setOnAction(v -> handler.accept(lastItem));
		}

		// Overloaded constructor
		public ImageIconTTC(Image icon, Consumer<Reminder> handler, boolean allowAssignmentFromClassroom) {
			// Call other constructor
			this(icon, handler);
			// Set the value of this
			this.allowAssignmentFromClassroom = allowAssignmentFromClassroom;
		}

		// Updates the cell with given item
		@Override
		public void updateItem(Reminder item, boolean empty) {
			// Calls super method
			super.updateItem(item, empty);
			// make sure there is no text
			setText(null);
			// If the item does not exist
			if (item == null || empty) {
				// Set null
				lastItem = null;
				setGraphic(null);
			} else {
				// Otherwise set the graphic and value
				lastItem = item;
				setGraphic(hbox);

				// If the calendar does not exist
				if (item.getCalendar() != null) {
					// If we dont allow assignments from the classroom and the calendar is classroom
					// loaded, disable the
					// button
					if (!allowAssignmentFromClassroom && item.getCalendar().isClassroomLoaded()) {
						button.setDisable(true);
					}
				} else {
					// Enable the button
					button.setDisable(false);
				}
			}
		}
	}

	public static class CheckBoxTTC extends TreeTableCell<Reminder, Reminder> {
		private HBox hbox = new HBox();
		private CheckBox button;
		private Reminder lastItem;

		public CheckBoxTTC(BiConsumer<Reminder, Boolean> handler) {
			button = new CheckBox();
			hbox.getChildren().addAll(button);
			HBox.setHgrow(button, Priority.ALWAYS);
			button.setOnAction(v -> handler.accept(lastItem, button.isSelected()));
		}

		@Override
		public void updateItem(Reminder item, boolean empty) {
			super.updateItem(item, empty);
			setText(null); // No text in label of super class
			if (item == null || empty || !(item instanceof CompleteableReminder)) {
				lastItem = null;
				setGraphic(null);
			} else {
				lastItem = item;
				setGraphic(hbox);

				button.setSelected(((CompleteableReminder) item).isCompleted());

				if (item.getCalendar() != null && item.getCalendar().isClassroomLoaded()) {
					button.setDisable(true);
				} else {
					button.setDisable(false);
				}
			}
		}
	}

}

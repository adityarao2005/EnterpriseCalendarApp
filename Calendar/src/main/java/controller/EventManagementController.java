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

public class EventManagementController implements DialogController<String> {

	private static final Image TRASH = new Image(
			EventManagementController.class.getResourceAsStream("/images/trash.png"));
	private static final Image EDIT = new Image(
			EventManagementController.class.getResourceAsStream("/images/edit-solid.png"));

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

	public EventManagementController() {
	}

	@FXML
	private void initialize() {
		// Set root node
		root = new TreeItem<>(new REvent());

		// Set the root node
		treeTableView.setRoot(root);

		// Set cell value factory of the type column
		typeColumn.setCellValueFactory(value -> {
			Reminder type = value.getValue().getValue();

			if (type instanceof REvent)
				return new ReadOnlyStringWrapper("REMINDER").getReadOnlyProperty();

			if (type instanceof RTask)
				return new ReadOnlyStringWrapper("TASK").getReadOnlyProperty();

			return new ReadOnlyStringWrapper("ASSIGNMENT").getReadOnlyProperty();
		});

		// Set cell factory for calendar column
		calendarColumn.setCellFactory(v -> {
			return new TreeTableCell<>() {
				@Override
				public void updateItem(RCalendar item, boolean empty) {
					super.updateItem(item, empty);

					// Set the text
					setText(item == null || empty ? "" : item.getName());
				}
			};
		});

		// Set cell factory for classroom loaded column
		classroomLoaded.setCellFactory(v -> {
			return new TreeTableCell<>() {

				@Override
				public void updateItem(RCalendar item, boolean empty) {
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

	@FXML
	private void onCreate() {
		// Get an instance from a modal
		Reminder reminder = Application.getApplication().dialog("/view/EventModalView.fxml", "Create Event");

		// add
		if (reminder != null) {
			reminder.getCalendar().getReminders().add(reminder);
		}
	}

	private void deleteReminder(Reminder reminder) {
		System.out.println(reminder);

		if (reminder instanceof RTask && ((RTask) reminder).getAssignment() != null) {
			((RTask) reminder).getAssignment().getSchedule().remove(reminder);
		} else {
			reminder.getCalendar().getReminders().remove(reminder);
		}
	}

	private void editReminder(Reminder reminder) {

		List<RTask> otherTasks = (reminder instanceof RTask && ((RTask) reminder).getAssignment() != null)
				? ((RTask) reminder).getAssignment().getSchedule()
				: List.of();

		reminder.from(EventSpecController.createReminderFrom(reminder, otherTasks));
	}

	// Not needed but good to have since this is acting as a dialog
	@Override
	public String getResult() {
		return "ok'd";
	}

	public static class ImageIconTTC extends TreeTableCell<Reminder, Reminder> {
		private HBox hbox = new HBox();
		private Button button;
		private Reminder lastItem;
		private boolean allowAssignment;

		public ImageIconTTC(Image icon, Consumer<Reminder> handler) {
			ImageView image = new ImageView(icon);
			button = new Button();

			image.setPreserveRatio(true);
			image.setFitHeight(20);

			button.setGraphic(image);

			HBox.setHgrow(button, Priority.NEVER);
			hbox.getChildren().addAll(button);
			button.setOnAction(v -> handler.accept(lastItem));
		}

		public ImageIconTTC(Image icon, Consumer<Reminder> handler, boolean allowAssignment) {
			this(icon, handler);
			this.allowAssignment = allowAssignment;
		}

		@Override
		public void updateItem(Reminder item, boolean empty) {
			super.updateItem(item, empty);
			setText(null); // No text in label of super class
			if (item == null || empty) {
				lastItem = null;
				setGraphic(null);
			} else {
				lastItem = item;
				setGraphic(hbox);

				if (item.getCalendar() != null) {
					if (!allowAssignment && item.getCalendar().isClassroomLoaded()) {
						button.setDisable(true);
					}
				} else {
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

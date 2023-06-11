package controller;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import model.events.RCalendar;
import model.events.REvent;
import model.events.RTask;
import model.events.Reminder;

public class EventManagementController implements DialogController<String> {

	@FXML
	private TreeTableView<Reminder> treeTableView;

	@FXML
	private TreeTableColumn<Reminder, String> typeColumn;

	@FXML
	private TreeTableColumn<Reminder, RCalendar> calendarColumn;

	@FXML
	private TreeTableColumn<Reminder, RCalendar> classroomLoaded;

	@FXML
	private TreeTableColumn<Reminder, FontAwesomeIcon> deleteColumn;

	@FXML
	private TreeTableColumn<Reminder, FontAwesomeIcon> editColumn;

	@FXML
	private TreeTableColumn<Reminder, BooleanProperty> completedColumn;

	// Root node
	private TreeItem<Reminder> root;

	private EventManagementController() {
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
		deleteColumn.setCellValueFactory(v -> {
			return new ReadOnlyObjectWrapper<>(FontAwesomeIcon.TRASH).getReadOnlyProperty();
		});

		// Set cell factory for delete column view
		deleteColumn.setCellFactory(v -> {
			return new TreeTableCell<>() {
				private FontAwesomeIconView view;

				@Override
				public void updateItem(FontAwesomeIcon item, boolean empty) {
					super.updateItem(item, empty);

					super.updateItem(item, empty);
					setText(null); // No text in label of super class
					if (empty) {
						lastItem = null;
						setGraphic(null);
					} else {
						lastItem = item;
						label.setText(item != null ? item : "<null>");
						setGraphic(hbox);
					}
				}
			};
		});
	}

	// Not needed but good to have since this is acting as a dialog
	@Override
	public String getResult() {
		return "ok'd";
	}

}

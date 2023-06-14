package view.controls.skin;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import view.controls.ControlTable;

// Skin for control table
public class ControlTableSkin<T> extends FXRootSkinBase<ControlTable<T>, BorderPane> {
	// Fields
	// FXML dependancy injection
	@FXML
	private ListView<T> view;

	@FXML
	private Label titleLabel;

	// Constructor - loads from super
	public ControlTableSkin(ControlTable<T> e) {
		super(e, ControlTableSkin.class.getResource("/view/controls/ControlTable.fxml"), BorderPane::new);
	}

	// Initialize method
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Create bindings
		view.itemsProperty().bind(this.getSkinnable().itemsProperty());
		view.cellFactoryProperty().bind(this.getSkinnable().cellFactoryProperty());
		titleLabel.textProperty().bind(this.getSkinnable().titleProperty());
	}

	// Getter for items
	public ObservableList<T> getItems() {
		return this.getSkinnable().getItems();
	}

	// Handle row subtracted
	@FXML
	private void handleRowSubtracted() {
		if (this.getSkinnable().onListRemoveRequestProperty().get().apply(view.getSelectionModel().getSelectedItem())) {
			this.getSkinnable().getItems().remove(view.getSelectionModel().getSelectedItem());
		}
	}

	// Handle row added
	@FXML
	private void handleRowAdded() {
		// Get the value
		T value = this.getSkinnable().onListAddRequestProperty().get().get();

		// If the value is not null then add to list
		if (value != null) {
			this.getSkinnable().getItems().add(value);
		}

	}
}

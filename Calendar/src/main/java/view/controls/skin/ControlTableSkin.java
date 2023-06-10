package view.controls.skin;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import view.controls.ControlTable;

public class ControlTableSkin<T> extends FXRootSkinBase<ControlTable<T>, BorderPane> {
	@FXML
	private ListView<T> view;

	public ControlTableSkin(ControlTable<T> e) {
		super(e, ControlTableSkin.class.getResource("/view/controls/CalendarView.fxml"), BorderPane::new);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		view.itemsProperty().bind(this.getSkinnable().itemsProperty());
		view.cellFactoryProperty().bind(this.getSkinnable().cellFactoryProperty());
	}

	public ObservableList<T> getItems() {
		return this.getSkinnable().getItems();
	}

	@FXML
	private void handleRowSubtracted() {
		if (this.getSkinnable().onListRemoveRequestProperty().get().apply(view.getSelectionModel().getSelectedItem())) {
			this.getSkinnable().getItems().remove(view.getSelectionModel().getSelectedItem());
		}
	}

	@FXML
	private void handleRowAdded() {

		T value = this.getSkinnable().onListAddRequestProperty().get().get();

		if (value != null) {
			this.getSkinnable().getItems().add(value);
		}

	}
}

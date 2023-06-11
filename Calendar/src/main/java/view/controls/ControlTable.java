package view.controls;

import java.util.function.Supplier;

import com.google.common.base.Predicate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.util.Callback;
import view.controls.skin.ControlTableSkin;

// XXX: Still got to check if this works
public final class ControlTable<T> extends Control {

	// Represents the items of the list
	private final ObjectProperty<ObservableList<T>> items = new ObjectPropertyBase<>(
			FXCollections.observableArrayList()) {

		@Override
		public Object getBean() {
			return ControlTable.this;
		}

		@Override
		public String getName() {
			return "items";
		}

	};

	public ObjectProperty<ObservableList<T>> itemsProperty() {
		return items;
	}

	public ObservableList<T> getItems() {
		return items.get();
	}

	public void setItems(ObservableList<T> items) {
		this.items.set(items);
	}

	// Represents the adding to the list request
	public final ObjectProperty<Supplier<T>> onListAddRequestProperty() {
		return onListAddRequest;
	}

	public final void setOnListAddRequestProperty(Supplier<T> value) {
		onListAddRequestProperty().set(value);
	}

	public final Supplier<T> getOnListAddRequest() {
		return onListAddRequestProperty().get();
	}

	private final ObjectProperty<Supplier<T>> onListAddRequest = new ObjectPropertyBase<Supplier<T>>(() -> null) {

		@Override
		public Object getBean() {
			return ControlTable.this;
		}

		@Override
		public String getName() {
			return "onListAddRequest";
		}
	};

	// Represents the remove from list request
	public final ObjectProperty<Predicate<T>> onListRemoveRequestProperty() {
		return onListRemoveRequest;
	}

	public final void setOnListRemoveRequest(Predicate<T> value) {
		onListRemoveRequestProperty().set(value);
	}

	public final Predicate<T> getOnListRemoveRequest() {
		return onListRemoveRequestProperty().get();
	}

	private final ObjectProperty<Predicate<T>> onListRemoveRequest = new ObjectPropertyBase<Predicate<T>>(e -> true) {

		@Override
		public Object getBean() {
			return ControlTable.this;
		}

		@Override
		public String getName() {
			return "onListRemoveRequest";
		}
	};

	// Represents the cell factory of the hidden listview
	private final ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactory = new ObjectPropertyBase<>() {

		@Override
		public Object getBean() {
			return ControlTable.this;
		}

		@Override
		public String getName() {
			return "cellFactory";
		}

	};

	public ObjectProperty<Callback<ListView<T>, ListCell<T>>> cellFactoryProperty() {
		return cellFactory;
	}

	public Callback<ListView<T>, ListCell<T>> getCellFactory() {
		return cellFactory.get();
	}

	public void setCellFactory(Callback<ListView<T>, ListCell<T>> cellFactory) {
		this.cellFactory.set(cellFactory);
	}

	// Creates a default skin
	@Override
	protected Skin<?> createDefaultSkin() {
		return new ControlTableSkin<>(this);
	}
}

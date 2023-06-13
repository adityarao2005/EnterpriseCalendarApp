package view.controls;

import view.controls.skin.UCalendarSkin;
import java.time.LocalDate;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Simulates the Win32 calendar control and calendar from the JavaFX DatePicker
 * control content
 * 
 * @author Raos
 *
 */
public final class UCalendar extends Control {
	/**
	 * Creates the default skin as {@link CalendarSkin}
	 */
	@Override
	protected Skin<?> createDefaultSkin() {
		return new UCalendarSkin(this);
	}

	/**
	 * Constructs Calendar object with a default date of now
	 */
	public UCalendar() {
	}

	/**
	 * The Event handler of the Calendar dates or changes of the current date
	 */
	public final ObjectProperty<EventHandler<ActionEvent>> onActionProperty() {
		return onAction;
	}

	public final void setOnAction(EventHandler<ActionEvent> value) {
		onActionProperty().set(value);
	}

	public final EventHandler<ActionEvent> getOnAction() {
		return onActionProperty().get();
	}

	private final ObjectProperty<EventHandler<ActionEvent>> onAction = new ObjectPropertyBase<EventHandler<ActionEvent>>() {
		@Override
		protected void invalidated() {
			setEventHandler(ActionEvent.ACTION, get());
		}

		@Override
		public Object getBean() {
			return UCalendar.this;
		}

		@Override
		public String getName() {
			return "onAction";
		}
	};

	/**
	 * The Current Date of the Calendar Control.
	 */
	public final ObjectProperty<LocalDate> currentDateProperty() {
		return this.currentDate;
	}

	public final LocalDate getCurrentDate() {
		return this.currentDateProperty().get();
	}

	public final void setCurrentDate(final LocalDate currentDate) {
		this.currentDateProperty().set(currentDate);
	}

	private final ObjectProperty<LocalDate> currentDate = new SimpleObjectProperty<>(this, "currentDate",
			LocalDate.now());

}
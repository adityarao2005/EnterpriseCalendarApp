package controller;

import java.util.ArrayList;

import application.Application;
import controller.dao.CalendarManifestController;
import controller.dao.GoogleConnectController;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import model.User;
import model.events.CompleteableReminder;
import model.events.RCalendar;
import view.controls.CheckBoxDS;
import view.controls.ScheduleViewController;
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
	private ListView<CompleteableReminder> todoList;

	@FXML
	private ScheduleViewController scheduleController;

	private User user;

	// Calendars view list
	private ObservableList<CheckBoxDS<RCalendar>> oCalendars;

	@FXML
	private void initialize() {
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

		// Create the change event for the calendar list listener
		ListChangeListener<CheckBoxDS<RCalendar>> changeEvent = event -> {
			if (event.wasRemoved())
				user.getCalendars().removeAll(event.getRemoved().stream().map(CheckBoxDS::getValue).toList());
			if (event.wasAdded())
				user.getCalendars().addAll(event.getAddedSubList().stream().map(CheckBoxDS::getValue).toList());
		};

		// Create the observable list proxy and add the items
		oCalendars = FXCollections.observableArrayList(user.getCalendars().stream().map(CheckBoxDS::new).toList());
		oCalendars.addListener(changeEvent);

		calendars.setItems(oCalendars);
		calendars.setCellFactory(CheckBoxListCell.forListView(CheckBoxDS::checkedProperty));

	}

	@FXML
	private void logoutAction() {

		Application.getApplication().setCurrentUser(null);
		// Navigate to the start screen
		Application.getApplication().navigate("/view/WelcomeView.fxml", "Welcome");
	}

	@FXML
	private void dateSelected() {

	}

	@FXML
	private void onCreateTask() {

	}

	@FXML
	private void onManage() {

	}

	@FXML
	private void onRefresh() {

	}

	@FXML
	private void onCreateCal() {

	}
}

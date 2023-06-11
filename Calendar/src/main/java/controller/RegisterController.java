package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import application.Application;
import controller.dao.GoogleConnectController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.User;

// Controls the registration view
public class RegisterController {
	// Fields

	// Uses FXML CDI (Dependancy Injection) to inject the fields into this class on
	// load
	@FXML
	private TextField nameField;
	@FXML
	private TextField userIDField;
	@FXML
	private PasswordField passwordField;

	// Action performed on back button click
	@FXML
	private void backAction() {
		// Navigate to the start screen
		Application.getApplication().navigate("/view/WelcomeView.fxml", "Welcome");
	}

	// Action performed on register button click
	@FXML
	private void registerAction() {
		// Validate the form fields
		boolean validated = validateFields();

		// If the form is invalid then return
		if (!validated)
			return;

		// Create a user based on the form fields
		User user = new User(userIDField.getText().trim(), nameField.getText().trim(), passwordField.getText().trim(),
				null);

		// Ask the user whether he would like to link his google account as well
		requestGoogleSync(user);

		// Add the user to the many users and set this user as the current user
		Application.getApplication().getUsers().add(user);
		Application.getApplication().setCurrentUser(user);

		// Navigate to the home page
		Application.getApplication().navigate("/view/MainView.fxml", "Home");

	}

	// Request the user to access his google classroom
	private void requestGoogleSync(User user) {

		// Ask the user whether he wants to link his google account
		Alert alert = new Alert(AlertType.CONFIRMATION, "Confirm", ButtonType.YES, ButtonType.NO);

		alert.setHeaderText("Would you like to import assignments from your google classroom?");
		// Get the response
		ButtonType buttonType = alert.showAndWait().get();

		// If the response is yes
		if (buttonType.equals(ButtonType.YES)) {
			// Create a google user
			try {
				GoogleConnectController.createGoogleUser(user);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

	// Validate the fields
	private boolean validateFields() {
		// Create a list of errors in the case there are errors encountered
		List<String> errors = new ArrayList<>();

		// Check if any of the text fields are empty, if so append it to the errors
		if (nameField.getText().trim().isEmpty()) {
			errors.add("Name is empty");
		}

		if (userIDField.getText().trim().isEmpty()) {
			errors.add("Username is empty");
		}

		if (passwordField.getText().trim().isEmpty()) {
			errors.add("Password is empty");
		}

		// Check if the username has been taken already
		if (Application.getApplication().getUsers().stream().map(User::getUsername)
				.anyMatch(userIDField.getText().trim()::equals)) {
			errors.add("Username is already taken");
		}

		// If there are errors
		if (errors.size() > 0) {
			// Alert the user of the errors
			Alert errorDialog = new Alert(AlertType.ERROR, "Error");

			errorDialog.setHeaderText(
					"You have the following errors: \n - " + errors.stream().collect(Collectors.joining("\n - ")));

			errorDialog.showAndWait().get();
		}

		// Return true if no errors
		return errors.size() == 0;
	}
}

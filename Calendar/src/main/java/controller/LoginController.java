package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import model.User;

// Login Controller - Controls the login page
public class LoginController {
	// Fields

	// Uses FXML CDI (Dependancy Injection) to inject the fields into this class on
	// load
	@FXML
	private TextField userIDField;
	@FXML
	private PasswordField passwordField;

	@FXML
	private void initialize() {
		// Auto fill user id fields
		User user;
		if ((user = Application.getApplication().getCurrentUser()) != null) {
			userIDField.setText(user.getUsername());
			passwordField.setText(user.getPassword());
		}
	}

	// Action performed on back button click
	@FXML
	private void backAction() {
		// Navigate to the start screen
		Application.getApplication().navigate("/view/WelcomeView.fxml", "Welcome");
	}

	// Action performed on login button click
	@FXML
	private void loginAction() {
		// Get the validated user
		User user = validateFields();

		// If the user is invalid - return
		if (user == null)
			return;

		// Set the current user
		Application.getApplication().setCurrentUser(user);

		// Navigate to the home page
		Application.getApplication().navigate("/view/MainView.fxml", "Home");
	}

	// Retrieves a valid user based on the fields
	private User validateFields() {
		// Create a list of errors in the case there are
		List<String> errors = new ArrayList<>();

		// If any of the fields are empty append the errors to the list
		if (userIDField.getText().trim().isEmpty()) {
			errors.add("Username is empty");
		}

		if (passwordField.getText().trim().isEmpty()) {
			errors.add("Password is empty");
		}

		// If there are errors at this point
		if (errors.size() > 0) {
			// Alert the user of the errors
			Alert errorDialog = new Alert(AlertType.ERROR, "Error");

			errorDialog.setHeaderText(
					"You have the following errors: \n - " + errors.stream().collect(Collectors.joining("\n - ")));

			// Make the user acknowledge
			errorDialog.showAndWait().get();

			// Exit
			return null;
		}

		// Go through all the users and check whether a user has the same username or
		// password
		User user = Application.getApplication().getUsers().stream()
				.filter(v -> v.getUsername().equals(userIDField.getText().trim()))
				.filter(v -> v.getPassword().equals(passwordField.getText().trim())).findFirst().orElse(null);

		// If there isnt a user with the userid and password
		if (user == null) {
			// Alert the user that their credentials are invalid
			Alert errorDialog = new Alert(AlertType.ERROR, "Error");

			errorDialog.setHeaderText("Invalid Credentials");

			// Make the user acknowledge
			errorDialog.showAndWait().get();

			// Exit
			return null;
		}

		// Return the validated user
		return user;
	}

}

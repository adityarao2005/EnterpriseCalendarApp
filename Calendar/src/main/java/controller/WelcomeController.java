package controller;

import application.Application;
import javafx.fxml.FXML;

// Welcome page Controller
public class WelcomeController {

	// Action performed when login button is clicked
	@FXML
	private void loginAction() {
		// Navigate to the login page
		Application.getApplication().navigate("/view/SignInView.fxml", "Login");
	}

	// Action performed when register button is clicked
	@FXML
	private void registerAction() {
		// Navigate to the register page
		Application.getApplication().navigate("/view/Register.fxml", "Register");

	}

}

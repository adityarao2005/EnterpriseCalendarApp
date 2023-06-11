package application;

import java.util.List;
import java.util.stream.Collectors;

import controller.DialogController;
import controller.dao.FileDAOController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.User;

// Application class - deals and handles everything related to application level logic such as
// navigation, holding common data structures, loading and unloading files, and showing the main frame.
// THis class is a Singleton, for the reason that there will always only be one instance of this class
public class Application extends javafx.application.Application {
	// Fields

	// Store the main window
	private Stage primaryStage;
	// Store the singleton application
	private static Application application;
	// Store the list of users
	private List<User> users;
	// Store the current user
	private User currentUser;

	private List<String> errors;

	// Constructor
	public Application() {
		// Set value of singleton
		application = this;
	}

	// Initializes application - handles all the data loading
	@Override
	public void init() throws Exception {
		// XXX DEBUG:
		System.out.println("Init");

		// Load users
		users = FileDAOController.readUsers();

		// Get current user
		currentUser = FileDAOController.readCurrentUser(users);
	}

	// Starts the application - Passes the main window
	@Override
	public void start(Stage primaryStage) {
		// XXX DEBUG:
		System.out.println("Starting");

		// Set the main window
		this.primaryStage = primaryStage;

		// Show initial UI
		showView();
	}

	// Called when application stops - Handle unloading data and cleaning up
	// connections
	@Override
	public void stop() throws Exception {
		super.stop();

		// Write all the users back to their files
		FileDAOController.writeUsers(users);

		// Write the current user
		FileDAOController.writeCurrentUser(currentUser);

		// XXX DEBUG:
		System.out.println("Stopping");
	}

	// Shows the initial view
	private void showView() {

		// Check if there was no user logged in when last closed
		// If so - navigate to the start screen, otherwise navigate to home screen
		if (currentUser == null) {
			navigate("/view/WelcomeView.fxml", "Welcome");
		} else {
			navigate("/view/SignInView.fxml", "Home");
		}

		// Show the main window
		primaryStage.show();

	}

	// Navigates to a particular FXML view file
	public void navigate(String url, String title) {
		try {
			// Retrieves the container from the FXML file
			Parent parent = FXMLLoader.load(this.getClass().getResource(url));

			// Sets the scene
			Scene scene = new Scene(parent);
			primaryStage.setScene(scene);
			// Set the title
			primaryStage.setTitle(title);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Navigates to a particular FXML view file
	public <R> R dialog(String url, String title) {
		return dialog(url, title, null);

	}

	// Navigates to a particular FXML view file
	@SuppressWarnings("unchecked")
	public <R> R dialog(String url, String title, Callback<Class<?>, Object> controllerFactory) {
		try {
			// Retrieves the container from the FXML file
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource(url));
			loader.setControllerFactory(controllerFactory);

			// Get the fxml value
			DialogPane parent = loader.load();

			// Create a new dialog
			Dialog<R> dialog = new Dialog<>();
			dialog.setDialogPane(parent);

			// Create a result converter
			dialog.setResultConverter(buttonType -> {
				switch (buttonType.getButtonData()) {
				case APPLY, OK_DONE, YES, FINISH, RIGHT: // If we are given the go-ahead then return whatever the fxml
															// loader says
					return ((DialogController<R>) loader.getController()).getResult();
				default: // Otherwise return null
					return null;
				}
			});

			// Create the result
			R result;

			// Do while loop to get result
			do {
				// Get result from dialog
				result = dialog.showAndWait().orElseGet(null);

				// If the result is null
				if (result == null) {
					// Get the errors if any
					if (Application.getApplication().getErrors() != null) {

						// Alert the user of the errors
						Alert errorDialog = new Alert(AlertType.ERROR, "Error");

						errorDialog.setHeaderText("You have the following errors: \n - " + Application.getApplication()
								.getErrors().stream().collect(Collectors.joining("\n - ")));

						// Make the user acknowledge
						errorDialog.showAndWait().get();

						// Set the errors back to null
						Application.getApplication().setErrors(null);
					} else // If no errors found, then the client must have closed or dismissed this dialog
							// so return null
						return null;
				}
				// Keep on performing this until we get a result
			} while (result == null);

			// Return the result
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}

	// Getters and Setters
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static Application getApplication() {
		return application;
	}

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}

}

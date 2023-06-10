package application;

import java.util.List;

import controller.DialogController;
import controller.dao.FileDAOController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
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
	@SuppressWarnings("unchecked")
	public <R> R dialog(String url, String title) {
		try {
			// Retrieves the container from the FXML file
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource(url));

			DialogPane parent = loader.load();

			// Create a new dialog
			Dialog<R> dialog = new Dialog<>();
			dialog.setDialogPane(parent);

			dialog.setResultConverter(buttonType -> {
				switch (buttonType.getButtonData()) {
				case APPLY, OK_DONE, YES, FINISH, RIGHT:
					return ((DialogController<R>) loader.getController()).getResult();
				default:
					return null;
				}
			});

			return dialog.showAndWait().orElseGet(null);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

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

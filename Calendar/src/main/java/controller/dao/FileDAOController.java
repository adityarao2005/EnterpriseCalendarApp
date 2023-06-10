package controller.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.SimpleStringProperty;
import model.User;

// File Data Access Object Controller - DAO means a class or service which is used to access the raw data from file
// Access the raw user data from the file
public class FileDAOController {
	// Stored locations
	private static final String USERS_FILE = "users-secrets/users.data";
	private static final String CURRENT_USER_FILE = "users-secrets/currentUser.data";

	// Reads the users
	@SuppressWarnings("unchecked")
	public static List<User> readUsers() throws IOException, ClassNotFoundException {
		// Create a users list
		List<User> users = new ArrayList<>();

		// Get the users file pointer
		File file = new File(USERS_FILE);

		// If the file exists
		if (file.exists()) {
			// Read the list from the file instead
			try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(file))) {

				users = (List<User>) input.readObject();

			}
		} else {
			// Otherwise create a new directory called "users-secrets"
			new File("users-secrets").mkdir();

			// Create the file and create the current users file
			file.createNewFile();
			new File(CURRENT_USER_FILE).createNewFile();

			// write the empty list to populate some data
			writeUsers(users);
		}

		// Return the users
		return users;
	}

	// Writes the users list to the file
	public static void writeUsers(List<User> users) {
		// Gets the instance of the object writer
		try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(new File(USERS_FILE)))) {

			// Write the list
			output.writeObject(users);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Read the current user from the file
	public static User readCurrentUser(List<User> users) throws FileNotFoundException, IOException {
		// Create a string property - Unfortunately you cannot access variables that
		// have been reassigned in lambda params
		SimpleStringProperty userName = new SimpleStringProperty("");

		// Read the users name from the file
		try (BufferedReader reader = new BufferedReader(new FileReader(new File(CURRENT_USER_FILE)))) {
			userName.set(reader.readLine());
		}

		// Check whether there is a user of that name otherwise return null
		return users.stream().filter(user -> user.getUsername().equals(userName.get())).findAny().orElse(null);
	}

	// Write the current user to file
	public static void writeCurrentUser(User user) throws FileNotFoundException {
		// Get the instance of a filewriter and print the username into the file if the
		// user exists
		try (PrintWriter writer = new PrintWriter(new File(CURRENT_USER_FILE))) {
			// Print the user
			if (user != null)
				writer.println(user.getUsername());
			else
				// Print nothing but still print
				writer.println();
		}
	}
}

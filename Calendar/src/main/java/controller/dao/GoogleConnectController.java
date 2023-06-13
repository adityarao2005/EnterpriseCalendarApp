package controller.dao;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Set;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.ClassroomScopes;
import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.CourseWork;
import com.google.api.services.classroom.model.ListCourseWorkResponse;
import com.google.api.services.classroom.model.ListCoursesResponse;
import com.google.api.services.classroom.model.ListStudentSubmissionsResponse;
import com.google.api.services.classroom.model.StudentSubmission;

import model.User;
import model.User.GoogleProfile;

// Handles Google authentication
public class GoogleConnectController {
	// A bunch of useful constants
	private static final String APPLICATION_NAME = "ICS Calendar App";
	private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
	private static final String TOKENS_DIRECTORY_PATH = "users-secrets/tokens";

	private static final Set<String> SCOPES = ClassroomScopes.all();
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

	private static final GoogleClientSecrets CLIENT_SECRETS;

	// Create the HTTP Transport layer
	private static final NetHttpTransport HTTP_TRANSPORT;

	static {

		try {
			HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	// Static initializer, when the class is created
	static {
		try {
			// Load google client secrets
			InputStream in = GoogleConnectController.class.getResourceAsStream(CREDENTIALS_FILE_PATH);

			// Error checking
			if (in == null) {
				throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
			}

			// Set the client secrets
			CLIENT_SECRETS = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	// Factory credential getter, gets the user to signin through google
	private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT, String username)
			throws IOException {
		// Build flow and trigger user authorization request.
		// Create an auth flow
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				CLIENT_SECRETS, SCOPES)
				// Store it in TOKENTS_DIR_PATH/USERNAME_token
				.setCredentialDataStore(
						new FileDataStoreFactory(new File(TOKENS_DIRECTORY_PATH)).getDataStore(username + "_token"))
				// Access it offline
				.setAccessType("offline").build();
		// Create a reciever
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();

		// Authorize the user
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	// Links the google account
	public static void createGoogleUser(User user) throws GeneralSecurityException, IOException {

		// Create a google profile
		GoogleProfile profile = new GoogleProfile(
				String.format("%s/%s_token", TOKENS_DIRECTORY_PATH, user.getUsername()));

		// Set the credentials
		profile.setClassroom(
				new Classroom.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, user.getUsername()))
						.setApplicationName(APPLICATION_NAME).build());

		// Get user profile
		user.setProfile(profile);
	}

	// Links the google account
	public static void retrieveClassroom(User user) throws GeneralSecurityException, IOException {

		// Create a google profile
		user.getProfile().setClassroom(
				new Classroom.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT, user.getUsername()))
						.setApplicationName(APPLICATION_NAME).build());

	}

	// List courses
	public static List<Course> listCourses(User user) throws IOException {

		// List the first 10 courses that the user has access to.
		ListCoursesResponse response = user.getProfile().getClassroom().courses().list().execute();

		return response.getCourses();
	}

	// list course work
	public static List<CourseWork> listCourseWork(User user, Course course) throws IOException {

		// List the first 10 courses that the user has access to.
		ListCourseWorkResponse response = user.getProfile().getClassroom().courses().courseWork().list(course.getId())
				.execute();

		return response.getCourseWork();
	}

	// List student submissions
	public static List<StudentSubmission> listStudentSubmission(User user, CourseWork work) throws IOException {

		// List the first 10 courses that the user has access to.
		ListStudentSubmissionsResponse response = user.getProfile().getClassroom().courses().courseWork()
				.studentSubmissions().list(work.getCourseId(), work.getId()).setUserId("me").execute();

		return response.getStudentSubmissions();
	}
}

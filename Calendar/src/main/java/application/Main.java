package application;

/*
Name(s): Aditya Rao
Date: June 14th, 2023
Course Code: ICS4U1-03, Mr.Fernandes
Title: ICS Calendar App
Description: // TODO: keep description
Features:
 - Ability to login and register
 - Ability to use your google account for loading calendars
 - Ability to create, manipulate, delete and read tasks, reminders, and assignments (except for google classroom assignments)
 - Ability to add tasks to assignments to organize and plan ahead of time
 - Ability to work on your tasks with pomodoro feature
 - Ability to view tasks of other days
Major Skills:
 - Algorithms
 - OOP
 - MVC design pattern
 - New apis
 	- JavaFX GUI API
 	- Google Classroom API
 - Serialization and File IO
 - Observable Design pattern and Data binding
 - Singleton Design pattern
 - Factory, Builder, Builder Factory design pattern
 - Using non java code as a view while having java controller (FXML)
 - CSS
 - Model-Skin-Behavior Architecture for controls
 - Maven for dependancy and API management
Areas of Concern:
 - Imported API: JavaFX, Google Classroom API
 - Instructions: 
 	1. Select the project (i.e Calendar)
 	2. Right click and open the Run-As menu and click on "Maven Build..."
 	3. A pop up shows, in the "Goals" text field, enter "clean javafx:run"
 	4. Click "Apply" and then Click "Run"
 - Notes: Unfortunately since the YRDSB admin have a list of authorized google apps which are allowed to have the student as a client and since my app is not in that list, GAPPS accounts or any admin controlled accounts will not be able to be used. Use personal account instead.
*/

// The main class - holds a method to run the program
public class Main {

	// Runs the program
	public static void main(String[] args) {
		Application.launch(Application.class, args);

	}

}

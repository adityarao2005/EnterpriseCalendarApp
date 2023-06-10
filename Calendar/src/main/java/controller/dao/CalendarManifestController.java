package controller.dao;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.CourseWork;
import com.google.api.services.classroom.model.StudentSubmission;

import model.User;
import model.events.Assignment;
import model.events.RCalendar;
import model.events.RTask;
import model.events.Reminder;

public class CalendarManifestController {

	public static void refreshGoogleCalendars(User user) throws IOException {

		// Get the list of courses from the user
		List<Course> courses = GoogleConnectController.listCourses(user);

		// Go through all the courses
		for (Course course : courses) {
			// Create a calendar associated with the course from the users calendar provided
			// that it is there
			RCalendar calendar = user.getCalendars().stream().filter(new RCalendar(course.getName())::identical)
					.findFirst().orElseGet(() -> {
						// If there isnt one, create your own
						RCalendar cal = new RCalendar(course.getName());
						cal.setClassroomLoaded(true);
						cal.setReminders(new ArrayList<>());

						// Add to the users calendar
						user.getCalendars().add(cal);

						return cal;
					});

			// Get a list of coursework
			List<CourseWork> courseWork = GoogleConnectController.listCourseWork(user, course);

			// Go through all the course work for this course
			for (CourseWork work : courseWork) {

				// If there is already this assignment in our calendar, skip it
				if (calendar.getReminders().stream().map(m -> (Assignment) m)
						.anyMatch(new Assignment(work.getTitle())::identical))
					continue;

				// Get the date and time
				LocalDate date = LocalDate.of(work.getDueDate().getYear(), work.getDueDate().getMonth(),
						work.getDueDate().getDay());
				LocalTime time = LocalTime.of(work.getDueTime().getHours(), work.getDueTime().getMinutes());

				// Create an instance of the assignment
				Assignment assignment = new Assignment();
				assignment.setCalendar(calendar);
				assignment.setDue(LocalDateTime.of(date, time));
				assignment.setReminder(LocalTime.MIDNIGHT);
				assignment.setName(work.getTitle());
				assignment.setSchedule(new ArrayList<>());

				List<StudentSubmission> submissions = GoogleConnectController.listStudentSubmission(user, work);

				if (submissions.isEmpty()) {

					assignment.setCompleted(false);
				} else {

					StudentSubmission submission = submissions.get(0);

					assignment.setCompleted(submission.getState().equals("TURNED_IN"));
				}

				// Add assignment to the calendar
				calendar.getReminders().add(assignment);

			}

		}

	}

	public static List<RTask> getUncompletedTasks(User user) {
		// Create a container list
		List<RTask> tasks = new ArrayList<>();

		// Go through the calendar and its reminder events
		for (RCalendar cal : user.getCalendars()) {

			for (Reminder rem : cal.getReminders()) {

				// If the reminder is a completeable reminder and its not yet complete then add
				// the task to the list
				if (rem instanceof RTask) {
					RTask task = (RTask) rem;

					if (!task.isCompleted()) {
						tasks.add(task);
					}
				}
			}
		}

		// Sort the tasks by their due date and return them
		tasks.sort(Comparator.comparing(RTask::finishBy));

		return tasks;
	}

	public static List<Assignment> getUnattendedAssignments(User user) {
		// Create a container list
		List<Assignment> tasks = new ArrayList<>();

		// Go through the calendar and its reminder events
		for (RCalendar cal : user.getCalendars()) {

			for (Reminder rem : cal.getReminders()) {

				// If the reminder is a completeable reminder and its not yet complete then add
				// the task to the list
				if (rem instanceof Assignment) {
					Assignment task = (Assignment) rem;

					if (!task.isCompleted() && task.getSchedule().isEmpty()) {
						tasks.add(task);
					}
				}
			}
		}

		// Sort the tasks by their due date and return them
		tasks.sort(Comparator.comparing(Assignment::finishBy));

		return tasks;

	}
}

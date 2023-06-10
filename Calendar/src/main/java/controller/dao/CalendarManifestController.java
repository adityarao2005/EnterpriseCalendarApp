package controller.dao;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;

import com.google.api.services.classroom.model.Course;
import com.google.api.services.classroom.model.CourseWork;

import javafx.util.converter.LocalDateTimeStringConverter;
import model.User;
import model.events.Assignment;
import model.events.RCalendar;
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
				assignment.setCompleted(!GoogleConnectController.listStudentSubmission(user, work).isEmpty());

				// Add assignment to the calendar
				calendar.getReminders().add(assignment);

			}

		}

		// Loop through all the calendars
		for (RCalendar calendar : user.getCalendars()) {
			System.out.println("Work to do: " + calendar.getName());

			for (Reminder reminder : calendar.getReminders()) {
				if (reminder instanceof Assignment) {
					Assignment assignment = (Assignment) reminder;

					LocalDateTimeStringConverter dConverter = new LocalDateTimeStringConverter(FormatStyle.MEDIUM,
							FormatStyle.SHORT);

					System.out.printf("%s Due on %s%n", assignment.getName(), dConverter.toString(assignment.getDue()));

				}
			}
		}
	}
}

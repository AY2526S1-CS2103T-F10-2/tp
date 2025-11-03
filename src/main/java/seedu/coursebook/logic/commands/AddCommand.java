package seedu.coursebook.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.coursebook.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.coursebook.logic.parser.CliSyntax.PREFIX_COURSE;
import static seedu.coursebook.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.coursebook.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.coursebook.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.coursebook.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.HashSet;
import java.util.Set;

import seedu.coursebook.commons.util.ToStringBuilder;
import seedu.coursebook.logic.CommandHistory;
import seedu.coursebook.logic.Messages;
import seedu.coursebook.logic.commands.exceptions.CommandException;
import seedu.coursebook.model.Model;
import seedu.coursebook.model.course.Course;
import seedu.coursebook.model.course.CourseColor;
import seedu.coursebook.model.person.Person;


/**
 * Adds a person to the address book.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a person to the address book. "
            + "Parameters: "
            + PREFIX_NAME + "NAME "
            + PREFIX_PHONE + "PHONE "
            + PREFIX_EMAIL + "EMAIL "
            + PREFIX_ADDRESS + "ADDRESS "
            + "[" + PREFIX_TAG + "TAG]...\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + PREFIX_TAG + "friends "
            + PREFIX_TAG + "owesMoney "
            + PREFIX_COURSE + "CS2103T,green "
            + PREFIX_COURSE + "CS2101";

    public static final String MESSAGE_SUCCESS = "New person added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON =
            "This person's name/phone/email already exists in the address book.";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model, CommandHistory history) throws CommandException {
        requireNonNull(model);

        if (model.hasPerson(toAdd)) {
            throw new CommandException(MESSAGE_DUPLICATE_PERSON);
        }

        // Enforce global course color consistency and conflict handling
        Set<Course> updatedCourses = new HashSet<>();
        for (Course course : toAdd.getCourses()) {
            CourseColor existingColor = model.getFilteredCourseList().stream()
                    .filter(c -> c.courseCode.equalsIgnoreCase(course.courseCode))
                    .map(c -> c.color)
                    .findFirst()
                    .orElse(null);

            // Check if course color is the default GREEN (which means user didn't specify a color)
            boolean isDefaultGreen = (course.color == CourseColor.GREEN);

            // Priority: Preserve existing color if course exists and user didn't specify a color
            if (existingColor != null && isDefaultGreen && existingColor != CourseColor.GREEN) {
                // Course exists with a color (not GREEN), but user didn't specify one - use existing color
                updatedCourses.add(new Course(course.courseCode, existingColor));
            } else if (existingColor != null && course.color != null && existingColor != course.color) {
                // User explicitly specified a different color than existing - update globally to user's choice
                model.setCourseColor(course.courseCode, course.color);
                updatedCourses.add(new Course(course.courseCode, course.color));
            } else if (existingColor == null && isDefaultGreen) {
                // New course, no color specified - default to GREEN
                model.setCourseColor(course.courseCode, CourseColor.GREEN);
                updatedCourses.add(new Course(course.courseCode, CourseColor.GREEN));
            } else if (existingColor != null && existingColor == course.color) {
                // Existing color matches what user specified - no change needed
                updatedCourses.add(course);
            } else {
                // No conflict, use course as is (e.g., existingColor is GREEN and course.color is GREEN)
                updatedCourses.add(course);
            }
        }

        // Create person with updated courses that have correct colors enforced
        Person personToAdd = new Person(toAdd.getName(), toAdd.getPhone(), toAdd.getEmail(),
                toAdd.getAddress(), toAdd.getTags(), updatedCourses, toAdd.getBirthday(), toAdd.isFavourite());
        model.addPerson(personToAdd);
        model.commitCourseBook();
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(personToAdd)));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof AddCommand // instanceof handles nulls
                && toAdd.equals(((AddCommand) other).toAdd));
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}

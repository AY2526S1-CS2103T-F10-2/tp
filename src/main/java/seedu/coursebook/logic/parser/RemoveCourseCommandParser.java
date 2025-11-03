package seedu.coursebook.logic.parser;

import static seedu.coursebook.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.coursebook.logic.parser.CliSyntax.PREFIX_COURSE;
import static seedu.coursebook.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;

import java.util.Set;

import seedu.coursebook.commons.core.index.Index;
import seedu.coursebook.logic.commands.RemoveCourseCommand;
import seedu.coursebook.logic.parser.exceptions.ParseException;
import seedu.coursebook.model.course.Course;

/**
 * Parses input arguments and creates a new RemoveCourseCommand object
 */
public class RemoveCourseCommandParser implements Parser<RemoveCourseCommand> {

    @Override
    public RemoveCourseCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_COURSE);

        if (argMultimap.getPreamble().isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveCourseCommand.MESSAGE_USAGE));
        }

        Index index;
        String preamble = argMultimap.getPreamble();
        try {
            index = ParserUtil.parseIndex(preamble);
        } catch (ParseException pe) {
            // If preamble contains non-digit characters, it's a format error
            if (pe.getMessage().equals(MESSAGE_INVALID_INDEX) && !preamble.trim().matches("\\d+")) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        RemoveCourseCommand.MESSAGE_USAGE));
            }
            // Otherwise, re-throw the original exception (e.g., negative index, index out of range)
            throw pe;
        }

        if (argMultimap.getAllValues(PREFIX_COURSE).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemoveCourseCommand.MESSAGE_USAGE));
        }

        Set<Course> courses = ParserUtil.parseCourses(argMultimap.getAllValues(PREFIX_COURSE));
        return new RemoveCourseCommand(index, courses);
    }
}

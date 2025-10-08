package seedu.coursebook.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.coursebook.commons.exceptions.IllegalValueException;
import seedu.coursebook.model.CourseBook;
import seedu.coursebook.model.ReadOnlyCourseBook;
import seedu.coursebook.model.person.Person;

/**
 * An Immutable CourseBook that is serializable to JSON format.
 */
@JsonRootName(value = "CourseBook")
class JsonSerializableCourseBook {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableCourseBook} with the given persons.
     */
    @JsonCreator
    public JsonSerializableCourseBook(@JsonProperty("persons") List<JsonAdaptedPerson> persons) {
        this.persons.addAll(persons);
    }

    /**
     * Converts a given {@code ReadOnlyCourseBook} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableCourseBook}.
     */
    public JsonSerializableCourseBook(ReadOnlyCourseBook source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
    }

    /**
     * Converts this address book into the model's {@code CourseBook} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public CourseBook toModelType() throws IllegalValueException {
        CourseBook courseBook = new CourseBook();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (courseBook.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            courseBook.addPerson(person);
        }
        return courseBook;
    }

}

package seedu.coursebook.logic.parser;

import static seedu.coursebook.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.coursebook.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.coursebook.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.coursebook.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.coursebook.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.coursebook.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.coursebook.logic.commands.FindCommand;
import seedu.coursebook.logic.parser.exceptions.ParseException;
import seedu.coursebook.model.person.Name;
import seedu.coursebook.model.person.PersonContainsKeywordsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        // Allowed prefixes: n/, p/, e/, a/, t/
        List<String> allowedPrefixes = Arrays.asList("n/", "p/", "e/", "a/", "t/");

        // Check the entire input for invalid prefixes before tokenization
        // This catches invalid prefixes even when mixed with valid ones
        String[] allTokens = trimmedArgs.split("\\s+");
        for (String token : allTokens) {
            // Check if token looks like a short prefix (1-2 letters + /)
            if (token.matches("[A-Za-z]{1,2}/.*")) {
                String prefix = token.substring(0, token.indexOf('/') + 1);
                if (!allowedPrefixes.contains(prefix)) {
                    // This looks like an invalid prefix, reject it
                    throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
                }
            }
            // Tokens with 3+ letters before / (like "Smith/Johnson") are allowed as they're clearly names
        }

        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args,
                PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_TAG);

        List<String> nameKeywords = new ArrayList<>();
        List<String> phoneKeywords = new ArrayList<>();
        List<String> emailKeywords = new ArrayList<>();
        List<String> addressKeywords = new ArrayList<>();
        List<String> tagKeywords = new ArrayList<>();

        // Collect all prefixed values (each occurrence split by whitespace into individual keywords)
        for (String value : argMultimap.getAllValues(PREFIX_NAME)) {
            nameKeywords.addAll(Arrays.asList(value.trim().split("\\s+")));
        }
        for (String value : argMultimap.getAllValues(PREFIX_PHONE)) {
            phoneKeywords.addAll(Arrays.asList(value.trim().split("\\s+")));
        }
        for (String value : argMultimap.getAllValues(PREFIX_EMAIL)) {
            emailKeywords.addAll(Arrays.asList(value.trim().split("\\s+")));
        }
        for (String value : argMultimap.getAllValues(PREFIX_ADDRESS)) {
            addressKeywords.addAll(Arrays.asList(value.trim().split("\\s+")));
        }
        for (String tagValue : argMultimap.getAllValues(PREFIX_TAG)) {
            tagKeywords.addAll(Arrays.asList(tagValue.trim().split("\\s+")));
        }

        boolean anyPrefixedProvided = !(nameKeywords.isEmpty() && phoneKeywords.isEmpty()
                && emailKeywords.isEmpty() && addressKeywords.isEmpty() && tagKeywords.isEmpty());

        // Reject any preamble when any supported prefix is used
        String preamble = argMultimap.getPreamble().trim();
        if (anyPrefixedProvided && !preamble.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        // Check preamble for invalid prefixes (tokens that look like prefixes but aren't allowed)
        // This prevents inputs like "find b/2020-01-01" or "find z/foo" from being treated as name searches
        // Names with / (like "Smith/Johnson") are allowed as they have 3+ letters before /
        if (!preamble.isEmpty()) {
            String[] preambleTokens = preamble.split("\\s+");
            for (String token : preambleTokens) {
                // Check if token looks like a short prefix (1-2 letters + /)
                // Only reject tokens that look like invalid prefixes, not longer names with /
                if (token.matches("[A-Za-z]{1,2}/.*")) {
                    String prefix = token.substring(0, token.indexOf('/') + 1);
                    if (!allowedPrefixes.contains(prefix)) {
                        // This looks like an invalid prefix, reject it
                        // Note: Names like "Al/Johnson" will be rejected here, but that's acceptable
                        // as they're ambiguous - users can use "Al Johnson" or "n/Al/Johnson" instead
                        throw new ParseException(
                                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
                    }
                }
                // Tokens with 3+ letters before / (like "Smith/Johnson") are allowed as they're clearly names
            }
        }

        if (!anyPrefixedProvided) {
            // Fallback to legacy behavior: treat entire args as name keywords
            String[] keywords = trimmedArgs.split("\\s+");
            nameKeywords.addAll(Arrays.asList(keywords));
        }

        // Validate name tokens match the same characters allowed in names when provided,
        // whether via prefixes or fallback
        if (!nameKeywords.isEmpty()) {
            boolean invalidNameToken = nameKeywords.stream()
                    .anyMatch(s -> s == null || s.isBlank() || !s.matches("[\\p{Alnum} '/.,-]+"));
            if (invalidNameToken) {
                throw new ParseException(Name.MESSAGE_CONSTRAINTS);
            }
        }

        // Validate that at least one non-empty keyword exists
        boolean hasAnyKeyword = nameKeywords.stream().anyMatch(s -> !s.isBlank())
                || phoneKeywords.stream().anyMatch(s -> !s.isBlank())
                || emailKeywords.stream().anyMatch(s -> !s.isBlank())
                || addressKeywords.stream().anyMatch(s -> !s.isBlank())
                || tagKeywords.stream().anyMatch(s -> !s.isBlank());

        if (!hasAnyKeyword) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        PersonContainsKeywordsPredicate predicate = new PersonContainsKeywordsPredicate(
                nameKeywords, phoneKeywords, emailKeywords, addressKeywords, tagKeywords);
        return new FindCommand(predicate);
    }

}

package seedu.coursebook.logic.parser;

import static seedu.coursebook.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.coursebook.logic.commands.CommandTestUtil.VALID_REMARK_AMY;
import static seedu.coursebook.logic.commands.CommandTestUtil.VALID_REMARK_BOB;
import static seedu.coursebook.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.coursebook.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.coursebook.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.coursebook.commons.core.index.Index;
import seedu.coursebook.logic.commands.RemarkCommand;
import seedu.coursebook.model.person.Remark;

public class RemarkCommandParserTest {

    private RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_indexSpecified_success() {
        // have remark
        Index targetIndex = INDEX_FIRST_PERSON;
        String userInput = targetIndex.getOneBased() + " r/" + VALID_REMARK_AMY;
        RemarkCommand expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(VALID_REMARK_AMY));
        assertParseSuccess(parser, userInput, expectedCommand);

        // no remark
        userInput = targetIndex.getOneBased() + " r/";
        expectedCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(""));
        assertParseSuccess(parser, userInput, expectedCommand);
    }

    @Test
    public void parse_missingParts_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

        // no index specified
        assertParseFailure(parser, "r/" + VALID_REMARK_AMY, expectedMessage);

        // no prefix specified
        assertParseFailure(parser, "1 " + VALID_REMARK_AMY, expectedMessage);

        // no index and no prefix specified
        assertParseFailure(parser, VALID_REMARK_AMY, expectedMessage);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);

        // negative index
        assertParseFailure(parser, "-5 r/" + VALID_REMARK_AMY, expectedMessage);

        // zero index
        assertParseFailure(parser, "0 r/" + VALID_REMARK_AMY, expectedMessage);

        // invalid arguments being parsed as preamble
        assertParseFailure(parser, "1 some random string", expectedMessage);

        // invalid prefix being parsed as preamble
        assertParseFailure(parser, "1 i/ string", expectedMessage);
    }

    @Test
    public void parse_invalidValue_failure() {
        // empty remark
        assertParseFailure(parser, "1 r/", String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE));
    }
}

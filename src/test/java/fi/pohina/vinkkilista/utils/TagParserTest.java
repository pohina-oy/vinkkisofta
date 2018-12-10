package fi.pohina.vinkkilista.utils;

import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

public class TagParserTest {
    private TagParser parser;

    @Before
    public void setUp() {
        parser = new TagParser();
    }

    @Test
    public void parseReturnsEmptySetWhenInputIsNull() {
        Set<String> output = parser.csvToSet(null);

        assertEquals(0, output.size());
    }

    @Test
    public void parseReturnsEmptySetWhenInputIsEmpty() {
        Set<String> output = parser.csvToSet("");

        assertEquals(0, output.size());
    }

    @Test
    public void parseReturnsEmptySetWhenInputIsNothingButCommas() {
        Set<String> output = parser.csvToSet(",,,,");

        assertEquals(0, output.size());
    }

    @Test
    public void parseReturnsSingleTagCorrectly() {
        testThatInputMatchesOutput(
            "foo",
            "foo"
        );
    }

    @Test
    public void parseReturnsTwoTagsCorrectly() {
        testThatInputMatchesOutput(
            "toaster,box",
            "toaster", "box"
        );
    }

    @Test
    public void parseTrimsSpacesFromMultipleTags() {
        testThatInputMatchesOutput(
            " toaster , box, foo",
            "toaster", "box", "foo"
        );
    }

    @Test
    public void parseReturnsMultipleTagsCorrectly() {
        testThatInputMatchesOutput(
            "comma,separated,video",
            "comma", "separated", "video"
        );
    }

    @Test
    public void parseSkipsEmptyTagAtMiddleCorrectly() {
        testThatInputMatchesOutput(
            "video,,blog",
            "video", "blog"
        );
    }

    @Test
    public void parseParsesTagsWithDashes() {
        testThatInputMatchesOutput(
            "scientific-journal,twitch-stream",
            "scientific-journal", "twitch-stream"
        );
    }

    @Test
    public void parseParsesTagsWithUnderscores() {
        testThatInputMatchesOutput(
            "science_today,foo_bar",
            "science_today", "foo_bar"
        );
    }

    @Test
    public void parseHandlesDuplicates() {
        testThatInputMatchesOutput(
            "developers,developers,developers,developers,"
                + "developers,steve-ballmer,developers,developers,developers,"
                + "developers,developers",
            "developers", "steve-ballmer"
        );
    }

    private void testThatInputMatchesOutput(
        String input,
        String... expectedTags
    ) {
        Set<String> output = parser.csvToSet(input);

        assertEquals(expectedTags.length, output.size());
        assertThat(output, hasItems(expectedTags));
    }
}

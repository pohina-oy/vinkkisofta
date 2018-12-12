package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.InMemoryTagDao;
import fi.pohina.vinkkilista.data_access.TagDao;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static org.hamcrest.CoreMatchers.hasItems;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TagServiceTest {

    private TagService tagService;
    private TagDao tagDao;

    @Before
    public void setUp() {
        tagDao = spy(new InMemoryTagDao());
        tagService = spy(new TagService(tagDao));
    }

    @Test
    public void tagIsCreatedWithValidName() {
        String name = "video";

        Tag created = tagService.findOrCreateTag(name);

        verify(tagDao, times(1)).add(created);

        assertEquals(name, created.getName());
        assertEquals(created, tagDao.findById(created.getId()));
    }

    @Test
    public void tagIsCreatedWithNameSavedInRightFormat() {
        Tag created = tagService.findOrCreateTag("  Scientific    PUBLICAtion     ");

        verify(tagDao, times(1)).add(created);

        assertEquals("scientific publication", created.getName());
        assertEquals(created, tagDao.findById(created.getId()));
    }

    @Test
    public void tagIsNotCreatedWithInvalidName() {
        int totalTags = tagDao.findAll().size();

        Tag tag1 = tagService.findOrCreateTag("");
        Tag tag2 = tagService.findOrCreateTag(null);

        assertEquals(totalTags, tagDao.findAll().size());
        assertEquals(tag1, null);
        assertEquals(tag2, null);
    }

    @Test
    public void tagIsNotCreatedIfItExists() {
        Tag tag1 = tagService.findOrCreateTag("video");
        int totalTags = tagDao.findAll().size();
        Tag tag2 = tagService.findOrCreateTag("video");

        assertEquals(totalTags, tagDao.findAll().size());
        assertEquals(tag1.getId(), tag2.getId());
    }

    @Test
    public void tagsAreCreatedWithValidNames() {
        Set<String> tagNames = new HashSet<>(
                Arrays.asList("video ", "science", "    ", "SCIENCE", "video    games", " BLog", null, ""));
        tagService.findOrCreateTags(tagNames);

        assertEquals(4, tagDao.findAll().size());
        assertEquals("video", tagDao.findByName("video").getName());
        assertEquals("science", tagDao.findByName("science").getName());
        assertEquals("video games", tagDao.findByName("video games").getName());
        assertEquals("blog", tagDao.findByName("blog").getName());
    }

    @Test
    public void tagFromUrlReturnsCorrectTag() {
        String tag = tagService.tagFromUrl(
                "https://www.youtube.com/watch?v=ZgjWOo7IqQY");
        assertEquals("video", tag);

        tag = tagService.tagFromUrl(
                "https://youtu.be/G60llMJepZI");
        assertEquals("video", tag);

        tag = tagService.tagFromUrl(
                "https://tastytreats-blog.blogspot.com/");
        assertEquals("blog", tag);

        tag = tagService.tagFromUrl(
                "https://wordpress.org/showcase/the-dish/");
        assertEquals("blog", tag);

        tag = tagService.tagFromUrl(
                "https://www.suomalainen.com/webapp/wcs"
                + "/stores/servlet/fi/skk/lazarus-p9789513196455--77");
        assertEquals("book", tag);

        tag = tagService.tagFromUrl(
                "https://ieeexplore.ieee.org/document/8543874");
        assertEquals("scientific publication", tag);

        tag = tagService.tagFromUrl(
                "https://dl.acm.org/citation.cfm?id=3292530&picked=prox");
        assertEquals("scientific publication", tag);
    }
    
    @Test
    public void toValidatedSetReturnsEmptySetWhenInputIsNull() {
        Set<String> output = tagService.toValidatedSet(null);

        assertEquals(0, output.size());
    }

    @Test
    public void toValidatedSetReturnsEmptySetWhenInputIsEmpty() {
        Set<String> output = tagService.toValidatedSet("");

        assertEquals(0, output.size());
    }

    @Test
    public void toValidatedSetReturnsEmptySetWhenInputIsNothingButCommas() {
        Set<String> output = tagService.toValidatedSet(",,,,");

        assertEquals(0, output.size());
    }

    @Test
    public void toValidatedSetReturnsSingleTagCorrectly() {
        testThatInputMatchesOutput(
            "foo",
            "foo"
        );
    }

    @Test
    public void toValidatedSetReturnsTwoTagsCorrectly() {
        testThatInputMatchesOutput(
            "toaster,box",
            "toaster", "box"
        );
    }

    @Test
    public void toValidatedSetTrimsSpacesFromMultipleTags() {
        testThatInputMatchesOutput(
            " toaster , box, foo",
            "toaster", "box", "foo"
        );
    }

    @Test
    public void toValidatedSetReturnsMultipleTagsCorrectly() {
        testThatInputMatchesOutput(
            "comma,separated,video",
            "comma", "separated", "video"
        );
    }

    @Test
    public void toValidatedSetSkipsEmptyTagAtMiddleCorrectly() {
        testThatInputMatchesOutput(
            "video,,blog",
            "video", "blog"
        );
    }

    @Test
    public void toValidatedSetParsesTagsWithDashes() {
        testThatInputMatchesOutput(
            "scientific-journal,twitch-stream",
            "scientific-journal", "twitch-stream"
        );
    }

    @Test
    public void toValidatedSetParsesTagsWithUnderscores() {
        testThatInputMatchesOutput(
            "science_today,foo_bar",
            "science_today", "foo_bar"
        );
    }

    @Test
    public void toValidatedSetHandlesDuplicates() {
        testThatInputMatchesOutput(
            "developers,developers,developers,developers,"
                + "developers,steve-ballmer,developers,developers,developers,"
                + "developers,developers",
            "developers", "steve-ballmer"
        );
    }

    private void testThatInputMatchesOutput(String input, String... expectedTags) {
        Set<String> output = tagService.toValidatedSet(input);

        assertEquals(expectedTags.length, output.size());
        assertThat(output, hasItems(expectedTags));
    }

}

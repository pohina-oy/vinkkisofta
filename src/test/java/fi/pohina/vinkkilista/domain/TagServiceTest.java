package fi.pohina.vinkkilista.domain;

import fi.pohina.vinkkilista.data_access.InMemoryTagDao;
import fi.pohina.vinkkilista.data_access.TagDao;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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

}

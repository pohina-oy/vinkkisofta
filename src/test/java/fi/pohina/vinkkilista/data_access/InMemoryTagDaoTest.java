package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Tag;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.*;

public class InMemoryTagDaoTest {

    private InMemoryTagDao tagDao;

    @Before
    public void setUp() {
        tagDao = new InMemoryTagDao();

        Tag first = new Tag(
                "first-id",
                "first"
        );

        Tag second = new Tag(
                "second-id",
                "second"
        );

        tagDao.add(first);
        tagDao.add(second);
    }

    @Test
    public void findByIdFindsInitiallyAddedTagById() {
        String expectedId = "first-id";

        Tag foundTag = tagDao.findById(expectedId);

        assertEquals(expectedId, foundTag.getId());
    }

    @Test
    public void findByIdDoesNotFindNonExistentId() {
        Tag foundTag = tagDao.findById("intellij-is-cool");

        assertNull(foundTag);
    }
    @Test
    public void findByIdDoesNotFindNonExistentName() {
        Tag foundTag = tagDao.findByName("intellij-is-cool");

        assertNull(foundTag);
    }

    @Test
    public void findAllCorrectlyFindsInitiallyAddedTags() {
        List<String> foundIds = tagDao.findAll()
                .stream()
                .map(Tag::getId)
                .collect(Collectors.toList());

        assertEquals(2, foundIds.size());
        assertThat(foundIds, hasItems("first-id", "second-id"));
    }

    @Test
    public void findAllFindsPreviousTagsAfterAdd() {
        String newId = "third-id";
        Tag newEntry = new Tag(
                newId,
                "third"
        );

        tagDao.add(newEntry);

        List<String> bookmarkIds = tagDao.findAll()
                .stream()
                .map(Tag::getId)
                .collect(Collectors.toList());

        assertThat(bookmarkIds, hasItem("first-id"));
        assertThat(bookmarkIds, hasItem("second-id"));
        assertThat(bookmarkIds, hasItem(newId));
    }

    @Test
    public void addCorrectlyAddsNewTag() {
        String addedId = "bookmark-3";
        Tag newEntry = new Tag(
                addedId,
                "new tag"
        );

        tagDao.add(newEntry);

        assertEquals(addedId, tagDao.findById(addedId).getId());
        assertThat(tagDao.findAll(), hasItem(newEntry));
    }
}

package fi.pohina.vinkkilista.domain;

import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class TagTest {

    @Test
    public void constructorAndGettersWorkCorrectly() {
        String id = "id",
                name = "name1";


        Tag tag = new Tag(id, name);

        assertEquals(id, tag.getId());
        assertEquals(name, tag.getName());
    }

    @Test
    public void tagEqualsWorksCorrectly() {

        Tag tag1 = new Tag("id", "name");
        Tag tag2 = new Tag("id", "name");
        Tag tag3 = new Tag("foo", "bar");
        Tag tag4 = new Tag("id", "bar");

        assertEquals(true, tag1.equals(tag2));
        assertEquals(true, tag2.equals(tag1));

        assertEquals(false, tag1.equals(tag3));
        assertEquals(false, tag3.equals(tag1));
        assertEquals(false, tag3.equals(tag2));
        assertEquals(false, tag2.equals(tag3));

        assertEquals(false, tag4.equals(tag1));
        assertEquals(false, tag1.equals("foobar"));
        assertEquals(false, tag1.equals(null));
    }

    @Test
    public void tagHashCodeReturnsCorrectValue() {
        Tag tag1 = new Tag("id", "name");
        Tag tag2 = new Tag("id", "name");

        int hash = Objects.hash(tag1.getId(), tag1.getName());

        assertEquals(hash, tag1.hashCode());
        assertEquals(hash, tag2.hashCode());
    }
}

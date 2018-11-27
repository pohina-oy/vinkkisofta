package fi.pohina.vinkkilista.domain;

import java.util.Objects;

/**
 * Represents a named bookmark tag.
 */
public class Tag {

    private final String id;
    private final String name;

    /**
     * Initializes a new instance of the {@link Tag} class with the specified
     * ID and name.
     *
     * @param id   the ID of the tag.
     * @param name the name of the tag.
     */
    public Tag(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Tag tag = (Tag) o;
        return Objects.equals(id, tag.id)
            && Objects.equals(name, tag.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}

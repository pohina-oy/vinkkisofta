package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Bookmark;
import fi.pohina.vinkkilista.domain.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface BookmarkDao {

    Bookmark findByID(int id);

    List<Bookmark> findAll();
    
    public ArrayList<Bookmark> findByTagSet(Set<Tag> tags);

    void add(Bookmark bookmark);
}

package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Bookmark;
import java.util.List;

public interface BookmarkDao<T> {

    Bookmark findByID(int id);

    List<Bookmark> findAll();

    void add(Bookmark bookmark);
}

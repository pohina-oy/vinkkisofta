package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Bookmark;
import fi.pohina.vinkkilista.domain.Tag;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Provides an PostgreSQL implementation of the {@link BookmarkDao} interface,
 * backed by a {@link List<Bookmark>}.
 */
public class PostgreBookmarkDao implements BookmarkDao {
    private Connection db;

    public PostgreBookmarkDao(String dbHost, String dbUser, String dbPassowrd, String dbName) {
        String url = "jdbc:postgresql://" + dbHost + "/" + dbName + "?user=" + dbUser + "&password=" + dbPassowrd;
        try { 
            this.db = DriverManager.getConnection(url);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     * This is a linear time operation.
     */
    @Override
    public Bookmark findById(String id) {
        try {
            String query = "SELECT * FROM bookmarks WHERE id = ?";
            PreparedStatement st = this.db.prepareStatement(query);
            st.setString(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.first()) {
                String title = rs.getString("title");
                String url = rs.getString("url");
                String author = rs.getString("author");
                return new Bookmark(id, title, url, author);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Bookmark> findAll() {
        List<Bookmark> bookmarks = new ArrayList<Bookmark>();
        try {
            Statement st = this.db.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM bookmarks");
            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                String url = rs.getString("url");
                String author = rs.getString("author");
                Set<Tag> tags = findBookmarkTags(id);
                bookmarks.add(new Bookmark(
                    id, 
                    title, 
                    url, 
                    author,
                    tags
                ));                
            }
            return bookmarks;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void add(Bookmark bookmark) {
        try {
            String query = "INSERT INTO bookmarks (id, title, url, author) " + " values (?, ? ,? ,?)";
            PreparedStatement st = this.db.prepareStatement(query);
            String bookmarkId = UUID.randomUUID().toString();
            st.setString(1, bookmarkId);
            st.setString(2, bookmark.getTitle());
            st.setString(3, bookmark.getUrl());
            st.setString(4, bookmark.getAuthor());
            st.executeQuery();
            for (Tag t : bookmark.getTags()) {
                addBookmarkTag(t.getName(), bookmarkId);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Gets a tag by name
     * @param tagName
     * @return
     */
    private Tag getTag(String tagName) {
        try {
            String query = "SELECT * FROM tags where name = ?";
            PreparedStatement st = this.db.prepareStatement(query);
            st.setString(1, tagName);
            ResultSet rs = st.executeQuery();
            if (rs.first()) {
                String tagId = rs.getString("id");
                return new Tag(tagId, tagName);
            }
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Creates a tag
     * @param tagName
     * @return
     */
    private String createTag(String tagName) {
        try {
            String query = "INSERT INTO tags (id, name) values (?, ?)";
            PreparedStatement st = this.db.prepareStatement(query);
            String tagId = UUID.randomUUID().toString();
            st.setString(1, tagId);
            st.setString(2, tagName);
            st.execute();
            return tagId;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    /**
     * Links a bookmark and tags    
     * @param tagName
     * @param bookmarkId
     */
    private void addBookmarkTag(String tagName, String bookmarkId) {
        Tag tag = getTag(tagName);
        String query = "INSERT INTO bookmark_tags (\"bookmarkId\", \"tagId\") values (?, ?)";
        try {
            if (tag != null) {
                PreparedStatement st = this.db.prepareStatement(query);
                st.setString(1, bookmarkId);
                st.setString(2, tag.getId());
                st.executeQuery();
            } else {
                String tagId = createTag(tagName);
                PreparedStatement st = this.db.prepareStatement(query);
                st.setString(1, bookmarkId);
                st.setString(2, tagId);
                st.executeQuery();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private Set<Tag> findBookmarkTags(String bookmarkId) {
        try {
            String query = "SELECT tags.* FROM bookmark_tags inner join tags on tags.id = bookmark_tags.\"tagId\" where bookmark_tags.\"bookmarkId\" = ?";
            PreparedStatement st = this.db.prepareStatement(query);
            st.setString(1, bookmarkId);
            ResultSet rs = st.executeQuery();
            Set<Tag> tagSet = new HashSet<>();
            while(rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                Tag t = new Tag(id, name);
                tagSet.add(t);
            }
            return tagSet;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public List<Bookmark> findByTagName(Set<Tag> tagSet) {
        ArrayList<String> tagArray = new ArrayList<>();
        for (Tag t : tagSet) {
            tagArray.add(t.getName());
        }
        String query = "select bookmarks.* from bookmark_tags inner join bookmarks on bookmark_tags.\"bookmarkId\" = bookmarks.id inner join tags on  tags.id = bookmark_tags.\"tagId\"  where tags.name = ANY(?)";
        try {
            PreparedStatement st = this.db.prepareStatement(query);
            Array array = this.db.createArrayOf("varchar", tagArray.toArray());
            st.setArray(1, array);
            ResultSet rs = st.executeQuery();
            HashSet<Bookmark> bookmarks = new HashSet<>();
            while (rs.next()) {
                String bookmarkId = rs.getString("id");
                String bookmarkTile = rs.getString("title");
                String bookmarkUrl = rs.getString("url");
                String bookmarkAuthor = rs.getString("author");
                Set<Tag> tags = findBookmarkTags(bookmarkId);
                bookmarks.add(new Bookmark(bookmarkId, bookmarkTile, bookmarkUrl, bookmarkAuthor, tags));
            }
            return new ArrayList<Bookmark>(bookmarks);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
	}
}

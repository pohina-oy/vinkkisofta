package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Bookmark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
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
                bookmarks.add(new Bookmark(
                    id, 
                    title, 
                    url, 
                    author
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
            st.setString(1, UUID.randomUUID().toString());
            st.setString(2, bookmark.getTitle());
            st.setString(3, bookmark.getUrl());
            st.setString(4, bookmark.getAuthor());
            st.executeQuery();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

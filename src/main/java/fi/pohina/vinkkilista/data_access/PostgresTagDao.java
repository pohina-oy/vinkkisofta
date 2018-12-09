package fi.pohina.vinkkilista.data_access;

import fi.pohina.vinkkilista.domain.Tag;

import java.sql.*;
import java.util.*;
import javax.sql.DataSource;

/**
 * Provides an PostgreSQL implementation of the {@link TagDao} interface,
 * backed by a {@link List<Tag>}.
 */
public class PostgresTagDao implements TagDao {

    private final DataSource dataSource;

    public PostgresTagDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * {@inheritDoc}
     * This is a linear time operation.
     */
    @Override
    public Tag findById(String id) {
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM tags WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.first()) {
                String name = rs.getString("name");
                return new Tag(id, name);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tag findByName(String name) {
        try (Connection conn = dataSource.getConnection()) {
            String query = "SELECT * FROM tags WHERE name = ?";
            PreparedStatement st = conn.prepareStatement(
                query,
                ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_UPDATABLE
            );
            st.setString(1, name);
            ResultSet rs = st.executeQuery();
            if (rs.first()) {
                String id = rs.getString("id");
                return new Tag(id, name);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tag> findAll() {
        List<Tag> tags = new ArrayList<>();
        try (Connection conn = dataSource.getConnection()) {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM tags");
            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                tags.add(new Tag(id, name));
            }
            return tags;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(Tag tag) {
        try (Connection conn = dataSource.getConnection()) {
            String query = "INSERT INTO tags (id, name) " + " values (?, ?)";
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, tag.getId());
            st.setString(2, tag.getName());
            st.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

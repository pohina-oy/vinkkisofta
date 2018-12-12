package fi.pohina.vinkkilista.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDateTime;

import fi.pohina.vinkkilista.domain.User;
import javax.sql.DataSource;

public class PostgresUserDao implements UserDao {

    private final DataSource dataSource;
    
    public PostgresUserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User findById(String id) {
        String query = "SELECT * FROM users left join user_read_bookmarks on users.id = user_read_bookmakrs.\"userId\" where users.id = ?";
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement st = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            st.setString(1, id);
            ResultSet rs = st.executeQuery();
            User user;
            if (rs.first()) {
                String userId = rs.getString("id");
                String userEmail = rs.getString("email");
                String username = rs.getString("username");
                Integer userGithubId = rs.getInt("githubId");

                String readBookmarkId = rs.getString("bookmarkId");
                LocalDateTime readDate = rs.getTimestamp("readDate").toLocalDateTime();
                user = new User(userId, userEmail, username, userGithubId);
                user.setBookmarkReadStatus(readBookmarkId, readDate);
            } else {
                return null;
            }

            while (rs.next()) {
                String readBookmarkId = rs.getString("bookmarkId");
                LocalDateTime readDate = rs.getTimestamp("readDate").toLocalDateTime();
                user.setBookmarkReadStatus(readBookmarkId, readDate);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public User findByGithubId(int githubId) {
        String query = "SELECT * FROM users where \"githubId\" = ?";
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement st = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            st.setInt(1, githubId);
            ResultSet rs = st.executeQuery();
            if (rs.first()) {
                String userId = rs.getString("id");
                String userEmail = rs.getString("email");
                String username = rs.getString("username");
                Integer userGithubId = rs.getInt("githubId");
                return new User(userId, userEmail, username, userGithubId);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public void add(User user) {
        String query = "INSERT INTO users (id, email, username, \"githubId\") values (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, user.getId());
            st.setString(2, user.getEmail());
            st.setString(3, user.getUsername());
            st.setInt(4, user.getGithubId());
            st.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<User> findAll() {
		return null;
	}

    @Override
    public void addBookmarkReadDate(String userId, String bookmarkId, LocalDateTime dateRead) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeBookmarkReadDate(String userId, String bookmarkId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
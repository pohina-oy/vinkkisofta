package fi.pohina.vinkkilista.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import fi.pohina.vinkkilista.domain.User;
import javax.sql.DataSource;

public class PostgresUserDao implements UserDao {

    private final DataSource dataSource;
    
    public PostgresUserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public User findById(String id) {
        String query = "SELECT * FROM users left join user_read_bookmarks on users.id = user_read_bookmarks.\"userId\" where users.id = ?";
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement st = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            st.setString(1, id);
            ResultSet rs = st.executeQuery();
            if (rs.first()) {
                String userId = rs.getString("id");
                String userEmail = rs.getString("email");
                String username = rs.getString("username");
                Integer userGithubId = rs.getInt("githubId");

                String readBookmarkId = rs.getString("bookmarkId");
                Timestamp readDate = rs.getTimestamp("readDate");
                User user = new User(userId, userEmail, username, userGithubId);
                user.setBookmarkReadStatus(readBookmarkId, readDate != null ? readDate.toLocalDateTime() : null);
                while (rs.next()) {
                    readBookmarkId = rs.getString("bookmarkId");
                    readDate = rs.getTimestamp("readDate");
                    user.setBookmarkReadStatus(readBookmarkId, readDate != null ? readDate.toLocalDateTime() : null);
                }
                return user;
            } else {
                return null;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @Override
    public User findByGithubId(int githubId) {
        String query = "SELECT * FROM users left join user_read_bookmarks on users.id = user_read_bookmarks.\"userId\" where \"githubId\" = ?";
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement st = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            st.setInt(1, githubId);
            ResultSet rs = st.executeQuery();
            if (rs.first()) {
                String userId = rs.getString("id");
                String userEmail = rs.getString("email");
                String username = rs.getString("username");
                Integer userGithubId = rs.getInt("githubId");

                String readBookmarkId = rs.getString("bookmarkId");
                Timestamp readDate = rs.getTimestamp("readDate");
                User user = new User(userId, userEmail, username, userGithubId);
                user.setBookmarkReadStatus(readBookmarkId, readDate != null ? readDate.toLocalDateTime() : null);
                while (rs.next()) {
                    readBookmarkId = rs.getString("bookmarkId");
                    readDate = rs.getTimestamp("readDate");
                    user.setBookmarkReadStatus(readBookmarkId, readDate != null ? readDate.toLocalDateTime() : null);
                }
                return user;
            } else {
                return null;
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
        String query = "INSERT INTO user_read_bookmarks (\"userId\",\"bookmarkId\",\"readDate\") values(?,?,?)";
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, userId);
            st.setString(2, bookmarkId);
            st.setTimestamp(3, Timestamp.valueOf(dateRead));
            st.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void removeBookmarkReadDate(String userId, String bookmarkId) {
        String query = "DELETE FROM user_read_bookmarks WHERE \"userId\" = ? AND \"bookmarkId\" = ?";
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement st = conn.prepareStatement(query);
            st.setString(1, userId);
            st.setString(2, bookmarkId);
            st.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
package fi.pohina.vinkkilista.data_access;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        String query = "SELECT * FROM users where id = ?";
        try (Connection conn = dataSource.getConnection()) {
            PreparedStatement st = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            st.setString(1, id);
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
}
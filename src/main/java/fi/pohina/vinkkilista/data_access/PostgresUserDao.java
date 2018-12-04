package fi.pohina.vinkkilista.data_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import fi.pohina.vinkkilista.domain.User;

public class PostgresUserDao implements UserDao {
    private Connection db;

    PostgresUserDao(String dbHost, String dbUser, String dbPassowrd, String dbName) {
        String url = "jdbc:postgresql://" + dbHost + "/" + dbName + "?user=" + dbUser + "&password=" + dbPassowrd;
        try {
            this.db = DriverManager.getConnection(url);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public User findUserById(String id) {
        String query = "SELECT * FROM users where id = ?";
        try {
            PreparedStatement st = this.db.prepareStatement(query);
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
    public User findUserByGithubId(int githubId) {
        String query = "SELECT * FROM users where \"githubId\" = ?";
        try {
            PreparedStatement st = this.db.prepareStatement(query);
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
        try {
            PreparedStatement st = this.db.prepareStatement(query);
            st.setString(1, user.getId());
            st.setString(2, user.getEmail());
            st.setString(3, user.getUsername());
            st.setInt(4, user.getGithubId());
            st.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
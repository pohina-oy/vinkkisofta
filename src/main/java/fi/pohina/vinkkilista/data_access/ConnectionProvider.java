package fi.pohina.vinkkilista.data_access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider {
    private final String connectionString;

    public ConnectionProvider(String connectionString) {
        this.connectionString = connectionString;
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(connectionString);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

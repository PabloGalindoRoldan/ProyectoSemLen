package ar.edu.unrn.seminario.accesos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Update these constants to match your local database configuration
    private static final String URL = "jdbc:mysql://localhost:3306/seminario?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // set your DB password

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // driver not found; propagate as runtime
            throw new RuntimeException("MySQL JDBC Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

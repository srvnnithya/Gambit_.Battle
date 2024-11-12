package van.backend;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/chess_game_db"; // Change to your database URL
    private static final String USER = "root"; // Your MySQL username
    private static final String PASSWORD = "Nithya@01*"; // Your MySQL password

    private Connection connection;

    // Method to establish a connection to the database
    public Connection connect() {
        try {
            // Load the MySQL JDBC driver (optional in newer JDBC versions)
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connection to the database established successfully.");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Failed to establish a connection to the database.");
            e.printStackTrace();
        }
        return connection;
    }

    // Method to close the database connection
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Failed to close the database connection.");
                e.printStackTrace();
            }
        }
    }

    // Method to check if the connection is successful
    public boolean isConnected() {
        return connection != null;
    }

    // Method to register a new user (player or guest)
    public void registerUser(String username, String playerType) {
        String query = "INSERT INTO users (username, player_type) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, playerType);
            pstmt.executeUpdate();
            System.out.println("User registered successfully: " + username);
        } catch (SQLException e) {
            System.err.println("Failed to register user.");
            e.printStackTrace();
        }
    }

    // Method to check if a user exists and get their type
    public String getUserType(String username) {
        String query = "SELECT player_type FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("player_type");
            } else {
                System.out.println("User not found: " + username);
            }
        } catch (SQLException e) {
            System.err.println("Failed to check user type.");
            e.printStackTrace();
        }
        return null;
    }
}

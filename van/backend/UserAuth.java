package van.backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserAuth {
    private DBConnection dbConnection;

    public UserAuth() {
        dbConnection = new DBConnection();
    }

    // Method to authenticate a user
    public boolean loginUser(String username, String password) {
        boolean isAuthenticated = false;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Establish a connection to the database
            connection = dbConnection.connect();
            
            // Prepare SQL query to find the user
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Check if a matching user was found
            if (resultSet.next()) {
                isAuthenticated = true; // User found
            }
        } catch (SQLException e) {
            System.err.println("Error during user authentication.");
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) dbConnection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return isAuthenticated; // Return authentication result
    }
}

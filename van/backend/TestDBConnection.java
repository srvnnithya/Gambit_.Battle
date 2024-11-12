package van.backend;

public class TestDBConnection {
    public static void main(String[] args) {
        DBConnection dbConnection = new DBConnection();
        dbConnection.connect();

        if (dbConnection.isConnected()) {
            System.out.println("Database is connected.");
        } else {
            System.out.println("Failed to connect to the database.");
        }

        dbConnection.close();
    }
}

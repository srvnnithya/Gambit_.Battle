package van.backend;

public class TestUserAuth {
    public static void main(String[] args) {
        UserAuth userAuth = new UserAuth();
        String username = "testUser"; // Change to an existing username
        String password = "testPass"; // Change to the corresponding password
        
        boolean isAuthenticated = userAuth.loginUser(username, password);
        if (isAuthenticated) {
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password.");
        }
    }
}

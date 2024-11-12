package van.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import van.backend.DBConnection; // Import your DBConnection class

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LoginScreen extends JFrame {
    private JTextField player1Field;
    private JTextField player2Field;

    public LoginScreen() {
        // Frame settings
        setTitle("GAMBIT Battle Login");
        setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Background Image
        JLabel background = new JLabel(new ImageIcon("resources/symphony.png"));
        background.setLayout(new BorderLayout());
        setContentPane(background);

        // Title Label
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel1 = new JLabel("Welcome to");
        titleLabel1.setFont(new Font("Georgia", Font.BOLD, 36)); // Larger font
        titleLabel1.setForeground(Color.BLACK);

        JLabel titleLabel2 = new JLabel("GAMBIT BATTLE!");
        titleLabel2.setFont(new Font("Georgia", Font.BOLD, 36)); // Larger font
        titleLabel2.setForeground(Color.BLACK);

        titlePanel.add(titleLabel1);
        titlePanel.add(titleLabel2);
        titlePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Center Panel (for input fields and buttons)
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Position the title higher
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        centerPanel.add(titlePanel, gbc);

        // Player 1 Name Input
        JLabel player1Label = new JLabel("Player 1 Name:");
        player1Label.setForeground(Color.BLACK);
        player1Field = new JTextField(12);
        player1Field.setPreferredSize(new Dimension(200, 30));
        styleTextField(player1Field);

        // Player 2 Name Input
        JLabel player2Label = new JLabel("Player 2 Name:");
        player2Label.setForeground(Color.BLACK);
        player2Field = new JTextField(12);
        player2Field.setPreferredSize(new Dimension(200, 30));
        styleTextField(player2Field);

        // Add player labels and fields to the center panel
        gbc.gridwidth = 1; // Resetting gridwidth for each individual component

        // Player 1 Label
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(player1Label, gbc);

        // Player 1 Field
        gbc.gridx = 1;
        centerPanel.add(player1Field, gbc);

        // Player 2 Label
        gbc.gridy = 2;
        gbc.gridx = 0;
        centerPanel.add(player2Label, gbc);

        // Player 2 Field
        gbc.gridx = 1;
        centerPanel.add(player2Field, gbc);

        // Guest Login Button
        JButton guestLoginButton = createButton("Guest Login");
        guestLoginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                player1Field.setText("Guest 1");
                player2Field.setText("Guest 2");
            }
        });

        // Start Game Button
        JButton loginButton = createButton("Start Game");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String player1Name = player1Field.getText();
                String player2Name = player2Field.getText();
                if (player1Name.isEmpty() || player2Name.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter names for both players or use guest login.");
                } else {
                    insertPlayersIntoDatabase(player1Name, player2Name);
                    System.out.println("Starting game: " + player1Name + " vs " + player2Name);
                    startChessGame(player1Name, player2Name); // Call method to start Chess game
                }
            }
        });

        // Add the buttons with reduced gaps and move them up
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.setOpaque(false);
        buttonPanel.add(guestLoginButton);
        buttonPanel.add(loginButton);
        centerPanel.add(buttonPanel, gbc);

        // Spacer to push elements higher
        gbc.gridy = 4;
        gbc.weighty = 1.0;
        centerPanel.add(Box.createVerticalGlue(), gbc);

        // Add title, input, and button panels to the layout
        background.add(centerPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void styleTextField(JTextField textField) {
        textField.setBackground(new Color(255, 255, 255, 200)); // Semi-transparent white
        textField.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Blue border
        textField.setFont(new Font("Arial", Font.PLAIN, 16));
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(Color.CYAN);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.GREEN);
                button.setFont(new Font("Arial", Font.BOLD, 18)); // Slightly increase font size on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.CYAN);
                button.setFont(new Font("Arial", Font.BOLD, 16)); // Reset font size
            }
        });
        return button;
    }

    private void insertPlayersIntoDatabase(String player1, String player2) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.connect();

        String insertQuery1 = "INSERT INTO users (username, player_type) VALUES (?, ?)";
        String insertQuery2 = "INSERT INTO users (username, player_type) VALUES (?, ?)";

        try {
            PreparedStatement preparedStatement1 = connection.prepareStatement(insertQuery1);
            preparedStatement1.setString(1, player1);
            preparedStatement1.setString(2, "Player");
            preparedStatement1.executeUpdate();

            PreparedStatement preparedStatement2 = connection.prepareStatement(insertQuery2);
            preparedStatement2.setString(1, player2);
            preparedStatement2.setString(2, "Guest");
            preparedStatement2.executeUpdate();

            System.out.println("Players inserted successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void startChessGame(String player1Name, String player2Name) {
        Chess chessGame = new Chess(player1Name, player2Name); // Launch the chess game with player names
        chessGame.setVisible(true);
        dispose(); // Close the login screen
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginScreen();
            }
        });
    }
}

package van.gui;

import javax.swing.JFrame;

public class Chess extends JFrame {
    private Board _board; // where the game is being played

    // Default constructor (no player names)
    public Chess() {
        this.setSize(640, 660);
        this.setTitle("Chess");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); // Centers the frame in the middle of the screen
        this.setResizable(false); // Window cannot be resized
        this._board = new Board(); // Initialize the board without player names
        getContentPane().add(_board); // Add board to the frame
    }

    public Chess(String player1Name, String player2Name) {
        //TODO Auto-generated constructor stub
    }

    // Main method to launch the game
    public static void main(String[] args) {
        Chess window = new Chess();
        window.setVisible(true);
    }
}
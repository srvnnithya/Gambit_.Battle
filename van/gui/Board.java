package van.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class Board extends JComponent implements MouseListener {
    public static final int BLACK_PAWN = 1;
    public static final int BLACK_ROOK = 3;
    public static final int BLACK_KNIGHT = 5;
    public static final int BLACK_BISHOP = 7;
    public static final int BLACK_QUEEN = 9;
    public static final int BLACK_KING = 11;
	
    public static final int WHITE_PAWN = 0;
    public static final int WHITE_ROOK = 2;
    public static final int WHITE_KNIGHT = 4;
    public static final int WHITE_BISHOP = 6;
    public static final int WHITE_QUEEN = 8;
    public static final int WHITE_KING = 10;

    public static final int WHITE_PLAYER = 0;
    public static final int BLACK_PLAYER = 1;
    public static final int NONE = -1;

    public enum Pieces {
	NONE, PAWN, ROOK, KNIGHT, BISHOP, QUEEN, KING
    }
	
    private int[][] _boardMap; // game board state
    private int _currentPlayer; // denotes who the current player is
    private int _check, _checkMate, _staleMate; // who's in check ?
    private boolean _ended;

    private int	_selectedPiece; // selected piece type
    private int _x, _y; // basic temporary saved coordinates

    private BufferedImage[] _icons;
    private Color _darkGreen, _lightYellow, _hlValid, _hlCheck; // colors used

    public Board() {
	// Set colors in use
	this._darkGreen = new Color(0, 100, 0);
	this._lightYellow = new Color(240, 220, 130);
	this._hlValid = new Color(250, 0, 0, 90);
	this._hlCheck = Color.ORANGE;

	// Initialize the board to be 8*8 squares
	this._boardMap = new int[8][8];

	// Place empty spaces (ie -1, not 0)
	for (int i = 0; i < 8; ++i)
	    for (int j = 0; j < 8; ++j)
		this._boardMap[i][j] = NONE;

	// Place black pieces
	for (int i = 0; i < 8; ++i)
	    this._boardMap[i][1] = BLACK_PAWN;
	this._boardMap[0][0] = BLACK_ROOK;
	this._boardMap[1][0] = BLACK_KNIGHT;
	this._boardMap[2][0] = BLACK_BISHOP;
	this._boardMap[3][0] = BLACK_QUEEN;
	this._boardMap[4][0] = BLACK_KING;
	this._boardMap[5][0] = BLACK_BISHOP;
	this._boardMap[6][0] = BLACK_KNIGHT;
	this._boardMap[7][0] = BLACK_ROOK;

	// Place white pieces
	for (int i = 0; i < 8; ++i)
	    this._boardMap[i][6] = WHITE_PAWN;
	this._boardMap[0][7] = WHITE_ROOK;
	this._boardMap[1][7] = WHITE_KNIGHT;
	this._boardMap[2][7] = WHITE_BISHOP;
	this._boardMap[3][7] = WHITE_QUEEN;
	this._boardMap[4][7] = WHITE_KING;
	this._boardMap[5][7] = WHITE_BISHOP;
	this._boardMap[6][7] = WHITE_KNIGHT;
	this._boardMap[7][7] = WHITE_ROOK;

	// Initialize the vector of buffered images and loads the differents icons
	this._icons = new BufferedImage[12];
	try {
	    this._icons[0] = ImageIO.read(this.getClass().getResource("icons/white_pawn.png"));
	    this._icons[1] = ImageIO.read(this.getClass().getResource("icons/black_pawn.png"));
	    this._icons[2] = ImageIO.read(this.getClass().getResource("icons/white_rook.png"));
	    this._icons[3] = ImageIO.read(this.getClass().getResource("icons/black_rook.png"));
	    this._icons[4] = ImageIO.read(this.getClass().getResource("icons/white_knight.png"));
	    this._icons[5] = ImageIO.read(this.getClass().getResource("icons/black_knight.png"));
	    this._icons[6] = ImageIO.read(this.getClass().getResource("icons/white_bishop.png"));
	    this._icons[7] = ImageIO.read(this.getClass().getResource("icons/black_bishop.png"));
	    this._icons[8] = ImageIO.read(this.getClass().getResource("icons/white_queen.png"));
	    this._icons[9] = ImageIO.read(this.getClass().getResource("icons/black_queen.png"));
	    this._icons[10] = ImageIO.read(this.getClass().getResource("icons/white_king.png"));
	    this._icons[11] = ImageIO.read(this.getClass().getResource("icons/black_king.png"));
	} catch (IOException e) {}

	// Initialize the game
	this._currentPlayer = WHITE_PLAYER;
	this._selectedPiece = this._x = this._y = NONE;
	this._check = this._checkMate = this._staleMate = NONE;
	this._ended = false;

	// Add a mouse listener that will detect all mouse events on the widget
	this.addMouseListener(this);
    }

    public Board(String player1Name, String player2Name) {
		//TODO Auto-generated constructor stub
	}

	// Methods required by MouseListener, not used
    public void mouseClicked(MouseEvent event) {}
    public void mouseEntered(MouseEvent event) {}
    public void mouseReleased(MouseEvent event) {}
    public void mouseExited(MouseEvent event) {}

    // Get the square associated to a pixel value,
    // assuming a 640*640px and 8*8 squares board
    private int getSquare(int pixel) {
	if (pixel < 640)
	    return (pixel / 80);
	return -1;
    }

    // Select the given piece
    private int selectPiece(int x, int y) {
	this._x = x;
	this._y = y;
	this._selectedPiece = this._boardMap[x][y];
	return this._selectedPiece;
    }

    // Will react to mouse press events on the widget
    public void mousePressed(MouseEvent event) {
	int x = getSquare(event.getX());
	int y = getSquare(event.getY());

	// Don't do anything if we're not on the play zone or if game is over
	if (x == -1 || y == -1 || this._ended)
	    return;

	// If no piece has been selected, select this one
	if (owned(x, y)) {
	    this.selectPiece(x, y);
	    this.repaint();
	}
	// If a piece is already selected, try the movement
	else
	    this.attemptMove(x, y);
    }

    // Check that the king move is valid
    private boolean checkKingMove(int x, int y) {
	// The king move is just a limited queen move
	if (Math.abs(this._x - x) < 2 && Math.abs(this._y - y) < 2)
	    return checkQueenMove(x, y);
	return false;
    }

    // Check that the queen move is valid
    private boolean checkQueenMove(int x, int y) {
	// The queen move can be a rook move or a bishop move
	return checkRookMove(x, y) || checkBishopMove(x, y);
    }

    // Check that the knight move is valid
    private boolean checkKnightMove(int x, int y) {
	if (Math.abs(this._y - y) == 2 && Math.abs(this._x - x) == 1
	    || Math.abs(this._y - y) == 1 && Math.abs(this._x - x) == 2)
	    return true;
	return false;
    }

    // Check that the bishop move is valid
    private boolean checkBishopMove(int x, int y) {
	// Diagonal movement
	if (Math.abs(this._x - x) == Math.abs(this._y - y))
	    return pathClear(x, y);
	return false;
    }

    // Check that the rook move is valid
    private boolean checkRookMove(int x, int y) {
	// Vertical movement
	if (this._x == x && this._y != y ||
	    this._x != x && this._y == y)
	    return pathClear(x, y);
	return false;
    }

    // Check that the pawn move is valid
    private boolean checkPawnMove(int x, int y)
    {
	// If pawn is white, then the check is done going up
	if (this._selectedPiece == WHITE_PAWN) {
	    // Check vertical movement and empty square
	    if (this._x == x && this._y - y == 1 && this.empty(x, y))
		return true;
	    // Check vertical movement and empty square for first pawn movement
	    if (this._x == x && this._y - y == 2 && this.empty(x, y)
		&& this._y == 6 && this.empty(x, y + 1))
		return true;
	    // Check diagonal movement and enemy piece
	    if (this._y - y == 1 && (this._x - x == 1 || x - this._x == 1)
		&& this.enemy(x, y))
		return true;
	}
	// If pawn is black, then the check goes down (same checks)
	if (this._selectedPiece == BLACK_PAWN) {
	    // Check vertical movement and empty square
	    if (this._x == x && y - this._y == 1 && this.empty(x, y))
		return true;
	    // Check vertical movement and empty square for first pawn movement
	    if (this._x == x && y - this._y == 2 && this.empty(x, y)
		&& this._y == 1 && this.empty(x, y - 1))
		return true;
	    // Check diagonal movement and enemy piece
	    if (y - this._y == 1 && (this._x - x == 1 || x - this._x == 1)
		&& this.enemy(x, y))
		return true;
	}
	return false;
    }

    // Simulate a movement before accepting it, to not put himself in a check
    private boolean simulateMove(int x, int y) {
	int[][] saved = new int[8][8];
	boolean ret = true;

	// Backup the actual board map
	for (int i = 0; i < 8; ++i)
	    System.arraycopy(this._boardMap[i], 0, saved[i], 0, this._boardMap[i].length);

	// Simulate the move, return false if there is a king check
	this.movePiece(x, y);
	ret = this.checkKingCheck() ? false : true;

	// Restore the real board map
	for (int i = 0; i < 8; ++i)
	    System.arraycopy(saved[i], 0, this._boardMap[i], 0, saved[i].length);
	return ret;
    }

    // Check if the move is allowed for the selected piece
    private boolean isMoveValid(int x, int y) {
	boolean ret = false;

	// Don't move over his own piece
	if (this.owned(x, y))
	    return false;

	// Is it possible for the piece to move there ?
	if (this._selectedPiece == WHITE_PAWN || this._selectedPiece == BLACK_PAWN)
	    ret = checkPawnMove(x, y);
	else if (this._selectedPiece == WHITE_ROOK || this._selectedPiece == BLACK_ROOK)
	    ret = checkRookMove(x, y);
	else if (this._selectedPiece == WHITE_KNIGHT || this._selectedPiece == BLACK_KNIGHT)
	    ret = checkKnightMove(x, y);
	else if (this._selectedPiece == WHITE_BISHOP || this._selectedPiece == BLACK_BISHOP)
	    ret = checkBishopMove(x, y);
	else if (this._selectedPiece == WHITE_QUEEN || this._selectedPiece == BLACK_QUEEN)
	    ret = checkQueenMove(x, y);
	else if (this._selectedPiece == WHITE_KING || this._selectedPiece == BLACK_KING)
	    ret = checkKingMove(x, y);
	else
	    return false;
	return ret && simulateMove(x, y);
    }

    // Move the previously selected piece to its new location
    private void movePiece(int x, int y) {
	this._boardMap[x][y] = this._boardMap[this._x][this._y];
	this._boardMap[this._x][this._y] = NONE;
    }
	
    // Check if a piece is checking the enemy king
    private boolean pieceChecking(int x, int y) {
	boolean checking = false;
	boolean found = false;
	int save_x = this._x;
	int save_y = this._y;
	int save_select = this._selectedPiece;
	int save_current = this._currentPlayer;
	int piece_owner = this._boardMap[x][y] % 2;
	int target = (piece_owner == this.BLACK_PLAYER)? WHITE_KING:BLACK_KING;

	this.selectPiece(x, y);
	this._currentPlayer = piece_owner;
	// Find enemy king and check if move is valid
	for (int i = 0; i < 8 && found == false; ++i)
	    for (int j = 0; j < 8 && found == false; ++j)
		if (this._boardMap[i][j] == target) {
		    checking = this.isMoveValid(i, j);
		    found = true;
		}
	this._x = save_x;
	this._y = save_y;
	this._selectedPiece = save_select;
	this._currentPlayer = save_current;
	return checking;
    }

    // Check for every enemy piece if the king is in check
    private boolean checkKingCheck() {
	this.switchPlayer();
	for (int x = 0; x < 8; ++x)
	    for (int y = 0; y < 8; ++y)
		if (this.owned(x, y) && this.pieceChecking(x, y)) {
		    this.switchPlayer();
		    return true;
		}
	this.switchPlayer();
	return false;
    }

    // Check if game is endend, by check mate or by stale mate
    private boolean checkEnd() {
	boolean ret = true;
	int save_x = this._x;
	int save_y = this._y;
	int save_select = this._selectedPiece;

	// Find the player's king
	for (int i = 0; i < 8; ++i)
	    for (int j = 0; j < 8; ++j)
		if (owned(i, j)) {
		    // check if it still has a valid move one the board
		    this.selectPiece(i, j);
		    for (int k = 0; k < 8 && ret; ++k)
			for (int l = 0; l < 8 && ret; ++l)
			    // if a valid move is found, return false
			    if (this.isMoveValid(k, l))
				ret = false;
		}
	this._x = save_x;
	this._y = save_y;
	this._selectedPiece = save_select;
	return ret;
    }

    // Attempt a piece movement
    private void attemptMove(int x, int y) {
	if (this.isMoveValid(x, y) && this.simulateMove(x, y)) {
	    this.movePiece(x, y);
	    this.endTurn();
	}
	else
	    this.repaint();
    }

    // Return the direction between two values
    private int getDirection(int i, int j) {
	if (i == j)
	    return 0;
	return (i < j) ? 1 : -1;
    }

    // Check if the path is clear between a point and the selected piece
    private boolean pathClear(int x, int y) {
	int xDirection = getDirection(x, this._x);
	int yDirection = getDirection(y, this._y);

	// don't check the point itself
	x += xDirection;
	y += yDirection;

	// check every square until we are on the piece's square
	while (x != this._x || y != this._y) {
	    if (this.empty(x, y) == false)
		return false;
	    x += xDirection;
	    y += yDirection;
	}
	return true;
    }

    // Check if a given piece belong to the current player
    private boolean owned(int x, int y) {
	int piece = this._boardMap[x][y];

	if (piece != NONE && piece % 2 == this._currentPlayer)
	    return true;
	return false;
    }

    // Check if a given piece belong to the other player
    private boolean enemy(int x, int y) {
	int piece = this._boardMap[x][y];

	if (piece != NONE && piece % 2 != this._currentPlayer)
	    return true;
	return false;
    }

    // Check if a given piece belong to the other player
    private boolean empty(int x, int y) {
	if (this._boardMap[x][y] == NONE)
	    return true;
	return false;
    }

    // Paint the window
    public void paintComponent(Graphics g) {
	super.paintComponents(g);
	Graphics2D g2d = (Graphics2D) g;
	drawGrid(g2d);
	drawPanel(g2d);
	drawPieces(g2d);
	// If game is ended, show it
	if (this._ended) {
	    g2d.setColor(Color.GRAY);
	    g2d.fillRect(100, 280, 440, 80);
	    g2d.setFont(new Font("Sans", Font.BOLD, 60));
	    g2d.setColor(Color.RED);
	    g2d.drawString("GAME OVER", 117, 340);
	}
    }

    // Draw the board, assuming a 640*640px board
    // Draw the board with updated colors for clarity
private void drawGrid(Graphics2D g2d) {
    Color lightBlue = new Color(173, 216, 230);
    Color darkBlue = new Color(70, 130, 180);
    
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            // Use alternate blue shades for the board
            if ((i + j) % 2 == 0) {
                g2d.setColor(lightBlue);
            } else {
                g2d.setColor(darkBlue);
            }
            g2d.fillRect(80 * i, 80 * j, 80, 80);
            
            // Highlight the selected square or valid moves
            if (this._selectedPiece != NONE && i == this._x && j == this._y) {
                g2d.setColor(new Color(255, 0, 0, 120)); // Transparent red for selected piece
                g2d.fillRect(80 * i, 80 * j, 80, 80);
            } else if (this.isMoveValid(i, j)) {
                g2d.setColor(new Color(0, 255, 0, 120)); // Transparent green for valid moves
                g2d.fillRect(80 * i, 80 * j, 80, 80);
            }
        }
    }
}

    // Draw the indications panel, assuming a 640 * 720 window
    // Draw the indications panel, assuming a 640 * 720 window
private void drawPanel(Graphics2D g2d) {
    // Background
    g2d.setColor(Color.GRAY);
    g2d.fillRect(0, 640, 640, 20);

    // Titles (never changing)
    g2d.setColor(Color.BLACK);
    g2d.setFont(new Font("Sans", Font.ITALIC, 15));
    g2d.drawString("Player's turn:", 10, 655);
    g2d.drawString("| Player's status:", 318, 655);

    // Player's turn
    g2d.setFont(new Font("Sans", Font.BOLD, 15));
    if (this._currentPlayer == WHITE_PLAYER) {
        g2d.setColor(Color.WHITE);
        g2d.drawString("Player 1 (WHITE)", 125, 655);
    } else {
        g2d.setColor(Color.BLACK);
        g2d.drawString("Player 2 (BLACK)", 125, 655);
    }

    // Player's status
    g2d.setFont(new Font("Sans", Font.BOLD, 15));
    if (this._checkMate == this._currentPlayer) {
        g2d.setColor(Color.RED);
        g2d.drawString(" CHECKMATE ", 460, 655);
    } else if (this._staleMate == this._currentPlayer) {
        g2d.setColor(Color.BLUE);
        g2d.drawString(" STALEMATE ", 460, 655);
    } else if (this._check == this._currentPlayer) {
        g2d.setColor(Color.ORANGE);
        g2d.drawString(" /!\\ CHECK ", 460, 655);
        highlightKingInCheck(g2d);
    } else {
        g2d.setColor(Color.GREEN);
        g2d.drawString("Everything is OK", 460, 655);
    }
}

// Highlight the king in check on the board
private void highlightKingInCheck(Graphics2D g2d) {
    int kingPiece = this._currentPlayer == WHITE_PLAYER ? WHITE_KING : BLACK_KING;
    for (int x = 0; x < 8; x++) {
        for (int y = 0; y < 8; y++) {
            if (this._boardMap[x][y] == kingPiece) {
                g2d.setColor(new Color(255, 69, 0, 120)); // Red highlight with transparency
                g2d.fillRect(80 * x, 80 * y, 80, 80);
                break;
            }
        }
    }
}

    //	Draw the pieces that are currently on the board.
    private void drawPieces(Graphics2D g2d) {
	for (int x = 0; x < 8; ++x)
	    for (int y = 0; y < 8; ++y)
		if (this.empty(x, y) == false)
		    g2d.drawImage(this._icons[this._boardMap[x][y]],
				  x * 80 + 14, y * 80 + 14, null);
    }

    // Perform end turn actions
    private void endTurn() {
	// Change the current player
	this.switchPlayer();

	// See if the current player have a king check due to precedent player
	this._check = NONE;
	if (this.checkKingCheck()) {
	    this._check = this._currentPlayer;
	}

	if (this.checkEnd()) {
	    if (this._check != NONE)
		this._checkMate = this._currentPlayer;
	    else
		this._staleMate = this._currentPlayer;
	    this._ended = true;
	}

	// Reset selected piece
	this._selectedPiece = this._x = this._y = NONE;

	// Then repaint the GUI without selected piece
	this.repaint();
    }

    // Change the current player
    private void switchPlayer() {
	this._currentPlayer ^= 1; // Bitwise operation (XOR) to switch between 0 and 1
    }
}

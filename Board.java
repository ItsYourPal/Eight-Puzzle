import dsa.Inversions;
import dsa.LinkedQueue;
import stdlib.In;
import stdlib.StdOut;

// A data type to represent a board in the 8-puzzle game or its generalizations.
public class Board {
    private int[][] tiles; //Representing a board using a 2D array.
    private int n; // Dimension of board, n*n.
    private int hamming; //The hamming distance of the goal board.
    private int manhattan; // The Manhattan distance of the goal board.
    private int blankPos; //The position of the blank tile (0) on the board.

    // Constructs a board from an n x n array; tiles[i][j] is the tile at row i and column j, with 0
    // denoting the blank tile.
    public Board(int[][] tiles) {
        if (tiles == null || tiles.length == 0 || tiles.length != tiles[0].length ) {
           throw new IllegalArgumentException("Invalid tiles");
        } 
        n = tiles.length;
        this.tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.tiles[i][j] = tiles[i][j]; // Make a deep copy of input tile.
            }
        }
        this.hamming = hamming();
        this.manhattan = manhattan();
        this.blankPos = blankPos();
    }

    // Returns the size of this board.
    public int size() {
        return n;
    }

    // Returns the tile at row i and column j of this board.
    public int tileAt(int i, int j) {
        if (i<0 || i>=size() || j>= size()) {
            throw new IllegalArgumentException("Invalid row or column");
        }
        return this.tiles[i][j];
    }

    // Returns Hamming distance between this board and the goal board.
    public int hamming() {
        int hamming_tiles = 0; //Number of tiles that are not in place.
        for (int i = 0; i < size(); i++) {
            for (int j =0; j< size(); j++) {
                if (tileAt(i,j) != i * n + j + 1 && tileAt(i,j) !=0 ) {
                    hamming_tiles += 1;
                }
            }
        }
        return hamming_tiles;
    }
    public int blankPos() {
        int blankRow = -1;
        for (int i = 0; i < n; i++) { 
            for (int j = 0; j < n; j++) { 
                if (tileAt(i,j) == 0){
                    blankRow = i * n +j +1 ; // Finding out the blank tiles pos - row Major index.
                    break;
                }
            }
        }
        return blankRow;
    }
    // Returns the Manhattan distance between this board and the goal board.
    public int manhattan() {
        int manhattan_tiles = 0; // The sum distance of tiles from their goals
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j <size(); j++) {
                if (tileAt(i,j) != 0) {
                    int tileRow = (tileAt(i,j)-1) / n; //Row of tile.
                    int tileCol = (tileAt(i,j)-1) % n; //Row of column
                    manhattan_tiles += Math.abs(tileRow - i) + Math.abs(tileCol - j); //Calculate distance
                }
            }
        }
        return manhattan_tiles;
    }

    // Returns true if this board is the goal board, and false otherwise.
    public boolean isGoal() {
        for (int i = 0; i < n; i++){
            for (int j = 0; j < n; j++) {
                if (tileAt(i,j) != i * n + j + 1 && (i != n - 1)) {
                    return false;
                }
            }
        }   
        return true;
    }

    // Returns true if this board is solvable, and false otherwise.
    public boolean isSolvable() {
        int[] temp_array = new int[size() * size() - 1];
        int count = 0;
        for (int p = 0; p < size(); p++) {   // p is the row pos
            for (int q = 0; q < size(); q++) { // q is the col pos
                if (this.tiles[p][q] > 0) {
                    temp_array[count] = this.tiles[p][q];
                    count++;
                }
            }
        }

        long invCount = Inversions.count(temp_array); //Inversions Class Utility used to count
        boolean isBoardEven = size() % 2 == 0; // The Even sized boards need one blank row.
        if (isBoardEven) {
            
            int newinvCount = (int) invCount + blankPos;
                return !(newinvCount % 2 == 0);
        } else {
            return invCount % 2 == 0; //The Odd size board has even inversions.
        }
    }
            
      

    // Returns an iterable object containing the neighboring boards of this board.
    public Iterable<Board> neighbors(){
        int row = -1;
        int col = -1;
        for (int p = 0; p < this.size(); p++) { //p is the row pos
            for (int q = 0; q < this.size(); q++) { // q is the col pos
                if (tileAt(p,q) == 0) {
                    row = p;
                    col = q; //Set the blank tile pos.
                }
            }
        }
    
        // Queue to store the neighboring boards
        LinkedQueue<Board> q = new LinkedQueue<>();
        if (row < n - 1){
            int[][] clone = cloneTiles();
            int temp = clone[row][col];
            clone[row][col] = clone[row + 1][col];
            clone[row + 1][col] = temp;
            q.enqueue(new Board(clone));
        }
        if (row > 0) {
            int[][] clone = cloneTiles();
            int temp = clone[row][col];
            clone[row][col] = clone[row - 1][col];
            clone[row -1][col] = temp;
            q.enqueue(new Board(clone));
        }
        if (col < n -1) {
            int[][] clone = cloneTiles();
            int temp = clone[row][col];
            clone[row][col] = clone[row][col+1];
            clone[row][col + 1] = temp;
            q.enqueue(new Board(clone));
        }

        if (col > 0) {
            int[][] clone = cloneTiles();
            int temp = clone[row][col];
            clone[row][col] = clone[row][col - 1];
            clone[row][col -1] = temp;
            q.enqueue(new Board(clone));
        }
        return q;
    }

    // Returns true if this board is the same as other, and false otherwise.
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other == this) {
            return true;
        }
        if (other.getClass() != this.getClass()) {
            return false;    
        }
        Board board = (Board) other; // cast to proper object type
        if (this.size() != board.size()){
            return false;
        }
        for (int p = 0; p < size(); p++) {  // p is the row pos
            for (int q = 0; q < size(); q++) {  //q is the col pos
                if (this.tiles[p][q] != board.tiles[p][q]) {
                    return false;
                }
            }
        }
        return true;
    }

    // Returns a string representation of this board.
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s.append(String.format("%2s", tiles[i][j] == 0 ? " " : tiles[i][j]));
                if (j < n - 1) {
                    s.append(" ");
                }
            }
            if (i < n - 1) {
                s.append("\n");
            }
        }
        return s.toString();
    }

    // Returns a defensive copy of tiles[][].
    private int[][] cloneTiles() {
        int[][] cloneTiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                cloneTiles[i][j] = tiles[i][j];
            }
        }
        return cloneTiles;
    }

    // Unit tests the data type. [DO NOT EDIT]
    public static void main(String[] args) {
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.readInt();
            }
        }
        Board board = new Board(tiles);
        StdOut.printf("The board (%d-puzzle):\n%s\n", n, board);
        String f = "Hamming = %d, Manhattan = %d, Goal? %s, Solvable? %s\n";
        StdOut.printf(f, board.hamming(), board.manhattan(), board.isGoal(), board.isSolvable());
        StdOut.println("Neighboring boards:");
        for (Board neighbor : board.neighbors()) {
            StdOut.println(neighbor);
            StdOut.println("----------");
        }
    }
}

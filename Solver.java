import dsa.LinkedStack;
import dsa.MinPQ;
import stdlib.In;
import stdlib.StdOut;

// A data type that implements the A* algorithm for solving the 8-puzzle and its generalizations.
public class Solver {
    private LinkedStack<Board> solution; // Using a stack to store the sequence of boards
    private int moves; // The number moves needed to get answer
    // Finds a solution to the initial board using the A* algorithm.
    public Solver(Board board) {
        /*if (board == null) {
            throw new NullPointerException("board is null");
        }
        if (!board.isSolvable()) {
            throw new IllegalArgumentException("board is unsolveable");
        } */
        
        // Priority Queue (minpq) to store each search node based on priority.
        MinPQ<SearchNode> pq = new MinPQ<SearchNode>();

        // Insert the intial board as the root node with 0 moves and no previous node.
        pq.insert(new SearchNode(board,0,null));
        
        // Main loop to process the nodes in the priority queue.
        while (!pq.isEmpty()) {
            SearchNode node = pq.deleteMin();
            // We will check if thecurrent board is
            if (node.board.isGoal()) {
                solution = new LinkedStack<Board>();
                moves = node.moves;
                while (node != null) {
                    solution.push(node.board);
                    node = node.previous;
                }
                break;
            } else {
                for (Board b: node.board.neighbors()) {
                    if (node.previous == null || !b.equals(node.previous.board))
                        pq.insert(new SearchNode(b,node.moves + 1, node));
                }
            }
        }
    }

    // Returns the minimum number of moves needed to solve the initial board.
    public int moves() {
        return moves;
    }

    // Returns a sequence of boards in a shortest solution of the initial board.
    public Iterable<Board> solution() {
        return solution;
    }

    // A data type that represents a search node in the grame tree. Each node includes a
    // reference to a board, the number of moves to the node from the initial node, and a
    // reference to the previous node.
    private class SearchNode implements Comparable<SearchNode> {
        public SearchNode previous;
        private Board board; 
        private int moves;

        // Constructs a new search node.
        public SearchNode(Board board, int moves, SearchNode previous) {
            this.board = board;
            this.moves = moves;
            this.previous = previous;
        }

        // Returns a comparison of this node and other based on the following sum:
        //   Manhattan distance of the board in the node + the # of moves to the node
        public int compareTo(SearchNode other) {
            int thisNode = this.moves + this.board.manhattan();
            int otherNode = other.moves + other.board.manhattan();
            return (thisNode -otherNode);
        }
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
        Board initial = new Board(tiles);
        if (initial.isSolvable()) {
            Solver solver = new Solver(initial);
            StdOut.printf("Solution (%d moves):\n", solver.moves());
            StdOut.println(initial);
            StdOut.println("----------");
            for (Board board : solver.solution()) {
                StdOut.println(board);
                StdOut.println("----------");
            }
        } else {
            StdOut.println("Unsolvable puzzle");
        }
    }
}

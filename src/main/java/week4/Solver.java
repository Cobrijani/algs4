package week4;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Solver {

    private final Iterable<Board> solution;
    private int moves = -1;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }
        this.solution = aStarSearch(initial);
    }

    private Iterable<Board> aStarSearch(Board initialBoard) {
        MinPQ<SearchNode> minPQ = new MinPQ<>(SearchNode::compareTo);
        MinPQ<SearchNode> minPQTwin = new MinPQ<>(SearchNode::compareTo);

        minPQ.insert(new SearchNode(null, 0, initialBoard));
        minPQTwin.insert(new SearchNode(null, 0, initialBoard.twin()));

        while (true) {

            SearchNode min = minPQ.delMin();
            if (min == null) {
                break;
            }
            if (min.board.isGoal()) {
                return reconstructSolution(min);
            }

            SearchNode twin = minPQTwin.delMin();
            if (twin != null && twin.board.isGoal()) {
                break;
            }

            for (SearchNode searchNode : min.neighbors()) {
                minPQ.insert(searchNode);
            }

            if (twin != null) {
                for (SearchNode searchNode : twin.neighbors()) {
                    minPQTwin.insert(searchNode);
                }
            }
        }

        return null;
    }

    private Iterable<Board> reconstructSolution(SearchNode node) {
        moves = node.moves;
        Deque<Board> deque = new LinkedList<>();
        deque.addFirst(node.board);
        SearchNode currentNode = node.previous;

        while (currentNode != null) {
            deque.addFirst(currentNode.board);
            currentNode = currentNode.previous;
        }

        return deque;
    }

    private static class SearchNode implements Comparable<SearchNode> {

        private final SearchNode previous;
        private final int moves;
        private final Board board;

        private int manhattanVal = -1;
        private int hammingVal = -1;

        public SearchNode(SearchNode previous, int moves, Board board) {
            this.previous = previous;
            this.moves = moves;
            this.board = board;
        }

        public Iterable<SearchNode> neighbors() {
            List<SearchNode> searchNodes = new ArrayList<>();
            for (Board neighbor : this.board.neighbors()) {
                if (this.previous == null) {
                    searchNodes.add(new SearchNode(this, this.moves + 1, neighbor));
                }

                if (this.previous != null &&
                        !neighbor.equals(this.previous.board)) {
                    searchNodes.add(new SearchNode(this, this.moves + 1, neighbor));
                }

            }
            return searchNodes;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other)
                return true;
            if (other == null || getClass() != other.getClass())
                return false;
            SearchNode that = (SearchNode) other;
            return board.equals(that.board);
        }

        @Override
        public int hashCode() {
            return Objects.hash(board);
        }

        @Override
        public String toString() {
            return "SearchNode{" +
                    "board=" + board +
                    '}';
        }

        // number of tiles out of place
        public int hamming() {
            if (hammingVal < 0) {
                hammingVal = this.board.manhattan();
            }
            return hammingVal;
        }

        // sum of Manhattan distances between tiles and goal
        public int manhattan() {
            if (manhattanVal < 0) {
                manhattanVal = this.board.manhattan();
            }
            return manhattanVal;
        }

        @Override
        public int compareTo(SearchNode other) {
            int priorityO1 = this.moves + this.manhattan();
            int priority02 = other.moves + other.manhattan();
            return Integer.compare(priorityO1, priority02);
        }
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return this.solution != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return this.solution;
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}
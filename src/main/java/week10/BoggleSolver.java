package week10;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class BoggleSolver {

    private final TrieSet tst = new TrieSet();

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        for (String word : dictionary) {
            tst.add(word);
        }
    }

    private int score(String word) {
        int len = word.length();
        if (len == 3 || len == 4) {
            return 1;
        } else if (len == 5) {
            return 2;
        } else if (len == 6) {
            return 3;
        } else if (len == 7) {
            return 5;
        } else if (len >= 8) {
            return 11;
        }
        return 0;
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        final Set<String> words = new HashSet<>();
        boolean[][] marked = initializeMarked(board);
        for (int i = 0; i < board.rows(); i++) {
            for (int j = 0; j < board.cols(); j++) {
                DieIndex index = new DieIndex(i, j);
                words.addAll(findValidWords("", index, board, marked));
            }
        }
        return words;
    }


    private List<String> findValidWords(String currentWord, DieIndex index, BoggleBoard board, boolean[][] marked) {
        List<String> validWords = new ArrayList<>();
        marked[index.i][index.j] = true;

        char letter = board.getLetter(index.i, index.j);

        final String newWord =
                currentWord +
                        ((letter == 'Q') ? letter + "U" : letter);


        if (newWord.length() >= 3 && tst.contains(newWord)) {
            validWords.add(newWord);
        }
        TrieSet.Node prefix = tst.getPrefixNode(newWord);
        if (prefix != null) {
            for (DieIndex adjIndex : adj(board, index, marked, prefix)) {
                validWords.addAll(findValidWords(newWord, adjIndex, board, marked));
            }
        }
        marked[index.i][index.j] = false;
        return validWords;
    }

    private boolean[][] initializeMarked(BoggleBoard board) {
        boolean[][] marked = new boolean[board.rows()][];
        for (int i = 0; i < marked.length; i++) {
            marked[i] = new boolean[board.cols()];
            Arrays.fill(marked[i], false);
        }
        return marked;
    }

    private List<DieIndex> adj(BoggleBoard board, DieIndex index, boolean[][] marked, TrieSet.Node prefix) {
        List<DieIndex> adjDies = new ArrayList<>();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                final DieIndex newIndex = new DieIndex(index.getI() + i, index.getJ() + j);
                if (newIndex.equals(index)) {
                    continue;
                }

                if (!outOfBounds(newIndex, board)
                        && !marked[newIndex.i][newIndex.j] &&
                        prefix.hasNextChar(board.getLetter(newIndex.i, newIndex.j))) {
                    adjDies.add(newIndex);
                }
            }
        }
        return adjDies;
    }


    private static class TrieSet {

        private static final int R = 26;        // extended ASCII

        private Node root;      // root of trie
        private int n;          // number of keys in trie

        // R-way trie node
        private static class Node {
            private final Node[] next = new Node[R];
            private boolean isString;

            public boolean hasNextChar(char c) {
                return next[c - 65] != null;
            }
        }

        public void add(String key) {
            if (key == null) throw new IllegalArgumentException("argument to add() is null");
            root = add(root, key, 0);
        }

        private Node add(Node x, String key, int d) {
            if (x == null) x = new Node();
            if (d == key.length()) {
                if (!x.isString) n++;
                x.isString = true;
            } else {
                char c = key.charAt(d);
                x.next[c - 65] = add(x.next[c - 65], key, d + 1);
            }
            return x;
        }

        public boolean contains(String key) {
            if (key == null) throw new IllegalArgumentException("argument to contains() is null");
            Node x = get(root, key, 0);
            if (x == null) return false;
            return x.isString;
        }

        private Node get(Node x, String key, int d) {
            if (x == null) return null;
            if (d == key.length()) return x;
            char c = key.charAt(d);
            return get(x.next[c - 65], key, d + 1);
        }

        public boolean hasPrefix(String key) {
            if (key == null) throw new IllegalArgumentException("argument to hasPrefix() is null");
            Node x = get(root, key, 0);
            return x != null;
        }

        public Node getPrefixNode(String key) {
            if (key == null) throw new IllegalArgumentException("argument to getPrefixNode() is null");
            return get(root, key, 0);
        }


    }

    private boolean outOfBounds(DieIndex index, BoggleBoard board) {
        if (index.getI() < 0 || index.getI() >= board.rows()) {
            return true;
        }
        return index.getJ() < 0 || index.getJ() >= board.cols();
    }

    private static class DieIndex {
        private final int i;
        private final int j;

        public DieIndex(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            DieIndex index = (DieIndex) other;
            return i == index.i && j == index.j;
        }

        @Override
        public int hashCode() {
            return Objects.hash(i, j);
        }
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!tst.contains(word)) {
            return 0;
        } else {
            return score(word);
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}

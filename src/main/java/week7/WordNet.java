package week7;

import edu.princeton.cs.algs4.BinarySearchST;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Topological;

import java.util.ArrayList;
import java.util.List;

public class WordNet {

    private final BinarySearchST<String, List<Integer>> words = new BinarySearchST<>();

    private String[] synsets;

    private Digraph digraph;

    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        checkNotNull(synsets);
        checkNotNull(hypernyms);
        constructSynsets(synsets);
        constructHypernyms(hypernyms);
        checkIfRootedDAG();

        this.sap = new SAP(this.digraph);
    }

    private void checkIfRootedDAG() {
        checkIfDAG();
        int rootVertex = getRootVertex();
        checkRootReachability(rootVertex);
    }

    private void checkRootReachability(int root) {
        BreadthFirstDirectedPaths bfs = new BreadthFirstDirectedPaths(this.digraph, root);
        for (int i = 0; i < this.digraph.V(); i++) {
            bfs.hasPathTo(i);
        }
    }

    private void checkIfDAG() {
        Topological topological = new Topological(this.digraph);
        if (!topological.hasOrder()) {
            throw new IllegalArgumentException("It has no order");
        }

    }

    private int getRootVertex() {
        for (int v = 0; v < this.digraph.V(); v++) {
            if (this.digraph.outdegree(v) == 0) {
                return v;
            }
        }
        throw new IllegalArgumentException("It does not have root");
    }

    private void constructHypernyms(String hypernyms) {
        In in = new In(hypernyms);

        String[] lines = in.readAllLines();

        this.digraph = new Digraph(lines.length);

        for (String line : lines) {
            checkNotNull(line);
            String[] tokenized = line.split(",");
            if (tokenized.length < 1) {
                throw new IllegalArgumentException();
            }
            int index = parseInteger(tokenized[0]);
            for (int j = 1; j < tokenized.length; j++) {
                digraph.addEdge(index, parseInteger(tokenized[j]));
            }
        }
    }

    private void constructSynsets(String synsetsList) {
        In in = new In(synsetsList);

        String[] lines = in.readAllLines();
        this.synsets = new String[lines.length];
        for (String line : lines) {
            checkNotNull(line);
            String[] tokenizedLine = line.split(",");
            if (tokenizedLine.length < 3) {
                throw new IllegalArgumentException();
            }
            int index = parseInteger(tokenizedLine[0]);

            String[] synset = tokenizedLine[1].split(" ");
            checkNotNull(synset);

            this.synsets[index] = tokenizedLine[1];
            for (String s : synset) {
                if (this.words.get(s) == null) {
                    this.words.put(s, new ArrayList<>());
                }
                this.words.get(s).add(index);
            }

        }
    }

    private int parseInteger(String index) {
        checkNotNull(index);
        try {
            return Integer.parseInt(index);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private void checkNotNull(Object param) {
        if (param == null) {
            throw new IllegalArgumentException();
        }
    }

    // returns all week7.WordNet nouns
    public Iterable<String> nouns() {
        return this.words.keys();
    }

    // is the word a week7.WordNet noun?
    public boolean isNoun(String word) {
        return this.words.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        checkIfNouns(nounA, nounB);
        List<Integer> v = this.words.get(nounA);
        List<Integer> w = this.words.get(nounB);
        return this.sap.length(v, w);
    }

    private void checkIfNouns(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        checkIfNouns(nounA, nounB);
        List<Integer> v = this.words.get(nounA);
        List<Integer> w = this.words.get(nounB);
        int minAnc = this.sap.ancestor(v, w);
        if (minAnc < 0 || minAnc > this.synsets.length - 1) {
            return null;
        }

        return this.synsets[minAnc];
    }


    // do unit testing of this class
    public static void main(String[] args) {

        WordNet wordnet = new WordNet(args[0], args[1]);

        assert wordnet.distance("horse", "horse") == 0;
        assert wordnet.distance("horse", "cat") == wordnet.distance("cat", "horse");

    }
}

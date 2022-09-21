package week7;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    // constructor takes a week7.WordNet object

    private final WordNet wordNet;

    public Outcast(WordNet wd) {
        this.wordNet = wd;

    }

    // given an array of week7.WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        if (nouns.length == 0) {
            return null;
        }

        String outcast = nouns[0];
        int maxDist = this.sumOfDistances((nouns[0]), this.wordNet, nouns);

        for (int i = 1; i < nouns.length; i++) {
            int newDist = this.sumOfDistances((nouns[i]), this.wordNet, nouns);
            if (newDist > maxDist) {
                maxDist = newDist;
                outcast = nouns[i];
            }
        }

        return outcast;
    }

    private int sumOfDistances(String noun, WordNet wd, String[] nouns) {
        int acc = 0;
        for (String n : nouns) {
            int dist = wd.distance(n, noun);
            acc += Math.max(dist, 0);
        }
        return acc;
    }

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}

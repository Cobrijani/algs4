package week7;

import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.AbstractMap;
import java.util.Map;

public class SAP {

    private final Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph digraph) {
        checkNotNull(digraph);
        this.G = new Digraph(digraph);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        checkOutOfBounds(v);
        checkOutOfBounds(w);
        BreadthFirstDirectedPaths vBfs = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths wBfs = new BreadthFirstDirectedPaths(this.G, w);
        return findAnc(vBfs, wBfs).getKey();
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        checkOutOfBounds(v);
        checkOutOfBounds(w);
        BreadthFirstDirectedPaths vBfs = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths wBfs = new BreadthFirstDirectedPaths(this.G, w);
        return findAnc(vBfs, wBfs).getValue();
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        checkNotNull(v);
        checkNotNull(w);
        validateVertices(v);
        validateVertices(w);
        if (isEmpty(v) || isEmpty(w)) {
            return -1;
        }
        BreadthFirstDirectedPaths vBfs = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths wBfs = new BreadthFirstDirectedPaths(this.G, w);
        return findAnc(vBfs, wBfs).getKey();
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        checkNotNull(v);
        checkNotNull(w);
        validateVertices(v);
        validateVertices(w);
        if (isEmpty(v) || isEmpty(w)) {
            return -1;
        }
        BreadthFirstDirectedPaths vBfs = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths wBfs = new BreadthFirstDirectedPaths(this.G, w);
        return findAnc(vBfs, wBfs).getValue();
    }

    private void validateVertices(Iterable<Integer> vertices) {
        vertices.forEach(vertex -> {
            checkNotNull(vertex);
            checkOutOfBounds(vertex);
        });
    }

    private <T> boolean isEmpty(Iterable<T> tIterable) {
        return !tIterable.iterator().hasNext();
    }

    private void checkNotNull(Object param) {
        if (param == null) {
            throw new IllegalArgumentException("Argument cannot be null");
        }
    }

    private void checkOutOfBounds(int number) {
        if (number < 0 || number >= this.G.V()) {
            throw new IllegalArgumentException("Out of Bounds");
        }
    }

    private Map.Entry<Integer, Integer> findAnc(BreadthFirstDirectedPaths vBfs, BreadthFirstDirectedPaths wBfs) {
        int minDist = Integer.MAX_VALUE;
        int minAnc = -1;
        for (int i = 0; i < this.G.V(); i++) {
            if (vBfs.hasPathTo(i) && wBfs.hasPathTo(i)) {
                int vDist = vBfs.distTo(i);
                int wDist = wBfs.distTo(i);
                if (vDist < 0 || wDist < 0) {
                    continue;
                }
                int newDist = vDist + wDist;
                if (minDist > newDist) {
                    minDist = newDist;
                    minAnc = i;
                }
            }
        }
        return new AbstractMap.SimpleEntry<>(minDist == Integer.MAX_VALUE ? -1 : minDist, minAnc);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}

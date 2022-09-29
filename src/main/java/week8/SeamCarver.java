package week8;

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class SeamCarver {

    private Picture carvedPicture;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        checkNotNull(picture);
        this.carvedPicture = new Picture(picture);
    }

    // current picture
    public Picture picture() {
        return new Picture(carvedPicture);
    }

    // width of current picture
    public int width() {
        return this.carvedPicture.width();
    }

    // height of current picture
    public int height() {
        return this.carvedPicture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (isOutOfBounds(x, y)) {
            throw new IllegalArgumentException();
        }

        if (isBorder(x, y)) {
            return 1000;
        }
        return Math.sqrt((double) calculateGradientX(x, y) + calculateGradientY(x, y));
    }

    private int calculateGradientX(int x, int y) {
        final Color color1 = this.carvedPicture.get(x + 1, y);
        final Color color2 = this.carvedPicture.get(x - 1, y);
        return calculateDiff(color1, color2);
    }

    private int calculateGradientY(int x, int y) {
        final Color color1 = this.carvedPicture.get(x, y + 1);
        final Color color2 = this.carvedPicture.get(x, y - 1);
        return calculateDiff(color1, color2);
    }

    private int calculateDiff(Color first, Color second) {
        int redDiff = first.getRed() - second.getRed();
        int greenDiff = first.getGreen() - second.getGreen();
        int blueDiff = first.getBlue() - second.getBlue();
        return redDiff * redDiff + greenDiff * greenDiff + blueDiff * blueDiff;
    }

    private boolean isBorder(int x, int y) {
        if (x == 0 || x == (width() - 1)) {
            return true;
        }
        return y == 0 || y == (height() - 1);
    }


    private List<Pixel> adjV(int x, int y, double[][] energy) {
        final List<Pixel> edges = new ArrayList<>();
        for (int i = -1; i < 2; i++) {
            int newX = x + i;
            int newY = y + 1;
            if (!isOutOfBounds(newX, newY)) {
                if (energy[newY][newX] < 0) {
                    energy[newY][newX] = energy(newX, newY);
                }
                edges.add(new Pixel(newX, newY, energy[newY][newX]));
            }
        }
        return edges;
    }

    private List<Pixel> adjH(int x, int y, double[][] energy) {
        final List<Pixel> edges = new ArrayList<>();
        for (int i = -1; i < 2; i++) {
            int newX = x + 1;
            int newY = y + i;
            if (!isOutOfBounds(newX, newY)) {
                if (energy[newY][newX] < 0) {
                    energy[newY][newX] = energy(newX, newY);
                }
                edges.add(new Pixel(newX, newY, energy[newY][newX]));
            }
        }
        return edges;
    }


    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] distTo = initializeDistances();
        double[][] energy = initializeDistances(Double.NEGATIVE_INFINITY);
        Arrays.fill(distTo[0], 0);
        Pixel[][] edgeTo = initializeEdges();

        for (int y = 0; y < height(); y++) {
            for (int x = 0; x < width(); x++) {
                for (Pixel e : adjV(x, y, energy)) {
                    relax(distTo, edgeTo, x, y, e);
                }
            }
        }
        return constructSeam(edgeTo, distTo);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[][] distTo = initializeDistances();
        double[][] energy = initializeDistances(Double.NEGATIVE_INFINITY);
        for (int i = 0; i < height(); i++) {
            distTo[i][0] = 0;
        }
        Pixel[][] edgeTo = initializeEdges();
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                for (Pixel e : adjH(x, y, energy)) {
                    relax(distTo, edgeTo, x, y, e);
                }
            }
        }
        return constructHorizontalSeam(edgeTo, distTo);
    }

    private int[] constructHorizontalSeam(Pixel[][] edgeTo, double[][] distTo) {
        int minY = findSmallestY(distTo);
        int[] seam = new int[width()];

        int counter = width() - 1;
        seam[counter--] = minY;

        for (Pixel e = edgeTo[minY][width() - 1]; e != null; e = edgeTo[e.y][e.x]) {
            seam[counter--] = e.y;
        }
        return seam;
    }

    private int[] constructSeam(Pixel[][] edgeTo, double[][] distTo) {
        int minX = findSmallestX(distTo);
        int[] seam = new int[height()];
        int counter = height() - 1;
        seam[counter--] = minX;

        for (Pixel e = edgeTo[height() - 1][minX]; e != null; e = edgeTo[e.y][e.x]) {
            seam[counter--] = e.x;
        }

        return seam;
    }

    private Pixel[][] initializeEdges() {
        Pixel[][] edgeTo = new Pixel[height()][];
        for (int i = 0; i < height(); i++) {
            edgeTo[i] = new Pixel[width()];
        }
        return edgeTo;
    }

    private int findSmallestY(double[][] distTo) {
        int minY = 0;
        double min = distTo[0][width() - 1];
        for (int i = 1; i < height(); i++) {
            double nextEl = distTo[i][width() - 1];
            if (nextEl < min) {
                min = nextEl;
                minY = i;
            }
        }
        return minY;
    }

    private int findSmallestX(double[][] distTo) {
        double[] lastLineDist = distTo[height() - 1];
        double min = lastLineDist[0];
        int minX = 0;
        for (int i = 1; i < width() - 1; i++) {
            if (lastLineDist[i] < min) {
                min = lastLineDist[i];
                minX = i;
            }
        }
        return minX;
    }

    private double[][] initializeDistances() {
        return initializeDistances(Double.POSITIVE_INFINITY);
    }

    private double[][] initializeDistances(double defValue) {
        double[][] distTo = new double[height()][];
        for (int i = 0; i < height(); i++) {
            distTo[i] = new double[width()];
            Arrays.fill(distTo[i], defValue);
        }
        return distTo;
    }

    private void relax(double[][] distTo, Pixel[][] edgeTo, int x, int y, Pixel e) {
        if (distTo[e.y][e.x] > distTo[y][x] + e.weight) {
            distTo[e.y][e.x] = distTo[y][x] + e.weight;
            edgeTo[e.y][e.x] = new Pixel(x, y, distTo[y][x] + e.weight);
        }
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        checkNotNull(seam);
        checkLength(seam, width());
        checkNotAdjacent(seam);
        if (height() <= 1) {
            throw new IllegalArgumentException();
        }

        final Picture newPicture = new Picture(width(), height() - 1);

        for (int x = 0; x < width(); x++) {
            int offset = 0;
            for (int y = 0; y < height(); y++) {
                if (y == seam[x]) {
                    offset++;
                } else {
                    newPicture.set(x, y - offset, this.carvedPicture.get(x, y));
                }
            }
        }
        this.carvedPicture = newPicture;
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        checkNotNull(seam);
        checkLength(seam, height());
        checkNotAdjacent(seam);
        if (width() <= 1) {
            throw new IllegalArgumentException();
        }

        final Picture newPicture = new Picture(width() - 1, height());

        for (int y = 0; y < height(); y++) {
            int offset = 0;
            for (int x = 0; x < width(); x++) {
                if (x == seam[y]) {
                    offset++;
                } else {
                    newPicture.set(x - offset, y, this.carvedPicture.get(x, y));
                }
            }
        }
        this.carvedPicture = newPicture;
    }

    private void checkLength(int[] seam, int expected) {
        if (seam.length != expected) {
            throw new IllegalArgumentException();
        }
    }

    private void checkNotAdjacent(int[] seam) {
        for (int i = 0; i + 1 < seam.length; i++) {
            int diff = seam[i] - seam[i + 1];
            if (Math.abs(diff) > 1) {
                throw new IllegalArgumentException();
            }
        }
    }

    private boolean isOutOfBounds(int x, int y) {
        return x < 0 || y < 0 || x >= width() || y >= height();
    }

    private void checkNotNull(Object param) {
        if (param == null) {
            throw new IllegalArgumentException();
        }
    }

    public static void main(String[] args) {
        Picture picture = new Picture("https://coursera.cs.princeton.edu/algs4/assignments/seam/files/6x5.png");
        SeamCarver seamCarver = new SeamCarver(picture);

        int[] result = seamCarver.findVerticalSeam();

        assert Arrays.equals(result, new int[]{3, 4, 3, 2, 1});

        seamCarver.removeVerticalSeam(result);

        int[] horizontalSeam = seamCarver.findHorizontalSeam();

        seamCarver.removeHorizontalSeam(horizontalSeam);
    }

    private static class Pixel {
        private final int x;
        private final int y;
        private final double weight;


        public Pixel(int x, int y, double weight) {
            this.x = x;
            this.y = y;
            this.weight = weight;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public double getWeight() {
            return weight;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            Pixel pixel = (Pixel) other;
            return x == pixel.x && y == pixel.y && weight == pixel.weight;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, weight);
        }

        @Override
        public String toString() {
            return "Pixel{" +
                    "x=" + x +
                    ", y=" + y +
                    ", weight=" + weight +
                    '}';
        }
    }


    //  unit testing (optional)

}

package week3;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FastCollinearPoints {

    private final Point[] points;

    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }
        checkIfContainsNull(points);
        checkIfContainsRepeatPoint(points);
        this.points = new Point[points.length];
        System.arraycopy(points, 0, this.points, 0, points.length);
    }

    public int numberOfSegments() {
        return segments().length;
    }

    private Point[] constructTemp(Point[] src, int indexExcluded) {
        Point[] dest = new Point[points.length - (indexExcluded + 1)];
        int k = 0;
        for (int j = indexExcluded; j < src.length; j++) {
            if (indexExcluded == j) {
                continue;
            }
            dest[k++] = points[j];
        }
        return dest;
    }

    public LineSegment[] segments() {
        List<LineSegment> segments = new ArrayList<>();

        for (int i = 0; i < points.length; i++) {
            Point[] temp = constructTemp(points, i);
            int tempLen = temp.length;
            final Point p = points[i];
            Arrays.sort(temp, p.slopeOrder());

            List<Point> foundSegments = new ArrayList<>();

            int counter = 0;

            Point end = null;

            for (int j = 0; j + 1 < tempLen; j++) {
                final Point q1 = temp[j];
                final Point q2 = temp[j + 1];

                if (p.slopeTo(q1) == p.slopeTo(q2)) {
                    counter++;
                    end = q2;
                } else {
                    if (counter >= 2) {
                        foundSegments.add(p);
                        foundSegments.add(q1);
                    }
                    counter = 0;
                }
            }

            if (!foundSegments.isEmpty()) {
                for (int j = 0; j + 1 < foundSegments.size(); j = j + 2) {
                    Point first = foundSegments.get(j);
                    Point last = foundSegments.get(j + 1);

                    if (isSmallest(first, last)) {
                        segments.add(new LineSegment(first, last));
                    }
                }
            }
            if (counter >= 2 && end != null && isSmallest(p, end)) {
                segments.add(new LineSegment(p, end));
            }
        }
        LineSegment[] returnSegment = new LineSegment[segments.size()];
        return segments.toArray(returnSegment);
    }

    private boolean isSmallest(Point p, Point q) {
        double slope = p.slopeTo(q);
        Point smallest = p;
        for (Point point : this.points) {
            if (slope == p.slopeTo(point) && point.compareTo(smallest) < 0) {
                smallest = point;
            }
        }
        return smallest.compareTo(p) == 0;
    }

    private void checkIfContainsNull(Point[] pointsArray) {
        for (Point p : pointsArray) {
            if (p == null) {
                throw new IllegalArgumentException();
            }
        }
    }

    private void checkIfContainsRepeatPoint(Point[] pointsArray) {
        Arrays.sort(pointsArray);
        for (int i = 0; i + 1 < pointsArray.length; i++) {
            if (pointsArray[i].compareTo(pointsArray[i + 1]) == 0) {
                throw new IllegalArgumentException();
            }
        }
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}

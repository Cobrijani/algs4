package week3;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteCollinearPoints {

    private final Point[] points;

    public BruteCollinearPoints(Point[] points) {
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
    }     // the number of line segments

    public LineSegment[] segments() {
        List<LineSegment> segments = new ArrayList<>();
        for (int firstPointIdx = 0; firstPointIdx < points.length; firstPointIdx++) {
            for (int secondPointIdx = firstPointIdx; secondPointIdx < points.length; secondPointIdx++) {
                if (firstPointIdx == secondPointIdx) {
                    continue;
                }
                for (int thirdPointIdx = secondPointIdx; thirdPointIdx < points.length; thirdPointIdx++) {
                    if (secondPointIdx == thirdPointIdx) {
                        continue;
                    }
                    for (int fourthPtIdx = thirdPointIdx; fourthPtIdx < points.length; fourthPtIdx++) {
                        if (thirdPointIdx == fourthPtIdx) {
                            continue;
                        }
                        final Point point1 = points[firstPointIdx];
                        final Point point2 = points[secondPointIdx];
                        final Point point3 = points[thirdPointIdx];
                        final Point point4 = points[fourthPtIdx];

                        if (point1.slopeTo(point2) == point2.slopeTo(point3) &&
                                point2.slopeTo(point3) == point3.slopeTo(point4)) {
                            segments.add(new LineSegment(point1, point4));
                        }
                    }
                }
            }
        }
        LineSegment[] returnSegment = new LineSegment[segments.size()];
        return segments.toArray(returnSegment);
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
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}

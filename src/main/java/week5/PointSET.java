package week5;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.TreeSet;
import java.util.stream.Collectors;

public class PointSET {

    private final TreeSet<Point2D> treeSet;

    public PointSET() {
        treeSet = new TreeSet<>((o1, o2) -> {
            if (o1.x() == o2.x()) {
                return Point2D.Y_ORDER.compare(o1, o2);
            } else {
                return Point2D.X_ORDER.compare(o1, o2);
            }
        });
    }

    public boolean isEmpty() {
        return treeSet.isEmpty();
    }

    public int size() {
        return treeSet.size();
    }

    public void insert(Point2D p) {
        requireNotNull(p);
        if (!contains(p)) {
            treeSet.add(p);
        }
    }       // add the point to the set (if it is not already in the set)

    public boolean contains(Point2D p) {
        requireNotNull(p);
        return treeSet.contains(p);
    }         // does the set contain point p?

    public void draw() {
        treeSet
                .forEach(Point2D::draw);
    }              // draw all points to standard draw

    public Iterable<Point2D> range(RectHV rect) {
        requireNotNull(rect);
        return treeSet.stream()
                .filter(rect::contains)
                .collect(Collectors.toList());
    }    // all points that are inside the rectangle (or on the boundary)

    public Point2D nearest(Point2D p) {
        requireNotNull(p);
        return treeSet
                .stream()
                .min((o1, o2) -> {
                    var distance1 = o1.distanceSquaredTo(p);
                    var distance2 = o2.distanceSquaredTo(p);
                    return Double.compare(distance1, distance2);
                })
                .orElse(null);
    }    // a nearest neighbor in the set to point p; null if the set is empty

    public static void main(String[] args) {

        Point2D a = new Point2D(0.2, 0.3);
        Point2D b = new Point2D(0.4, 0.1);
        Point2D c = new Point2D(0.6, 0.5);

        PointSET pointSET = new PointSET();

        pointSET.insert(a);
        pointSET.insert(b);
        pointSET.insert(c);

        assert pointSET.contains(new Point2D(0.4, 0.1));
        assert !pointSET.contains(new Point2D(0.4, 0.2));
        assert pointSET.nearest(new Point2D(0.7, 0.5)).equals(new Point2D(0.6, 0.5));
        Iterable<Point2D> points = pointSET.range(new RectHV(0.0, 0.0, 0.5, 0.3));
        points.forEach(x -> {
            assert x.equals(a) || x.equals(b);
        });

        pointSET.draw();
    }

    private void requireNotNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
    }
}

package week5;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class KdTree {

    private Node root = null;
    private int size = 0;

    public KdTree() {

    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size;
    }

    public void insert(Point2D p) {
        requireNotNull(p);
        if (root == null) {
            root = new Node(p, null, null);
            size = size + 1;
            return;
        }
        if (contains(p)) {
            return;
        }
        insert(root, p, true);
        size = size + 1;
    }

    private void insert(Node rootNode, Point2D p, boolean vertical) {
        final Point2D curr = rootNode.curr;
        final boolean shouldGoLeft = vertical ? curr.x() > p.x() : curr.y() > p.y();

        if (shouldGoLeft) {
            goLeft(rootNode, p, vertical);
        } else {
            goRight(rootNode, p, vertical);
        }
    }

    private void goRight(Node rootNode, Point2D p, boolean vertical) {
        if (rootNode.right == null) {
            rootNode.right = new Node(p, null, null);
        } else {
            insert(rootNode.right, p, !vertical);
        }
    }

    private void goLeft(Node rootNode, Point2D p, boolean vertical) {
        if (rootNode.left == null) {
            rootNode.left = new Node(p, null, null);
        } else {
            insert(rootNode.left, p, !vertical);
        }
    }

    public boolean contains(Point2D p) {
        requireNotNull(p);
        if (isEmpty()) {
            return false;
        }
        return contains(root, p, 0);
    }

    private boolean contains(Node rootNode, Point2D p, int height) {
        if (rootNode == null) {
            return false;
        }
        boolean vertical = height % 2 == 0;
        Point2D curr = rootNode.curr;
        if (curr.equals(p)) {
            return true;
        }
        final boolean shouldGoLeft = vertical ? curr.x() > p.x() : curr.y() > p.y();
        return contains(shouldGoLeft ? rootNode.left : rootNode.right, p, height + 1);
    }

    public void draw() {
        draw(root, 0, 0, 1, 1, true);
    }

    private void draw(Node rootNode, double xMin, double yMin, double xMax, double yMax, boolean vertical) {
        if (rootNode == null) {
            return;
        }
        rootNode.curr.draw();
        if (vertical) {
            StdDraw.line(rootNode.curr.x(), yMin, rootNode.curr.x(), yMax);
        } else {
            StdDraw.line(xMin, rootNode.curr.y(), xMax, rootNode.curr.y());
        }
        draw(rootNode.left, xMin, yMin, rootNode.curr.x(), rootNode.curr.y(), !vertical);
        draw(rootNode.right, rootNode.curr.x(), rootNode.curr.y(), xMax, yMax, !vertical);
    }

    public Iterable<Point2D> range(RectHV rect) {
        requireNotNull(rect);
        if (isEmpty()) {
            return Collections.emptyList();
        }
        return range(root, rect, 0);
    }

    private List<Point2D> range(Node rootNode, RectHV rect, int height) {
        if (rootNode == null) {
            return Collections.emptyList();
        }
        boolean vertical = height % 2 == 0;
        final List<Point2D> points = new ArrayList<>();
        if (rect.contains(rootNode.curr)) {
            points.add(rootNode.curr);
        }

        boolean onLeft = vertical ? rect.xmax() < rootNode.curr.x() : rect.ymax() < rootNode.curr.y();
        boolean partiallyOnLeft = vertical ? rect.xmin() < rootNode.curr.x() : rect.ymin() < rootNode.curr.y();

        if (onLeft) {
            points.addAll(range(rootNode.left, rect, height + 1));
        } else if (partiallyOnLeft) {
            // its partially on the left
            points.addAll(range(rootNode.left, rect, height + 1));
            points.addAll(range(rootNode.right, rect, height + 1));
        } else {
            // completely on right
            points.addAll(range(rootNode.right, rect, height + 1));
        }
        return points;
    }

    public Point2D nearest(Point2D p) {
        requireNotNull(p);
        if (root == null) {
            return null;
        }
        return Optional
                .ofNullable(nearest(root, p, true,
                        new NearestPoint(root.curr, root.curr.distanceSquaredTo(p))))
                .map(x -> x.point).orElse(null);
    }

    private NearestPoint nearest(Node rootNode, Point2D p, boolean vertical, NearestPoint currentNearest) {
        if (rootNode == null) {
            return currentNearest;
        }
        final Point2D curr = rootNode.curr;
        final double currDist = curr.distanceSquaredTo(p);
        final NearestPoint currNear = (currDist > currentNearest.distance) ? currentNearest : new NearestPoint(curr, currDist);

        final double dDist = vertical ? currNear.point.y() - p.y() : currNear.point.x() - p.x();

        if ((dDist * dDist) > currNear.distance) {
            return currNear;
        }

        final boolean shouldGoLeftFirst = vertical ? curr.x() > p.x() : curr.y() > p.y();
        final NearestPoint firstNearest = nearest(shouldGoLeftFirst ? rootNode.left : rootNode.right, p, !vertical, currNear);
        final NearestPoint secondNearest = nearest(shouldGoLeftFirst ? rootNode.right : rootNode.left, p, !vertical, currNear);

        if (firstNearest.distance < secondNearest.distance) {
            return firstNearest;
        } else {
            return secondNearest;
        }
    }

    private void requireNotNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
    }

    public static void main(String[] args) {

        Point2D a = new Point2D(0.5, 0.5);
        Point2D b = new Point2D(0.125, 0.25);
        Point2D c = new Point2D(0.0, 0.625);
        Point2D d = new Point2D(0.625, 0.875);
        Point2D e = new Point2D(0.875, 0.375);

        KdTree tree = new KdTree();
        Stream.of(a, b, c, d, e).forEach(tree::insert);

        assert tree.nearest(new Point2D(0.75, 1.0)).equals(d);

    }

    private static class NearestPoint {
        private final Point2D point;
        private final double distance;

        public NearestPoint(Point2D point, double distance) {
            this.point = point;
            this.distance = distance;
        }
    }

    private static class Node {

        private final Point2D curr;
        private Node left;
        private Node right;

        public Node(Point2D curr, Node left, Node right) {

            this.curr = curr;
            this.left = left;
            this.right = right;
        }

    }
}

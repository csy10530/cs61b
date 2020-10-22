package bearmaps;

import java.util.List;

public class KDTree implements PointSet {
  private static final boolean VERTICAL = false;

  private Node root;

  public KDTree(List<Point> points) {
    for (Point point : points) {
      root = add(point, root, VERTICAL);
    }
  }

  private Node add(Point point, Node node, boolean orientation) {
    if (node == null)
      return new Node(point, orientation);

    if (node.point.equals(point)) return node;
    boolean newOrientation = !orientation;
    int cmp = compare(point, node.point, orientation);

    if (cmp >= 0)
      node.right = add(point, node.right, newOrientation);
    else
      node.left = add(point, node.left, newOrientation);

    return node;
  }

  public Point nearest(double x, double y) {
    Point goal = new Point(x, y);
    return nearest(root, goal, root).point;
  }

  private Node nearest(Node n, Point goal, Node best) {
    if (n == null) return best;
    if (n.distance(goal) < best.distance(goal)) best = n;

    int cmp = compare(goal, n.point, n.isVertical);
    Node goodSide, badSide;
    if (cmp < 0) {
      goodSide = n.left;
      badSide = n.right;
    } else {
      goodSide = n.right;
      badSide = n.left;
    }

    best = nearest(goodSide, goal, best);
    if (prune(n, goal, best))
      best = nearest(badSide, goal, best);

    return best;
  }

  private boolean prune(Node n, Point goal, Node best) {
    if (n == null)
      return false;

    Point tmp;
    if (n.isVertical)
      tmp = new Point(goal.getX(), n.point.getY());
    else
      tmp = new Point(n.point.getX(), goal.getY());

    return Point.distance(tmp, goal) < Point.distance(best.point, goal);
  }

  private int compare(Point a, Point b, boolean isVertical) {
    if (isVertical)
      return Double.compare(a.getY(), b.getY());

    return Double.compare(a.getX(), b.getX());
  }

  private class Node {
    private boolean isVertical;
    private Point point;
    private Node left;
    private Node right;

    private Node(Point point, boolean isVertical) {
      this.point = point;
      this.isVertical = isVertical;
    }

    private double distance(Point point) {
      return Math.sqrt(Point.distance(this.point, point));
    }
  }

  public static void main(String[] args) {
    Point a = new Point(2, 3);
    Point b = new Point(4, 2);
    Point c = new Point(4, 5);
    Point d = new Point(3, 3);
    Point e = new Point(1, 5);
    Point f = new Point(4, 4);

    KDTree nn = new KDTree(List.of(a, b, c, d, e, f));
  }
}

package bearmaps;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.princeton.cs.algs4.Stopwatch;

import static org.junit.Assert.assertEquals;

public class KDTreeTest {

  private static Random r = new Random(500);

  @Test
  public void test() {
    Point p1 = new Point(1.1, 2.2); // constructs a Point with x = 1.1, y = 2.2
    Point p2 = new Point(3.3, 4.4);
    Point p3 = new Point(-2.9, 4.2);

    NaivePointSet nn = new NaivePointSet(List.of(p1, p2, p3));
    Point ret = nn.nearest(3.0, 4.0); // returns p2
    assertEquals(3.3, ret.getX(), 0.01);
    assertEquals(4.4, ret.getY(), 0.01);
  }

  // This is not really a test, and the user can just check the Java visualizer to make
  // sure the insertion is correct.
  @Test
  public void testConstructor() {
    Point p1 = new Point(2, 3);
    Point p2 = new Point(4, 2);
    Point p3 = new Point(4, 2);
    Point p4 = new Point(4, 5);
    Point p5 = new Point(3, 3);
    Point p6 = new Point(1, 5);
    Point p7 = new Point(4, 4);

    KDTree kd = new KDTree(List.of(p1, p2, p3, p4, p5, p6, p7));
    Point expected = new Point(1, 5);
    Point actual = kd.nearest(0, 7);
    assertEquals(expected, actual);
  }

  private Point randomPoint() {
    double x = r.nextDouble();
    double y = r.nextDouble();
    return new Point(x, y);
  }


  /* return N random points */
  private List<Point> randomPoint(int n) {
    List<Point> points = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      points.add(randomPoint());
    }
    return points;
  }

  // A randomized testing cite from @Josh Hug:
  // https://www.youtube.com/watch?v=lp80raQvE5c&feature=youtu.be
  @Test
  public void test10000Point() {
    List<Point> points1000 = randomPoint(100000);
    NaivePointSet nps = new NaivePointSet(points1000);
    KDTree kd = new KDTree(points1000);

    List<Point> points200 = randomPoint(10000);
    Stopwatch st = new Stopwatch();
    for (Point p : points200) {
      Point expected = nps.nearest(p.getX(), p.getY());
      Point actual = kd.nearest(p.getX(), p.getY());
      assertEquals(expected, actual);
    }
    System.out.println(st.elapsedTime());
  }

  @Test
  public void testTimeDifference() {
    List<Point> points1000 = randomPoint(100000);
    NaivePointSet nps = new NaivePointSet(points1000);
    KDTree kd = new KDTree(points1000);

    Stopwatch sw = new Stopwatch();
    List<Point> points200 = randomPoint(10000);
    for (Point p : points200) {
      Point a = nps.nearest(p.getX(), p.getY());
    }
    System.out.println("Time elapsed for naive solution: " + sw.elapsedTime());
    for (Point p : points200) {
      Point a = kd.nearest(p.getX(), p.getY());
    }
    System.out.println("Time elapsed for KDTree solution: " + sw.elapsedTime());
  }
}

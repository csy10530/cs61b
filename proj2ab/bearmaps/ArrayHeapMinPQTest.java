package bearmaps;

import org.junit.Before;
import org.junit.Test;

import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stopwatch;

import static org.junit.Assert.assertEquals;

public class ArrayHeapMinPQTest {

  private int[] database1;
  private int[] database2;
  private ArrayHeapMinPQ<Integer> test;

  @Before
  public void init() {
    test = new ArrayHeapMinPQ();
    database1 = new int[16];
    for (int i = 0; i < database1.length - 1; i++) {
      database1[i] = database1.length - i;
    }
    database2 = new int[16];
    for (int i = 0; i < database2.length - 1; i++) {
      database2[i] = i;
    }

  }
/*
  @Test
  public void testAdd() {
    for (int i = 0; i < database1.length - 1; i++) {
      test.add(database1[i], database1[i]);
    }
    test.printHeap();
  }

 */

  @Test
  public void testAdd() {
    ArrayHeapMinPQ<String> pq = new ArrayHeapMinPQ<>();
    pq.add("C", 3);
    pq.add("A", 1);
    pq.add("B", 2);
    assertEquals(3, pq.size());
  }

  @Test
  public void testGetSmallest() {
    ArrayHeapMinPQ<String> pq = new ArrayHeapMinPQ<>();
    pq.add("B", 2);
    pq.add("C", 3);
    pq.add("A", 1);
    assertEquals("A", pq.getSmallest());
  }

  @Test
  public void testRemoveSmallest() {
    ArrayHeapMinPQ<String> pq = new ArrayHeapMinPQ<>();
    pq.add("A", 1);
    pq.add("C", 3);
    pq.add("B", 2);
    String s = pq.removeSmallest();
    assertEquals("A", s);
    assertEquals(2, pq.size());
  }

  @Test
  public void testChangePriority() {
    ArrayHeapMinPQ<String> pq = new ArrayHeapMinPQ<>();
    pq.add("A", 1);
    pq.add("C", 3);
    pq.add("B", 2);
    pq.changePriority("A", 5);
    pq.changePriority("C", 1);
    assertEquals("C", pq.getSmallest());
  }

  @Test(expected = NoSuchElementException.class)
  public void testNoSuchElement() {
    ArrayHeapMinPQ<String> pq = new ArrayHeapMinPQ<>();
    pq.add("A", 1);
    pq.add("C", 3);
    pq.add("B", 2);
    pq.changePriority("D", 7);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIllegalArgument() {
    ArrayHeapMinPQ<String> pq = new ArrayHeapMinPQ<>();
    pq.add("A", 1);
    pq.add("C", 3);
    pq.add("A", 2);
  }

  @Test
  public void testTime() {
    Stopwatch sw = new Stopwatch();
    ArrayHeapMinPQ<Integer> temp = new ArrayHeapMinPQ<>();
    for (int i = 100000; i > 0; i--) {
      temp.add(i, i);
    }
    for (int i = 1; i <= 100000; i++) {
      temp.removeSmallest();
    }
    System.out.println("Add and Remove: Total time elapsed: " + sw.elapsedTime() + " seconds.");
    sw = new Stopwatch();

    NaiveMinPQ<Integer> temp2 = new NaiveMinPQ<>();
    for (int i = 100000; i > 0; i--) {
      temp2.add(i, i);
    }
    for (int i = 1; i <= 100000; i++) {
      temp2.removeSmallest();
    }
    System.out.println("Add and Remove: Total time elapsed: " + sw.elapsedTime() + " seconds.");
    temp = new ArrayHeapMinPQ<>();
    temp2 = new NaiveMinPQ<>();
    for (int i = 100000; i > 0; i--) {
      temp.add(i, i);
      temp2.add(i, i);
    }
    sw = new Stopwatch();
    for (int i = 100000; i > 0; i--) {
      temp.changePriority(i, 100000 - i);
    }
    System.out.println("ChangePriority: Total time elapsed: " + sw.elapsedTime() + " seconds.");
    sw = new Stopwatch();
    for (int i = 100000; i > 0; i--) {
      temp2.changePriority(i, 100000 - i);
    }
    System.out.println("ChangePriority: Total time elapsed: " + sw.elapsedTime() + " seconds.");
  }

  @Test
  public void testRandom() {
    ArrayHeapMinPQ<Integer> temp = new ArrayHeapMinPQ<>();
    NaiveMinPQ<Integer> temp2 = new NaiveMinPQ<>();
    for (int i = 1; i < 100000; i++) {
      int k = StdRandom.uniform(1, 100000);
      if (!temp.contains(k)) {
        temp.add(k, k);
        temp2.add(k, k);
      }
    }
    for (int i = 1; i <= temp.size(); i++) {
      assertEquals(temp2.removeSmallest(), temp.removeSmallest());
    }
  }
}

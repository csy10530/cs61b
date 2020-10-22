package bearmaps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.princeton.cs.algs4.Stopwatch;

public class ArrayHeapMinPQ<T> implements ExtrinsicMinPQ<T> {
  private List<Node> items;
  private Map<T, Integer> entries;
  private int n;

  public ArrayHeapMinPQ() {
    items = new ArrayList<>();
    entries = new HashMap<>();
    Node emptyNode = new Node();
    items.add(emptyNode);
    n = 0;
  }

  @Override
  public void add(T item, double priority) throws IllegalArgumentException {
    if (contains(item))
      throw new IllegalArgumentException("Item already exists");

    Node node = new Node(item, priority);
    items.add(node);
    entries.put(item, ++n);
    swim(n);
  }

  private void swim(int idx) {
    while (idx > 1 && smaller(idx, idx / 2)) {
      exch(idx, idx / 2);
      idx /= 2;
    }
  }

  private boolean smaller(int i, int j) {
    return items.get(i).getPriority() < items.get(j).getPriority();
  }

  @Override
  public boolean contains(T item) {
    return entries.containsKey(item);
  }

  @Override
  public T getSmallest() throws NoSuchElementException {
    if (n == 0)
      throw new NoSuchElementException("The queue is empty");

    return items.get(1).getItem();
  }

  @Override
  public T removeSmallest() throws NoSuchElementException {
    if (n == 0)
      throw new NoSuchElementException("The queue is empty");

    T item = items.get(1).getItem();
    exch(1, n--);
    sink(1);

    return item;
  }

  private void sink(int k) {
    while (k * 2 <= n) {
      int j = k * 2;
      if (j < n && smaller(j + 1, j)) j++;
      if (!smaller(j, k)) break;
      exch(k, j);
      k = j;
    }
  }

  private void exch(int i, int j) {
    Collections.swap(items, i, j);

    // update position in the hash map
    T item0 = items.get(i).getItem();
    T item1 = items.get(j).getItem();
    entries.put(item0, j);
    entries.put(item1, i);
  }

  @Override
  public int size() {
    return n;
  }

  @Override
  public void changePriority(T item, double priority) throws NoSuchElementException {
    if (!contains(item))
      throw new NoSuchElementException("No such element");

    Node node = items.get(entries.get(item));
    double oldPriority = node.getPriority();
    node.setPriority(priority);

    // if new priority is higher, sink; else, swim
    if (oldPriority < priority) sink(entries.get(item));
    else swim(entries.get(item));
  }

  private class Node {
    private T item;
    private double priority;

    private Node(T item, double priority) {
      this.item = item;
      this.priority = priority;
    }

    private Node() {

    }

    private T getItem() {
      return item;
    }

    private double getPriority() {
      return priority;
    }

    private void setPriority(double priority) {
      this.priority = priority;
    }
  }
}

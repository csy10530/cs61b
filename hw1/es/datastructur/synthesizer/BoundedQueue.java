package es.datastructur.synthesizer;

import java.util.Iterator;

public interface BoundedQueue<T> extends Iterable<T> {
  int capacity();

  int fillCount();

  void enqueue(T x) throws RuntimeException, IllegalArgumentException;

  T dequeue() throws RuntimeException;

  T peek() throws RuntimeException;

  default boolean isEmpty() {
    return fillCount() == 0;
  }

  default boolean isFull() {
    return fillCount() == capacity();
  }

  Iterator iterator();
}

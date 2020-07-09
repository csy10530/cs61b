package es.datastructur.synthesizer;
import java.util.Iterator;

public class ArrayRingBuffer<T> implements BoundedQueue<T> {
    /* Index for the next dequeue or peek. */
    private int first;
    /* Index for the next enqueue. */
    private int last;
    /* Variable for the fillCount. */
    private int fillCount;
    /* Array for storing the buffer data. */
    private T[] rb;

    /**
     * Create a new ArrayRingBuffer with the given capacity.
     */
    public ArrayRingBuffer(int capacity) throws IllegalArgumentException {
        if (capacity < 0)
            throw new IllegalArgumentException("Can't have negative capacity");

        rb = (T[]) new Object[capacity];
        first = 0;
        last = 0;
        fillCount = 0;
    }

    /**
     * Adds x to the end of the ring buffer. If there is no room, then
     * throw new RuntimeException("Ring buffer overflow").
     * Can't add null items.
     */
    @Override
    public void enqueue(T x) throws RuntimeException, IllegalArgumentException {
        if (isFull())
            throw new RuntimeException("Ring buffer overflow");

        if (x == null)
            throw new IllegalArgumentException("Can't add null items");

        rb[last++] = x;
        fillCount++;
        if (last == rb.length) last = 0;
    }

    /**
     * Dequeue oldest item in the ring buffer. If the buffer is empty, then
     * throw new RuntimeException("Ring buffer underflow").
     */
    @Override
    public T dequeue() throws RuntimeException {
        if (isEmpty())
            throw new RuntimeException("Ring buffer underflow");

        T item = rb[first];
        rb[first++] = null;
        fillCount++;

        if (first == rb.length) first = 0;
        return item;
    }

    /**
     * Return oldest item, but don't remove it. If the buffer is empty, then
     * throw new RuntimeException("Ring buffer underflow").
     */
    @Override
    public T peek() throws RuntimeException {
        if (isEmpty())
            throw new RuntimeException("Ring buffer underflow");
        return rb[first];
    }

    @Override
    public int capacity() {
        return rb.length;
    }

    @Override
    public int fillCount() {
        return fillCount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (this == o) return true;
        if (this.getClass() != o.getClass()) return false;

        ArrayRingBuffer<T> other = (ArrayRingBuffer<T>) o;

        if (this.capacity() != other.capacity() || this.capacity() != other.capacity())
            return false;

        int i = 0;
        for (T item : other) {
            if (!item.equals(rb[i++])) return false;
        }
        return true;
    }

    @Override
    public Iterator iterator() {
        return new BufferIterator();
    }

    private class BufferIterator implements Iterator<T> {
        int pos;

        private BufferIterator() {
            pos = 0;
        }

        @Override
        public boolean hasNext() {
            return pos < last;
        }

        @Override
        public T next() {
            T item = rb[pos];
            pos++;
            return item;
        }
    }
}

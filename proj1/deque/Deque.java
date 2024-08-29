package deque;

public interface Deque<type> {
    void addFirst(type item);

    void addLast(type item);

    // Returns true if deque is empty, false otherwise.
    default boolean isEmpty() {
        return size() == 0;
    }

    int size();

    void printDeque();

    type removeFirst();

    type removeLast();

    type get(int index);

    boolean equals(Object o);
}

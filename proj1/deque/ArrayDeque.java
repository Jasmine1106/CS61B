package deque;

public class ArrayDeque<type> {
    private type[] items = (type[]) new Object[8];
    private int size;
    private int nextFirst;
    private int nextLast;

    // Creates an empty array deque.
    public ArrayDeque() {
        size = 0;
        nextFirst = 3;
        nextLast = 4;
    }
    // creat a one item array
    public ArrayDeque(type item) {
        items[3] = item;
        size = 1;
        nextFirst = 2;
        nextLast = 4;
    }

    /** Resizing the underlying array to the target capacity*/
    private void resize(int capacity) {
        type[] a = (type []) new Object[capacity];
        int firstPos = Math.abs(capacity - size) / 2;
        System.arraycopy(items, nextFirst + 1, a, firstPos, size);
        nextFirst = firstPos - 1;
        nextLast = firstPos + size;
        items = a;

    }

    // Adds an item of type T to the front of the deque. You can assume that item is never null.
    public void addFirst(type item) {
        items[nextFirst] = item;
        nextFirst -= 1;
        size += 1;
        if ( nextFirst == -1) {
            resize(size * 2);
        }
    }

    public void addLast(type item) {
        items[nextLast] = item;
        nextLast += 1;
        size += 1;
        if ( nextLast == items.length) {
            resize(size * 2);
        }
    }

    // Returns true if deque is empty, false otherwise.
    public boolean isEmpty() {
        if (size <= 0) {
            return true;
        } return false;
    }

    public int size() {
        if (size < 0) {
            return 0;
        } return size;
    }

    // Removes and returns the item at the front of the deque. If no such item exists, returns null.
    public type removeFirst() {
        if (isEmpty()) {
            return null;
        }
        nextFirst += 1;
        type return_value = items[nextFirst];
        items[nextFirst] = null;
        size -= 1;
        reSize();
        return return_value;
    }


    // Removes and returns the item at the back of the deque. If no such item exists, returns null.
    public type removeLast() {
        if (isEmpty()) {
            return null;
        }
        nextLast -= 1;
        type return_value = items[nextLast];
        items[nextLast] = null;
        size -= 1;
        reSize();
        return return_value;
    }

    // if usage is too low, resizeing array
    private void reSize() {
        if (isEmpty()) {
            resize(8);
        } else if (items.length / 4 > size && size >= 4) {
            resize(size * 2);
        }
    }


    // get the index'th item
    public type get(int index) {
        if (index < 0 && index > size - 1) {
            return null;
        }
        return items[nextFirst + 1 + index];
    }

    // print
    public void printDeque() {
        for (int i = 0; i < size; i += 1) {
            System.out.print(get(i));
        } System.out.println();
    }



}

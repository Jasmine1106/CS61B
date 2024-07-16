package deque;

public class ArrayDeque<type> {
    private type[] items;
    private int size;
    private int len = 100; // inital length of ArrayDeque
    private int back_index = 1; // means the number of back items
    private double usage = size / len;

    // Creates an empty array deque.
    public ArrayDeque() {
        items = (type[]) new Object[len];
        size = 0;
    }

    /** Resizing the underlying array to the target capacity*/
    private void resize(int capacity) {
        type[] a = (type []) new Object[capacity];
        System.arraycopy(items, 0, a, 0, size);
        items = a;
        len = capacity; // keeping track of the chanind length of arrar deque.
        back_index = 1; // resizeing when new array created.
    }

    // Adds an item of type T to the front of the deque. You can assume that item is never null.
    public void addFirst(type item) {
        if ( size == items.length) {
            resize(size * 2);
        }
        items[len - back_index] = item;  // From the back of the deque,move forwards with back_index.
        back_index += 1;
        size += 1;
    }

    public void addLast(type item) {
       if (size == items.length) {
           resize (size * 2);
       }
       items[size] = item;
       size += 1;
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
        type return_value = items[len - back_index];
        items[len - back_index] = null;
        back_index -= 1;
        size -= 1;
        return return_value;
    }

    // Removes and returns the item at the back of the deque. If no such item exists, returns null.
    public type removeLast() {
        type return_value = items[size - 1];
        items[size] = null;
        size -= 1;
        return return_value;
    }


    // get the index'th item
    public type get(int index) {
        return items[index];
    }

    // save memory when it was wasted
    public void save_memory(double usage) {
        if (usage < 0.25) {
            resize(len / 2); // healve the length of the array.
        }
    }

}

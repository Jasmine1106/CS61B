package deque;

public class LinkedListDeque<type> {
    private IntNode sentinel;
    private IntNode last;
    private int size;

    // nested IntNode class
    public class IntNode {
        public IntNode prev;
        public type item;
        public IntNode next;          //Doubly Linked List
        // creat a new IntNode
        public IntNode(IntNode p, type i, IntNode n) {
            prev = p;
            item = i;
            next = n;
        }
    }

    // Creates an empty linked list deque.
    public LinkedListDeque() {
        sentinel =  new IntNode(null, null, null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
    }


    // create a new linkedlist
    public LinkedListDeque(type a) {
        sentinel =  new IntNode(null, null, null);
        sentinel.next = new IntNode(sentinel, a, sentinel);
        size = size + 1;
    }

    // return the number of items in the LinkedListDeque
    public int size() {
        if (size < 0) {
            return 0;
        } return size;
    }

    // Returns true if deque is empty, false otherwise.
    public boolean isEmpty() {
        if (sentinel.next == sentinel.prev) {
            return true;
        } return false;
    }

    // add a new item to the front
    public void addFirst(type a) {
        sentinel.next = new IntNode(sentinel, a, sentinel.next);
        size = size + 1;
    }

    // add a new item to the last
    public void addLast(type a) {
        sentinel.prev = new IntNode(sentinel.prev, a, sentinel);
        size = size + 1;
    }

    /* Remove the front item and returns the front item.
     Otherwise, returns null. **/
    public type removeFirst() {
        if (isEmpty()) {
            return null;
        } type return_value = sentinel.next.item;
        sentinel.next = sentinel.next.next;    // Remove
        size = size - 1;
        return return_value;
    }

    // remove the last item and returns the last item.
    // Otherwise, returns null.
    public type removeLast () {
        if (isEmpty()) {
            return null;
        } type return_value = sentinel.prev.item;
        sentinel.prev = sentinel.prev.prev;    // Remove
        size = size - 1;
        return return_value;
    }

    // Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
    // If no such item exists, returns null. Must not alter the deque!
    public type get(int index) {
        IntNode i = sentinel.next;
        while (index != 0) {
            if (i.next == null) {
                return null;
            }
            i = i.next;
            index = index - 1;
        } return i.item;
    }

    // a helper method for the following recursive method.
    public type getRecursive(IntNode p, int index) {
        if (p.next == null) {
            return null;
        } if (index == 0) {
            return p.next.item;
        }return getRecursive(p.next, index - 1);
    }
    // implement get with recursion
    public type getRecursive(int index) {
        return getRecursive(sentinel, index);
    }

    //Prints the items in the deque from first to last, separated by a space.
    // Once all the items have been printed, print out a new line.
    public void printDeque() {
        for (int i = 0; i < size; i += 1) {
            System.out.print(get(i));
        } System.out.println();
    }


}

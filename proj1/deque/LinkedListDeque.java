package deque;

import java.util.Iterator;

public class LinkedListDeque<type> implements Deque<type>, Iterable<type>{
    private IntNode<type> sentinel = new IntNode<>(null, null, null);;
    private int size;

    // nested IntNode class
    private static class IntNode<type> {
        private IntNode<type> prev;
        private type item;
        private IntNode<type> next;          //Doubly Linked List
        // creat a new IntNode
        IntNode(IntNode<type> p, type i, IntNode<type> n) {
            prev = p;
            item = i;
            next = n;
        }
    }

    // Creates an empty linked list deque.
    public LinkedListDeque() {
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    // create a new linkedlist
    public LinkedListDeque(type a) {
        sentinel.next = new IntNode<>(sentinel, a, sentinel);
        size = 1;
    }

    // return the number of items in the LinkedListDeque
    public int size() {
        if (size < 0) {
            return 0;
        } return size;
    }


    // add a new item to the front
    public void addFirst(type a) {
        sentinel.next = new IntNode<>(sentinel, a, sentinel.next);
        sentinel.next.next.prev = sentinel.next;
        size = size + 1;
    }

    // add a new item to the last
    public void addLast(type a) {
        sentinel.prev = new IntNode<>(sentinel.prev, a, sentinel);
        sentinel.prev.prev.next = sentinel.prev;   // to make old item point to new created item.
        size = size + 1;
    }

    /* Remove the front item and returns the front item.
     Otherwise, returns null. **/
    public type removeFirst() {
        if (isEmpty()) {
            return null;
        }
        type return_value = sentinel.next.item;
        sentinel.next = sentinel.next.next;    // Remove
        sentinel.next.prev = sentinel;
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
        sentinel.prev.next = sentinel;
        size = size - 1;
        return return_value;
    }

    // Gets the item at the given index, where 0 is the front, 1 is the next item, and so forth.
    // If no such item exists, returns null. Must not alter the deque!
    public type get(int index) {
        IntNode<type> i = sentinel.next;
        while (index != 0) {
            if (i.next == null) {
                return null;
            }
            i = i.next;
            index = index - 1;
        } return i.item;
    }

    // a helper method for the following recursive method.
    public type getRecursive(IntNode<type> p, int index) {
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

    /* The Deque objects we’ll make are iterable (i.e. Iterable<T>)
    so we must provide this method to return an iterator.  **/
    public Iterator<type> iterator() {

    }

    /*  Returns whether or not the parameter o is equal to the Deque.
    o is considered equal if it is a Deque and if it contains the same contents
     (as goverened by the generic T’s equals method) in the same order.
    (You’ll need to use the instance of keywords)
     */
    public boolean equals(Object o) {

    }



}
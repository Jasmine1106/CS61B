package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap <K extends Comparable<K>, V> implements Map61B<K, V> {
    private BSTNode root;


    private class BSTNode{
        private K key;
        private V value;
        private BSTNode left, right;
        private int size;
        public BSTNode(K k, V v, int size){
            this.key = k;
            this.value = v;
            this.size = size;
        }

    }

    /* Returns the number of key-value mappings in this map with an overloading helper method **/
    public int size(){
        return size(root);
    }

    private int size(BSTNode N) {
        if (N == null) {
            return 0;
        }
        return N.size;
    }


    /** Removes all  the mappings from this map. */
    public void clear(){
        root = null;
    }


    /* Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Illegal argument");
        }
        return containsKey(root, key);
    }

    private boolean containsKey(BSTNode N, K key) {
        if (N == null) {
            return false;
        }
        int cmp = key.compareTo(N.key);
        if (cmp == 0) {
            return true;
        } else if (cmp < 0) {
            return containsKey(N.left, key);
        } else {
            return containsKey(N.right, key);
        }
    }

    /* Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        return get(root, key);
    }

    private V get(BSTNode N, K key) {
        if (key == null) {
            throw new IllegalArgumentException("calls get( with a null key");
        }
        if (N == null) {
            return null;
        }
        int cmp = key.compareTo(N.key);
        if (cmp < 0) {
            return get(N.left, key);
        } else if (cmp > 0) {
            return get(N.right, key);
        } else {
            return N.value;
        }
    }


    /*Inserts the specified key-value pair into the symbol table, overwriting the old
     * value with the new value if the symbol table already contains the specified key.
            * Deletes the specified key (and its associated value) from this symbol table
     * if the specified value is {@code null}.    **/
     public void put(K key, V value){
         if (key == null) {
             throw new IllegalArgumentException("Illegal argument");
         }
         root = put(root, key, value);
    }

    private BSTNode put(BSTNode N, K key, V value) {
         if (N == null) {
             return new BSTNode(key, value ,1);
         }
         int cmp = key.compareTo(N.key);
         if (cmp < 0) {
             N.left = put(N.left, key, value);
         } else if (cmp > 0) {
             N.right = put(N.right, key, value);
         } else {
             N.value = value;
         }
         N.size = 1 + size(N.left) + size(N.right);
         return N;
    }

    /* print the map in order **/
    public void printInOrder() {

    }

    /* Returns a Set view of the keys contained in this map. Not required for Lab 7.
     * If you don't implement this, throw an UnsupportedOperationException. */
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    /* Removes the mapping for the specified key from this map if present.
     * Not required for Lab 7. If you don't implement this, throw an
     * UnsupportedOperationException. */
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /* Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 7. If you don't implement this,
     * throw an UnsupportedOperationException.*/
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public java.util.Iterator<K> iterator() {
        return null;
    }

}

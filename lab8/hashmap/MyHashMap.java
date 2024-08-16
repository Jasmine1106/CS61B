package hashmap;

import java.security.Key;
import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author Jasmine1106
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    static final int DEFAULT_INITAL_SIZE = 1 << 4;   // aka 16
    static final double DEFAULT_LOAD_FACTOR= 0.75f;
    // You should probably define some more!
    private int num;          // numbers of key-value pairs
    private int ini_size;
    private double load_factor;

    /** Constructors */
    public MyHashMap() {
        this(DEFAULT_INITAL_SIZE);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        if (initialSize <= 0 || maxLoad <= 0) {
            throw new UnsupportedOperationException("invalid arguments");
        }
        this.ini_size = initialSize;
        this.load_factor = maxLoad;
        buckets = createTable(ini_size);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] tables = new Collection[tableSize];
        for (int i = 0; i < tableSize; i += 1) {
            tables[i] = createBucket();
        }
        return tables;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    public int hash(K key) {
        return hash(key, buckets);
    }

    // a helper method for convenience of resize method
    public int hash(K key, Collection<Node>[] table) {
        int h = key.hashCode();
        return Math.floorMod(h, table.length);
    }

    /** Removes all  the mappings from this map. */
    public void clear() {
        buckets = createTable(DEFAULT_INITAL_SIZE);
        num = 0;
    }

    /** Returns true if this map contains a mapping for the specified key. */
    public boolean containsKey(K key) {
        return (get(key) != null);
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     */
    public V get(K key) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Node node = getNode(key);
        if (node == null) {
            return null;
        }
        return node.value;

    }

    // linearly get the node
    private Node getNode(K key) {
        int room = hash(key);
        for (Node node : buckets[room]) {
            if (node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    /** Returns the number of key-value mappings in this map. */
    public int size() {
        return num;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     */
    public void put(K key, V value) {
        Node search_node = getNode(key);
        if (search_node != null) {
            search_node.value = value;
            return;
        }
        int room = hash(key);
        Node put_node = createNode(key, value);
        buckets[room].add(put_node);       //adding the node to the end of the collection data structure
        num += 1;
        if (overloading()) {
            resize(buckets.length * 2);
        }
    }

    private boolean overloading() {
        return num / buckets.length > load_factor;
    }

    private void resize(int capacity) {
        Collection<Node>[] new_buckets = createTable(capacity);
        Iterator<Node> nodeIterator = new MyHashMapNodeIterator();
        while (nodeIterator.hasNext()) {
            Node node = nodeIterator.next();
            int room = hash(node.key, new_buckets);
            new_buckets[room].add(node);
        }
        buckets = new_buckets;
    }

    /** Returns a Set view of the keys contained in this map. */
    public Set<K> keySet(){
        HashSet<K> set= new HashSet<>();
        for (K key : this) {
            set.add(key);
        }
        return set;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     */

    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }

    private class MyHashMapIterator implements Iterator<K>{
        private final Iterator<Node> nodeIterator = new MyHashMapNodeIterator();

        public boolean hasNext() {
            return nodeIterator.hasNext();
        }

        public K next() {
            return nodeIterator.next().key;
        }
    }

    // @author github:exuanbo
    private class MyHashMapNodeIterator implements Iterator<Node> {
        private final Iterator<Collection<Node>> bucketsIterator = Arrays.stream(buckets).iterator();
        private Iterator<Node> currentBucketIterator;
        private int nodesLeft = num;

        public boolean hasNext() {
            return nodesLeft > 0;
        }

        public Node next() {
            if (currentBucketIterator == null || !currentBucketIterator.hasNext()) {
                Collection<Node> currentBucket = bucketsIterator.next();
                while (currentBucket.size() == 0) {
                    currentBucket = bucketsIterator.next();
                }
                currentBucketIterator = currentBucket.iterator();
            }
            nodesLeft -= 1;
            return currentBucketIterator.next();
        }
    }



    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     */
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

}

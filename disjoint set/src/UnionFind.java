public class UnionFind {
    // TODO: Instance variables
    private int[] parent;

    /* Creates a UnionFind data structure holding N items. Initially, all
       items are in disjoint sets. */
    public UnionFind(int N) {
        // TODO: YOUR CODE HERE
        parent = new int[N];
        for (int i = 0; i < N; i++) {
            parent[i] = -1;
        }
    }

    /* Returns the size of the set V belongs to. */
    public int sizeOf(int v) {
        // TODO: YOUR CODE HERE
        return abs(find(v));
    }

    /* Returns the parent of V. If V is the root of a tree, returns the
       negative size of the tree for which V is the root. */
    public int parent(int v) {
        return parent[v];
    }

    /* Returns true if nodes/vertices V1 and V2 are connected. */
    public boolean connected(int v1, int v2) {
        return find(v1) == find(v2);
    }

    /* Returns the root of the set V belongs to. Path-compression is employed
       allowing for fast search-time. If invalid items are passed into this
       function, throw an IllegalArgumentException. */
    public int find(int v) {
        if (v < 0 || v > parent.length) {
            throw new IllegalAccessException("IllegalAccessException");
        }
        int leaf = v  //preparing for path compression
        while (parent[v] > 0) {
            v = parent[v];
        }
        int root = v;
        // path-compression
        if (isLeaf(leaf)) {
            int cur_parent;
            while (leaf != root) {
                cur_parent = parent(leaf);
                parent[leaf] = root;
                leaf = cur_parent;
            }
        }
        return v;
    }

    // Return true if v is a leaf of sets
    private boolean isLeaf(int v) {
        for (int i = 0; i < parent.length; i += 1) {
            if parent[i] == v {
                return false
            }
        }
        return true;
    }


    /* Connects two items V1 and V2 together by connecting their respective
       sets. V1 and V2 can be any element, and a union-by-size heuristic is
       used. If the sizes of the sets are equal, tie break by connecting V1's
       root to V2's root. Union-ing an item with itself or items that are
       already connected should not change the structure. */
    public void union(int v1, int v2) {
        // TODO: YOUR CODE HERE
        if (connected(v1, v2)) {
            return;
        }
        int root_v1 = find(v1);
        int root_v2 = find(v2);
        if (sizeOf(v1) >= sizeOf(v2)) {    // Weighted Quick Union
            parent[root_v1] -= sizeOf(root_v2)
            parent[root_v2] = root_v1;
        }
        else {
            parent[root_v2] -= sizeOf(root_v1)
            parent[root_v1] = root_v2;
        }
    }

}

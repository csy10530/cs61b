import java.util.Arrays;

public class UnionFind {
    private int[] parents;
    private int[] sizes;

    /* Creates a UnionFind data structure holding n vertices. Initially, all
       vertices are in disjoint sets. */
    public UnionFind(int n) {
        parents = new int[n];
        sizes = new int[n];
        Arrays.fill(sizes, 1);
        Arrays.fill(parents, -1);
    }

    /* Throws an exception if v1 is not a valid index. */
    private void validate(int vertex) {
        if (vertex < 0 || vertex >= parents.length)
            throw new IllegalArgumentException("Invalid Index");
    }

    /* Returns the size of the set v1 belongs to. */
    public int sizeOf(int v1) {
        validate(v1);

        return sizes[find(v1)];
    }

    /* Returns the parent of v1. If v1 is the root of a tree, returns the
       negative size of the tree for which v1 is the root. */
    public int parent(int v1) {
        validate(v1);

        return parents[v1];
    }

    /* Returns true if nodes v1 and v2 are connected. */
    public boolean connected(int v1, int v2) {
        validate(v1);
        validate(v2);

        return find(v1) == find(v2);
    }

    /* Connects two elements v1 and v2 together. v1 and v2 can be any valid 
       elements, and a union-by-size heuristic is used. If the sizes of the sets
       are equal, tie break by connecting v1's root to v2's root. Unioning a 
       vertex with itself or vertices that are already connected should not 
       change the sets but may alter the internal structure of the data. */
    public void union(int v1, int v2) {
        validate(v1);
        validate(v2);

        if (connected(v1, v2) || v1 == v2) return;

        int v1Root = find(v1);
        int v2Root = find(v2);

        /**if (sizeOf(v1) <= sizeOf(v2)) {
            parents[v1Root] = v2Root;
            sizes[v2Root] += sizes[v1Root];
        }
        else {
            parents[v2Root] = v1Root;
            sizes[v1Root] += sizes[v2Root];
        }*/

        if (sizeOf(v1) <= sizeOf(v2)) {
            parents[v1Root] = v2Root;
            sizes[v2Root] += sizes[v1Root];
            changeRoot(v1, v2Root);
        }
        else {
            parents[v2Root] = v1Root;
            sizes[v1Root] += sizes[v2Root];
            changeRoot(v2, v1Root);
        }
    }

    /**
     * Method for path compression. Change the given vertex and its parent to the root.
     */
    private void changeRoot(int vertex, int root) {
        int v = vertex;
        while (parents[v] > 0) {
            int i = parent(v);
            parents[v] = root;
            v = i;
        }
    }

    /* Returns the root of the set V belongs to. Path-compression is employed
       allowing for fast search-time. */
    public int find(int vertex) {
        validate(vertex);

        int v = vertex;
        while (parents[vertex] > 0)
            vertex = parents[vertex];

        changeRoot(v, vertex);

        return vertex;
    }
}

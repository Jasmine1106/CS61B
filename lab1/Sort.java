public class Sort {
    /** Sorts strings destructively. */
    public static void sort(String[] x) {
        sort(x, 0);
    }

    //a helper method to sort at a specfic index
    private static void sort(String[] x, int start) {
        if (start == x.length) {
            return;
        }
        int smallestIndex = findsmallest(x, start);
        swap(x, start, smallestIndex);
        sort(x, start + 1);
    }

    // find the smaalest item of a string
    /* @source Got help with string compares from https://goo.gl/a7yBU5. */
    public static int findsmallest (String[] x, int start) {
        int SmallestIndex = start;
        for (int i = start; i < x.length; i += 1) {
            int cmp = x[i].compareTo(x[SmallestIndex]);
            if (cmp < 0) {
                SmallestIndex = i;
            }
        }
        return SmallestIndex;
    }
    // swap the smallest item to the front
    public static void swap(String[] x, int a, int b)  {
        String temp = x[a];
        x[a] = x[b];
        x[b] = temp;

    }
}
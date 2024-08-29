package deque;

import java.util.Comparator;

public class MaxArrayDeque<type> extends ArrayDeque<type> {
    private final Comparator<type> comparator;

    // creates a MaxArrayDeque with the given Comparator.
    public MaxArrayDeque(Comparator<type> c) {
        comparator = c;
    }
    /* returns the maximum element in the deque as governed by the previously given Comparator.
    If the MaxArrayDeque is empty, simply return null. **/
    public type max() {
        return max(comparator);
    }
    /*  returns the maximum element in the deque as governed by the parameter Comparator c.
    If the MaxArrayDeque is empty, simply return null.   **/
    public type max(Comparator<type> c) {
        if (this.isEmpty()) {
            return null;
        }
        int maxIndex = 0;
        for (int i = 0; i < this.size(); i++) {
            if (c.compare(get(i), get(maxIndex)) > 0) {
                maxIndex = i;
            }
        }
        return get(maxIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != this.getClass()) {
            return false;
        }
        if (((MaxArrayDeque<?>) o).max() != max()) {
            return false;
        }
        return super.equals(o);
    }
}

package randomizedtest;

import org.junit.Test;
import edu.princeton.cs.algs4.StdRandom;
import static org.junit.Assert.*;

public class randomizedTest {
    @Test
    public void randomizedTest() {
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                assertEquals(L.size(), B.size());
            } else if (operationNumber == 1) {
                // size
                int size = L.size();
                int size_b = B.size();
            } else if (operationNumber == 2) {
                if (L.size() <= 0 || B.size() <= 0) {
                    continue;
                }
                int b = B.getLast();
                int l = L.getLast();
                assertEquals(b, l);
                assertEquals(B.removeLast(), L.removeLast());
            }
        }
    }
}

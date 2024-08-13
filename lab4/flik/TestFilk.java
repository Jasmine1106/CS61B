package flik;

import org.junit.Test;
import static org.junit.Assert.*;
public class TestFilk {
    @Test
    public void test127(){
        assertTrue("127==127",Flik.isSameNumber(127,127));
    }

    @Test
    public void test128(){
        assertTrue("128==128",Flik.isSameNumber(128,128));

    }


}

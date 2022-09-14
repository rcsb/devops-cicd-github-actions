package org.rcsb;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class DummyTest {
    @Test
    public void fail(){
        Assert.fail("O-o-o-ops!");
    }

    @Test
    public void success(){
        Assert.assertTrue(true);
    }
}
package org.expasy.mzjava.tools.glycoforest.graphdb.mapdb;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class EdgeKeyTest {

    @Test
    public void testCompareTo() throws Exception {

        final EdgeKey key1 = new EdgeKey(733.36548764456f, 0);
        final EdgeKey key2 = new EdgeKey(733.36548764456f, 1);
        final EdgeKey key3 = new EdgeKey(733.445724132437f, 1);
        final EdgeKey key4 = new EdgeKey(895.3548794679236f, 0);

        List<EdgeKey> keys = Lists.newArrayList(
                key3,
                key4,
                key2,
                key1
        );

        //Check that list is not sorted
        Assert.assertNotEquals(key1, keys.get(0));
        Assert.assertNotEquals(key2, keys.get(1));
        Assert.assertNotEquals(key3, keys.get(2));
        Assert.assertNotEquals(key4, keys.get(3));

        Collections.sort(keys, new EdgeKeyComparator());

        Assert.assertEquals(key1, keys.get(0));
        Assert.assertEquals(key2, keys.get(1));
        Assert.assertEquals(key3, keys.get(2));
        Assert.assertEquals(key4, keys.get(3));
    }
}
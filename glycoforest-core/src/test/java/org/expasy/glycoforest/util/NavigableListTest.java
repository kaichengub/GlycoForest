package org.expasy.glycoforest.util;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.stream.Collectors;

/**
 * @author Oliver Horlacher
 * @version sqrt -1
 */
public class NavigableListTest {

    @Test
    public void testNoKeyPresent() throws Exception {

        NavigableList<Integer, String> list = new NavigableList.Builder<Integer, String>(8)
                .add(1, "1a")
                .add(1, "1b")
                .add(3, "3")
                .add(4, "4a")
                .add(4, "4b")
                .add(6, "6")
                .build();

        Assert.assertEquals(Lists.newArrayList("1a", "1b"), list.values(-1, false, 2, false).collect(Collectors.toList()));
        Assert.assertEquals(Lists.newArrayList("1a", "1b"), list.values(-1, true, 2, true).collect(Collectors.toList()));

        Assert.assertEquals(Lists.newArrayList("3", "4a", "4b"), list.values(2, false, 5, false).collect(Collectors.toList()));
        Assert.assertEquals(Lists.newArrayList("3", "4a", "4b"), list.values(2, true, 5, true).collect(Collectors.toList()));

        Assert.assertEquals(Lists.newArrayList("6"), list.values(5, false, 12, false).collect(Collectors.toList()));
        Assert.assertEquals(Lists.newArrayList("6"), list.values(5, true, 12, true).collect(Collectors.toList()));
    }

    @Test
    public void testFromInclusive() throws Exception {

        NavigableList<Integer, String> list = new NavigableList.Builder<Integer, String>(8)
                .add(1, "1a")
                .add(1, "1b")
                .add(3, "3")
                .add(4, "4a")
                .add(4, "4b")
                .add(4, "4c")
                .add(5, "5")
                .build();

        Assert.assertEquals(Lists.newArrayList("1a", "1b", "3"), list.values(1, true, 3, true).collect(Collectors.toList()));
        Assert.assertEquals(Lists.newArrayList("3"), list.values(1, false, 3, true).collect(Collectors.toList()));
        Assert.assertEquals(Lists.newArrayList("4a", "4b", "4c", "5"), list.values(4, true, 5, true).collect(Collectors.toList()));
        Assert.assertEquals(Lists.newArrayList("5"), list.values(4, false, 5, true).collect(Collectors.toList()));
        Assert.assertEquals(Lists.newArrayList("1a", "1b"), list.values(-1, false, 2, false).collect(Collectors.toList()));
        Assert.assertEquals(Lists.newArrayList("1a", "1b"), list.values(-1, true, 2, true).collect(Collectors.toList()));
    }

    @Test
    public void testToInclusive() throws Exception {

        NavigableList<Integer, String> list = new NavigableList.Builder<Integer, String>(8)
                .add(1, "1a")
                .add(1, "1b")
                .add(3, "3")
                .add(4, "4a")
                .add(4, "4b")
                .add(4, "4c")
                .add(5, "5")
                .build();

        Assert.assertEquals(Lists.newArrayList("1a", "1b", "3"), list.values(1, true, 3, true).collect(Collectors.toList()));
        Assert.assertEquals(Lists.newArrayList("1a", "1b", "3"), list.values(1, true, 4, false).collect(Collectors.toList()));
        Assert.assertEquals(Lists.newArrayList("1a", "1b", "3", "4a", "4b", "4c"), list.values(1, true, 4, true).collect(Collectors.toList()));
        Assert.assertEquals(Lists.newArrayList("1a", "1b", "3"), list.values(1, true, 4, false).collect(Collectors.toList()));
    }

    @Test
    public void testToInclusive2() throws Exception {

        NavigableList<Integer, String> list = new NavigableList.Builder<Integer, String>(8)
                .add(1, "1a")
                .add(1, "1b")
                .add(3, "3")
                .add(4, "4a")
                .add(4, "4b")
                .add(4, "4c")
                .add(5, "5a")
                .add(5, "5b")
                .build();

        Assert.assertEquals(Lists.newArrayList("4a", "4b", "4c"), list.values(4, true, 5, false).collect(Collectors.toList()));
        Assert.assertEquals(Lists.newArrayList("4a", "4b", "4c", "5a", "5b"), list.values(4, true, 5, true).collect(Collectors.toList()));
    }
}
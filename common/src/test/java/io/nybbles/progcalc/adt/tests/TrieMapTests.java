package io.nybbles.progcalc.adt.tests;

import io.nybbles.progcalc.adt.TrieMap;
import org.junit.Test;

import static org.junit.Assert.*;

public class TrieMapTests {
    private static class TestData {
        public TestData(String value) {
            this.value = value;
        }
        public String value;
    }

    @Test
    public void basicOperations() {
        var map = new TrieMap<TestData>();
        map.insert("One", new TestData("1"));
        map.insert("Two", new TestData("2"));
        map.insert("Three", new TestData("3"));
        map.insert("Four", new TestData("4"));

        var pairs = map.getPairs();
        assertEquals(4, pairs.size());

        var node = map.find(null, 'T');
        assertNotNull(node);
        var node2 = map.find(node, 'w');
        assertNotNull(node2);
        var node3 = map.find(node2, 'r');
        assertNull(node3);
    }
}

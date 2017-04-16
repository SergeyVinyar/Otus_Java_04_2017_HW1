package ru.vinyarsky.hw3;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class MyHashMapTest {

    @Test
    void size() {
        MyHashMap<Integer, String> map = new MyHashMap<>();

        for (int i = 0; i < 10; i++) {
            map.put(i, "Value" + Integer.toString(i));
        }

        assertEquals(10, map.size());

        for (int i = 200; i < 300; i++) {
            map.put(i, "Value" + Integer.toString(i));
        }

        assertEquals(110, map.size());

        for (int i = 220; i < 240; i++) {
            map.remove(i);
        }

        assertEquals(90, map.size());
    }

    @Test
    void isEmpty() {
        MyHashMap<Integer, String> map = new MyHashMap<>();

        assertTrue(map.isEmpty());

        map.put(1, "Value1");

        assertFalse(map.isEmpty());

        map.remove(1);

        assertTrue(map.isEmpty());
    }

    @Test
    void containsKey() {
        MyHashMap<Integer, String> map = new MyHashMap<>();

        map.put(1, "Value1");
        map.put(2, "Value2");

        assertTrue(map.containsKey(1));
        assertTrue(map.containsKey(2));
        assertFalse(map.containsKey(3));

        map.remove(2);

        assertTrue(map.containsKey(1));
        assertFalse(map.containsKey(2));
        assertFalse(map.containsKey(3));
    }

    @Test
    void containsValue() {
        MyHashMap<Integer, String> map = new MyHashMap<>();

        map.put(1, "Value1");
        map.put(2, "Value2");

        assertTrue(map.containsValue("Value1"));
        assertTrue(map.containsValue("Value2"));
        assertFalse(map.containsValue("Value3"));

        map.remove(2);

        assertTrue(map.containsValue("Value1"));
        assertFalse(map.containsValue("Value2"));
        assertFalse(map.containsValue("Value3"));
    }

    @Test
    void get() {
        MyHashMap<Integer, String> map = new MyHashMap<>();

        map.put(1, "Value1");

        assertEquals("Value1", map.get(1));
    }

    @Test
    void putAll() {
        MyHashMap<Integer, String> map1 = new MyHashMap<>();
        map1.put(1, "Value1");
        map1.put(2, "Value2");
        map1.put(3, "Value3");

        MyHashMap<Integer, String> map2 = new MyHashMap<>(map1);

        assertEquals(3, map2.size());
        assertTrue(map2.containsKey(1));
        assertTrue(map2.containsKey(2));
        assertTrue(map2.containsKey(3));

        map1.remove(2);

        assertEquals(3, map2.size());
        assertTrue(map2.containsKey(1));
        assertTrue(map2.containsKey(2));
        assertTrue(map2.containsKey(3));
    }

    @Test
    void clear() {
        MyHashMap<Integer, String> map = new MyHashMap<>();
        map.put(1, "Value1");
        map.put(2, "Value2");
        map.put(3, "Value3");

        assertEquals(3, map.size());
        assertFalse(map.isEmpty());

        map.clear();

        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
    }

    @Test
    void keySet() {
        MyHashMap<Integer, String> map = new MyHashMap<>();
        map.put(1, "Value1");
        map.put(3, "Value3");

        Set<Integer> set = map.keySet();

        assertEquals(2, set.size());
        assertTrue(set.contains(1));
        assertFalse(set.contains(2));
        assertTrue(set.contains(3));
    }

    @Test
    void values() {
        MyHashMap<Integer, String> map = new MyHashMap<>();
        map.put(1, "Value1");
        map.put(3, "Value3");

        Collection<String> values = map.values();

        assertEquals(2, values.size());
        assertTrue(values.contains("Value1"));
        assertFalse(values.contains("Value2"));
        assertTrue(values.contains("Value3"));
    }

    @Test
    void entrySet() {
        MyHashMap<Integer, String> map = new MyHashMap<>();
        map.put(1, "Value1");
        map.put(3, "Value3");

        Set<Map.Entry<Integer, String>> set = map.entrySet();
        Iterator<Map.Entry<Integer, String>> iterator = set.iterator();

        assertTrue(iterator.hasNext());
        Map.Entry<Integer, String> entry1 = iterator.next();
        assertTrue(entry1 != null);
        assertEquals(new Integer(1), entry1.getKey());
        assertEquals("Value1", entry1.getValue());

        assertTrue(iterator.hasNext());
        Map.Entry<Integer, String> entry2 = iterator.next();
        assertTrue(entry2 != null);
        assertEquals(new Integer(3), entry2.getKey());
        assertEquals("Value3", entry2.getValue());

        assertFalse(iterator.hasNext());
    }

}
package ru.vinyarsky.hw8;

/*

В качестве валидатора использовал библиотечку
https://github.com/skyscreamer/JSONassert

Это необходимо для того, чтобы избежать ложных ошибок
из-за несовпадающего порядка полей в результирующем json'е.

 */

import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.*;

public class JsonWriterTest {

    private Gson gson;

    @Before
    public void before() {
        this.gson = new Gson();
    }

    @Test
    public void arrayOfPrimitives() throws Exception {
        {
            int[] data = new int[]{456, 567, 678};
            String actualString = JsonWriter.AsJson(data);
            String expectedString = gson.toJson(data);
            JSONAssert.assertEquals(expectedString, actualString, false);
        }

        {
            double[] data = new double[]{456.5, 567.3, 678.22};
            String actualString = JsonWriter.AsJson(data);
            String expectedString = gson.toJson(data);
            JSONAssert.assertEquals(expectedString, actualString, false);
        }

        {
            boolean[] data = new boolean[]{false, true, true};
            String actualString = JsonWriter.AsJson(data);
            String expectedString = gson.toJson(data);
            JSONAssert.assertEquals(expectedString, actualString, false);
        }
    }

    @Test
    public void arrayOfObjectTypes() throws Exception {
        {
            Object[] data = new Object[]{null, 223, "667", true, 0.3};
            String actualString = JsonWriter.AsJson(data);
            String expectedString = gson.toJson(data);
            JSONAssert.assertEquals(expectedString, actualString, false);
        }

        {
            Boolean[] data = new Boolean[]{null, true, false};
            String actualString = JsonWriter.AsJson(data);
            String expectedString = gson.toJson(data);
            JSONAssert.assertEquals(expectedString, actualString, false);
        }

        {
            String[] data = new String[]{"First", null, "Third"};
            String actualString = JsonWriter.AsJson(data);
            String expectedString = gson.toJson(data);
            JSONAssert.assertEquals(expectedString, actualString, false);
        }
    }

    @Test
    public void collections() throws Exception {

        List<String> list = new ArrayList<>();
        list.add("One");
        list.add("Two");
        list.add("Three");

        Set<String> set = new HashSet<String>();
        list.add("One");
        list.add("Two");
        list.add("Three");

        {
            String actualString = JsonWriter.AsJson(list);
            String expectedString = gson.toJson(list);
            JSONAssert.assertEquals(expectedString, actualString, false);
        }

        {
            String actualString = JsonWriter.AsJson(set);
            String expectedString = gson.toJson(set);
            JSONAssert.assertEquals(expectedString, actualString, false);
        }
    }

    @Test
    public void map() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        map.put("One", 1);
        map.put("Two", 2);
        map.put("Three", 3);

        String actualString = JsonWriter.AsJson(map);
        String expectedString = gson.toJson(map);
        JSONAssert.assertEquals(expectedString, actualString, false);
    }

    @Test
    public void complexStructure() throws Exception {
        TestClass instance1 = new TestClass("instance1", 666, "Hello world", null, new Object[] {1, true, "Element", null});
        TestClass instance2 = new TestClass("instance2", 777, "Hello another world", null, new Object[] {1, true, "Another element", null});

        {
            TestClass[] data = new TestClass[]{instance1, instance2};
            String actualString = JsonWriter.AsJson(data);
            String expectedString = gson.toJson(data);
            JSONAssert.assertEquals(expectedString, actualString, false);
        }

        instance1.refValue = instance2;

        {
            String actualString = JsonWriter.AsJson(instance1);
            String expectedString = gson.toJson(instance1);
            JSONAssert.assertEquals(expectedString, actualString, false);
        }
    }

    @Test(expected = Exception.class)
    public void ringReferences() throws Exception {
        TestClass instance1 = new TestClass("instance1", 666, "Hello world", null, new Object[] {1, true, "Element", null});
        TestClass instance2 = new TestClass("instance2", 777, "Hello another world", null, new Object[] {1, true, "Another element", null});

        instance1.refValue = instance2;
        instance2.refValue = instance1;

        JsonWriter.AsJson(instance1);
    }

    static class TestClass {
        public final String tag;

        public final int intValue;
        public final String strValue;
        public Object refValue;
        public final Object[] refArray;

        TestClass(String tag, int intValue, String strValue, Object refValue, Object[] refArray) {
            this.tag = tag;

            this.intValue = intValue;
            this.strValue = strValue;
            this.refValue = refValue;
            this.refArray = refArray;
        }
    }
}
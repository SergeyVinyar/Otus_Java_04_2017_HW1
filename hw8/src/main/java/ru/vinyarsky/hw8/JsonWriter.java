package ru.vinyarsky.hw8;

/*

В целом работает, тесты проходят.

Есть ограничения:

1) Не поддерживаются аннотации gson'а
2) Кольцевые ссылки приводят к исключению
3) Не выгружаются не public поля
4) gson поддерживает выгрузку одного поля, например, gson.toJson(true) выдаст "true".
   Я этого не стал поддерживать, поскольку simple json "из коробки" этого не поддерживает.
   В принципе нет ничего особо сложного: надо сделать класс-обертку над переданным значением, имплементирующий
   интерфейс JSONAware, и возвращать его инстанс в строке 52.

*/

import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import java.util.*;

public class JsonWriter {

    private static Set<Object> processedObjects;

    public static String AsJson(Object object) throws Exception {
        if (processedObjects == null)
            processedObjects = new HashSet<>();
        else
            processedObjects.clear();
        return processObject(object).toJSONString();
    }

    private static JSONAware processObject(Object input) throws Exception {
        if (input == null)
            return new JSONObject();

        if (processedObjects.contains(input))
            throw new Exception("Кольцевые ссылки не поддерживаются");
        if (!isSimpleObject(input))
            processedObjects.add(input);

        JSONAware result;
        Class clazz = input.getClass();
        if (isSimpleObject(input)) {
            JSONObject json = new JSONObject();
            json.put("value", input);
            result = json;
        }
        else if (clazz.isArray()) {
            JSONArray json = new JSONArray();
            for (int i = 0; i < Array.getLength(input); i++) {
                Object value = Array.get(input, i);
                if (isSimpleObject(value))
                    json.add(value);
                else
                    json.add(processObject(value));
            }
            result = json;
        }
        else {
            Set<Class> inputInterfaces = new HashSet<>(Arrays.asList(clazz.getInterfaces()));
            if (inputInterfaces.contains(List.class) || inputInterfaces.contains(Set.class)) {
                JSONArray json = new JSONArray();
                Collection collection = (Collection) input;
                for (Object value : collection) {
                    if (isSimpleObject(value))
                        json.add(value);
                    else
                        json.add(processObject(value));
                }
                result = json;
            } else if (inputInterfaces.contains(Map.class)) {
                JSONObject json = new JSONObject();
                for (Object entry : ((Map) input).entrySet()) {
                    Object key = ((Map.Entry) entry).getKey();
                    Object value = ((Map.Entry) entry).getValue();
                    if (isSimpleObject(value))
                        json.put(key, value);
                    else
                        json.put(key, processObject(value));
                }
                result = json;
            } else {
                JSONObject json = new JSONObject();
                for(Field field : clazz.getFields()) {
                    Object value = field.get(input);
                    if (isSimpleObject(value))
                        json.put(field.getName(), value);
                    else
                        json.put(field.getName(), processObject(value));
                }
                result = json;
            }
        }
        return result;
    }

    private static boolean isSimpleObject(Object input) {
        if (input == null)
            return true;

        Class clazz = input.getClass();
        return     clazz.equals(Long.class)      || clazz.equals(long.class)
                || clazz.equals(Integer.class)   || clazz.equals(int.class)
                || clazz.equals(Short.class)     || clazz.equals(short.class)
                || clazz.equals(Byte.class)      || clazz.equals(byte.class)
                || clazz.equals(Float.class)     || clazz.equals(float.class)
                || clazz.equals(Double.class)    || clazz.equals(double.class)
                || clazz.equals(Character.class) || clazz.equals(char.class)
                || clazz.equals(Boolean.class)   || clazz.equals(boolean.class)
                || clazz.equals(String.class)
                || clazz.equals(Object.class);
    }
}

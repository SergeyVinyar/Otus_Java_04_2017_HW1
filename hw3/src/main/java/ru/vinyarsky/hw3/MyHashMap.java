package ru.vinyarsky.hw3;

import java.util.*;

/**
 * Null-ключи не поддерживаются
 */
public class MyHashMap<K, V> implements Map<K, V> {

    /**
     * Load factor по умолчанию
     */
    private static final float INITIAL_LOADFACTOR = 0.75f;

    /**
     * Capacity по умолчанию
     */
    private static final int INITIAL_CAPACITY = 16;

    /**
     * Количество элементов по умолчанию, добавляемых/удаляемых при перехешировании
     */
    private static final int CAPACITY_INCREMENT = 16;

    private MyEntry<K, V>[] buckets;
    private Float loadFactor = INITIAL_LOADFACTOR;

    private int aSize = 0;

    @SuppressWarnings("unchecked")
    public MyHashMap(int initialCapacity) {
        // Поскольку в run-time нет информации о типах, компилятор не может взять на себя проверку корректности работы
        // с элементами generic-массива. Следовательно, он и не позволяет создать типизированный экземпляр массива.
        // Поэтому создаем нетипизированный массив и кастим его к нужному типу.
        this.buckets = (MyEntry<K, V>[]) new MyEntry[initialCapacity];
    }

    public MyHashMap() {
        this(INITIAL_CAPACITY);
    }

    public MyHashMap(MyHashMap<? extends K, ? extends V> m) {
        this(m.size() + (int) (m.size() * (1 - INITIAL_LOADFACTOR)));
        putAll(m);
    }


    public int size() {
        return this.aSize;
    }

    public boolean isEmpty() {
        return this.aSize == 0;
    }

    public boolean containsKey(Object key) {
        // В метод передается тип Object, а не K, т.к. по спецификации ключ найден, если equals вернул true,
        // а это в общем случае может и не означать, что тип объектов совпадает.
        // Аналогичная ситуация в методах containsValue, get, remove.
        return getEntry(key) != null;
    }

    public boolean containsValue(Object value) {
        for (MyEntry<K, V> entry : this.buckets) {
            while (entry != null) {
                if (entry.getValue().equals(value))
                    return true;
                entry = entry.next;
            }
        }
        return false;
    }

    public V get(Object key) {
        MyEntry<K, V> entry = getEntry(key);
        return entry != null ? entry.getValue() : null;
    }

    public V put(K key, V value) {
        MyEntry<K, V> oldEntry = getEntry(key);
        if (oldEntry != null) {
            V oldValue = oldEntry.getValue();
            oldEntry.setValue(value);
            return oldValue;
        }

        rehashIfRequired(1);

        int bucketIndex = key.hashCode() / this.buckets.length;

        MyEntry<K, V> entry = this.buckets[bucketIndex];
        while(entry != null && entry.next != null)
            entry = entry.next;

        MyEntry<K, V> newEntry = new MyEntry<>(key, value);
        if (entry != null)
            entry.next = newEntry;
        else
            this.buckets[bucketIndex] = newEntry;

        this.aSize++;

        return null;
    }

    public V remove(Object key) {
        MyEntry<K, V> prevEntry = null;

        int bucketIndex = key.hashCode() / this.buckets.length;

        MyEntry<K, V> entry = this.buckets[bucketIndex];
        while (entry != null) {
            if (entry.getKey().equals(key))
                break;
            prevEntry = entry;
            entry = entry.next;
        }

        // Нет записи с таким ключом
        if (entry == null)
            return null;

        // Запись найдена
        if (prevEntry != null) {
            // и она не первая в списке записей в бакете
            prevEntry.next = entry.next;
        }
        else {
            // и она первая в списке записей в бакете
            this.buckets[bucketIndex] = entry.next;
        }

        this.aSize--;

        rehashIfRequired(0);

        return entry.getValue();
    }

    @SuppressWarnings("unchecked")
    public void putAll(Map<? extends K, ? extends V> m) {
        rehashIfRequired(m.size());
        for(Entry entry: m.entrySet())
            put((K) entry.getKey(), (V) entry.getValue());
    }

    public void clear() {
        Arrays.fill(this.buckets, null);
        this.aSize = 0;
    }

    public Set<K> keySet() {
        Set<K> set = new HashSet<K>(this.size());
        for (MyEntry<K, V> entry : this.buckets) {
            while (entry != null) {
                set.add(entry.getKey());
                entry = entry.next;
            }
        }
        return set;
    }

    public Collection<V> values() {
        List<V> list = new ArrayList<V>(this.size());
        for (MyEntry<K, V> entry : this.buckets) {
            while (entry != null) {
                list.add(entry.getValue());
                entry = entry.next;
            }
        }
        return list;
    }

    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> set = new HashSet<>(this.size());
        for (MyEntry<K, V> entry : this.buckets) {
            while (entry != null) {
                set.add(entry);
                entry = entry.next;
            }
        }
        return set;
    }

    /**
     * Поиск вхождения по ключу
     * @return null, если не найдено
     */
    private MyEntry<K, V> getEntry(Object key) {
        for (MyEntry<K, V> entry : this.buckets) {
            while (entry != null) {
                if (entry.getKey().equals(key))
                    return entry;
                entry = entry.next;
            }
        }
        return null;
    }

    /**
     * Перехеширование, если требуется
     * @param entriesExpectedCount Количество записей, которые предполагается добавить (+) или удалить (-)
     */
    @SuppressWarnings("unchecked")
    private void rehashIfRequired(int entriesExpectedCount) {
        int requiredLength = size() + entriesExpectedCount;
        // + запас
        requiredLength += requiredLength * (1 - this.loadFactor);

        int blocksRequired = (requiredLength - this.buckets.length) / CAPACITY_INCREMENT;

        // При blocksRequired < 0 можно оставить незадействованные бакеты,
        // а при blocksRequired > 0 хочется, чтобы всем бакетов хватило.
        if (blocksRequired > 0)
            blocksRequired++;

        if (blocksRequired == 0)
            return;

        Set<Entry<K, V>> set = entrySet();

        int newBucketsLength = size() + 16 * blocksRequired;
        this.buckets = (MyEntry<K, V>[]) new MyEntry[newBucketsLength];

        // К сожалению, даже после перехеширования никто не гарантирует, что не будут образовываться списки,
        // поскольку могут быть коллизии хешей. Поэтому сделаем через добавление каждой записи заново.
        // Хотя, теоретически, здесь можно сэкономить на пересоздании экземпляров MyEntry.
        this.aSize = 0;
        for(Entry<K, V> entry: set)
            put(entry.getKey(), entry.getValue());
    }

    public class MyEntry<K, V> implements Entry<K, V> {

        private K key;
        private V value;

        private MyEntry<K, V> next;

        private MyEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public V setValue(V value) {
            V oldValue = this.value;
            this.value = value;
            return oldValue;
        }
    }
}

package ru.vinyarsky.hw2;

/*
 * Общий принцип работы:
 *
 * Создаем подряд два объекта. Они с высокой вероятностью ложатся в памяти друг за другом.
 * После этого нам достаточно через unsafe вычислить адрес одного объекта и второго,
 * и посчитать между ними разницу. Соответственно результат получается с учетом выравнивания (8 байт).
 *
 * Разумеется данный код категорически нельзя использовать в боевых условиях.
 *
 * Программа скорее всего не будет работать на 32-разрядной VM.
 * Но без проблем отработает на 64-разрядной со сжатием ссылок и без.
 *
 * Задачу также можно было решить через Instrumentation, но я отсек этот вариант, т.к. это
 * просто и неинтересно :)
 *
 * Запустить можно:
 * 1) из Идеи
 * 2) со сжатием ссылок
 *    java -jar hw2-1.0.0.jar
 * 3) без сжатия ссылок
 *    java -XX:-UseCompressedOops -jar hw2-1.0.0.jar
 */

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class Main {

    static AddressGetter getter;

    public static void main(String[] args) throws Exception {
        Field unsafeField  = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe)unsafeField.get(null);

        getter = new AddressGetter(unsafe);

        // new Object(): 16
        System.out.print("new Object(): ");
        System.out.println(sizeOf(new Object()));

        // new String(): 40
        System.out.print("new String(): ");
        System.out.println(sizeOf(new String()));

        // Строку из пула посчитать таким образом нельзя, т.к. она в памяти не будет рядом с boundObject
        //System.out.print("new String().intern(): ");
        //System.out.println(sizeOf(new String().intern()));

        // new Integer(5000): 16
        System.out.print("new Integer(5000): ");
        System.out.println(sizeOf(new Integer(5000)));

        // new Boolean(true): 16
        System.out.print("new Boolean(true): ");
        System.out.println(sizeOf(new Boolean(true)));

        // new Object[]: 16
        System.out.print("new Object[]: ");
        System.out.println(sizeOf(new Object[0]));

        // new Object[1]: 24
        System.out.print("new Object[1]: ");
        System.out.println(sizeOf(new Object[1]));

        // new Object[2]: 24
        System.out.print("new Object[2]: ");
        System.out.println(sizeOf(new Object[2]));

        // new Object[3]: 32
        System.out.print("new Object[3]: ");
        System.out.println(sizeOf(new Object[3]));

        // { new Object() }: 40
        System.out.print("{ new Object() }: ");
        Object[] objectInArray = { new Object() };
        System.out.println(sizeOf(objectInArray));

        // { new Object(), new Object() }: 56
        System.out.print("{ new Object(), new Object() }: ");
        Object[] twoObjectsInArray = { new Object(), new Object() };
        System.out.println(sizeOf(twoObjectsInArray));

        // new EmptyClass(): 16
        System.out.print("new EmptyClass(): ");
        System.out.println(sizeOf(new EmptyClass()));

        // new ClassWithStrings(): 168
        System.out.print("new ClassWithStrings(): ");
        System.out.println(sizeOf(new ClassWithStrings()));
    }

    /**
     * Пустой класс для подсчета его размера
     */
    static class EmptyClass {
    }

    /**
     * Непустой класс для подсчета его размера
     */
    static class ClassWithStrings {
        String string1 = new String("ABCD");
        String string2 = new String("EF");
    }

    /**
     * Возвращает размер object в памяти как разность адресов
     */
    static long sizeOf(Object object) {
        Object boundObject = new Object();

        long objectAddress = getter.getAddressOfObject(object);
        long boundObjectAddress = getter.getAddressOfObject(boundObject);

        return boundObjectAddress - objectAddress;
    }

    /**
     * Вспомогательный класс для вычисления адреса объекта
     */
    static class AddressGetter {

        private Object obj;
        private long objFieldOffset;
        private Unsafe unsafe;

        AddressGetter(Unsafe unsafe) throws Exception {
            this.unsafe = unsafe;
            this.objFieldOffset = unsafe.objectFieldOffset(this.getClass().getDeclaredField("obj"));
        }

        long getAddressOfObject(Object object) {
            this.obj = object;
            long address = unsafe.getLong(this, this.objFieldOffset);

            // Для улучшения производительности при наличии не более 32 Гб RAM
            // VM в качестве ссылок на объекты использует 32-разрядное число.
            // Оно получается из 64-разрядного адреса сдвигом вправо на 3 бита.
            // Поскольку VM знает, что объекты всегда выравниваются по 8 байтам,
            // нам не нужно никогда ссылаться на адреса не кратные 8.
            // Если бы не выравнивание, 32 бита позволяли бы адресовать только 4 Гб,
            // а так получается 32 Гб.
            // В данном случае нам надо восстановить реальный адрес из "ссылки" на объект.
            if (unsafe.arrayIndexScale(Object[].class) == 4)
                address = address << 3;
            return address;
        }
    }
}

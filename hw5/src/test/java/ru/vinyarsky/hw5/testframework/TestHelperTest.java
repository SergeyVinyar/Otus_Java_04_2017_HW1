package ru.vinyarsky.hw5.testframework;

import org.junit.platform.commons.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class TestHelperTest {

    private ByteArrayOutputStream output;

    @org.junit.jupiter.api.BeforeEach
    void beforeEach() {
        // Перенаправляем стандартный вывод, чтобы проанализировать его содержимое
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    /**
     * Проверка запуска тестов через class-объект
     */
    @org.junit.jupiter.api.Test
    void runTestsWithClassObjectAsParameter() throws Exception {
        TestHelper.runTests(TestClass.class);
        String outputLog = output.toString();

        int beforeMethod1CallCount = substringContainsCount("beforeMethod1 called", outputLog);
        assertEquals(2, beforeMethod1CallCount, "Неверное количество вызовов метода beforeMethod1");

        int beforeMethod2CallCount = substringContainsCount("beforeMethod2 called", outputLog);
        assertEquals(2, beforeMethod2CallCount, "Неверное количество вызовов метода beforeMethod2");


        int afterMethod1CallCount = substringContainsCount("afterMethod1 called", outputLog);
        assertEquals(2, afterMethod1CallCount, "Неверное количество вызовов метода afterMethod1");

        int afterMethod2CallCount = substringContainsCount("afterMethod2 called", outputLog);
        assertEquals(2, afterMethod2CallCount, "Неверное количество вызовов метода afterMethod2");

        assertTrue(outputLog.contains("okMethod called"), "Нет вызова метода okMethod");
        assertTrue(outputLog.contains("failMethod called"), "Нет вызова метода failMethod");

        assertTrue(outputLog.contains("okMethod - OK"), "Нет записи о прохождении теста методом okMethod");
        assertTrue(outputLog.contains("failMethod - FAILED"), "Нет записи о НЕпрохождении теста методом failMethod");
    }

    /**
     * Проверка запуска тестов через наименование пакета
     */
    @org.junit.jupiter.api.Test
    void runTestsWithPackageAsParameter() throws Exception {
        TestHelper.runTests("ru.vinyarsky.hw5.testpackage1");
        String outputLog = output.toString();

        // Должны вызваться тесты классов в пакете ru.vinyarsky.hw5.testpackage1...
        assertTrue(outputLog.contains("TestClass1::testMethod called"), "Нет записи о вызове TestClass1::testMethod");
        assertTrue(outputLog.contains("TestClass2::testMethod called"), "Нет записи о вызове TestClass2::testMethod");

        // ...но не вызваться тесты в пакете ru.vinyarsky.hw5.testpackage2
        assertFalse(outputLog.contains("TestClass3::testMethod called"), "Ошибочный вызов TestClass3::testMethod");
    }

    /**
     * Возвращает количество вхождений подстроки substring в строку string
     */
    private int substringContainsCount(String substring, String string) {
        Matcher matcher = Pattern.compile(substring).matcher(string);
        int count = 0;
        while (matcher.find())
            count++;
        return count;
    }
}

/**
 * Набор тестов для проверки фреймворка
 */
class TestClass {

    @Before
    public static void beforeMethod1() {
        System.out.println("beforeMethod1 called");
    }

    @Before
    public static void beforeMethod2() {
        System.out.println("beforeMethod2 called");
    }


    @Test
    public static void okMethod() {
        System.out.println("okMethod called");
    }

    @Test
    public static void failMethod() {
        System.out.println("failMethod called");
        Assert.isTrue(false);
    }

    @After
    public static void afterMethod1() {
        System.out.println("afterMethod1 called");
    }

    @After
    public static void afterMethod2() {
        System.out.println("afterMethod2 called");
    }
}
package ru.vinyarsky.hw5.testframework;

/**
 * Assert-методы для использования внутри тестов
 */
public class Assert {

    private Assert() {
    }

    /**
     * Проверка, что объект == null
     */
    public static void isNull(Object object) {
        if (object != null)
            throw new AssertException("Object is not null");
    }

    /**
     * Проверка, что объект != null
     */
    public static void isNotNull(Object object) {
        if (object == null)
            throw new AssertException("Object is null");
    }

    /**
     * Безусловный fail теста
     */
    public static void fail() {
        throw new AssertException("Test failed");
    }

    /**
     * Проверка, что значение истинно
     */
    public static void isTrue(Boolean value) {
        if (!Boolean.TRUE.equals(value))
            throw new AssertException("Value is not true");
    }

    /**
     * Проверка, что значение ложно
     */
    public static void isFalse(Boolean value) {
        if (!Boolean.FALSE.equals(value))
            throw new AssertException("Value is not false");
    }
}

class AssertException extends RuntimeException {

    AssertException(String message) {
        super(message);
    }
}

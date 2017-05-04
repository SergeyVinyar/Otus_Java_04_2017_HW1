package ru.vinyarsky.hw5.testframework;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты методов Assert
 */
class AssertTest {

    @org.junit.jupiter.api.Test
    void isNull_OK() {
        Assert.isNull(null);
    }

    @org.junit.jupiter.api.Test
    void isNull_Fail() {
        Throwable error = assertThrows(Error.class, () -> Assert.isNull(new Object()));
        assertEquals("Object is not null", error.getMessage());
    }


    @org.junit.jupiter.api.Test
    void isNotNull_OK() {
        Assert.isNotNull(new Object());
    }

    @org.junit.jupiter.api.Test
    void isNotNull_Fail() {
        Throwable error = assertThrows(Error.class, () -> Assert.isNotNull(null));
        assertEquals("Object is null", error.getMessage());
    }

    @org.junit.jupiter.api.Test
    void fail() {
        Throwable error = assertThrows(Error.class, () -> Assert.fail());
        assertEquals("Test failed", error.getMessage());
    }

    @org.junit.jupiter.api.Test
    void isTrue_OK() {
        Assert.isTrue(true);
    }

    @org.junit.jupiter.api.Test
    void isTrue_Fail() {
        Throwable error = assertThrows(Error.class, () -> Assert.isTrue(false));
        assertEquals("Value is not true", error.getMessage());
    }

    @org.junit.jupiter.api.Test
    void isFalse_OK() {
        Assert.isFalse(false);
    }

    @org.junit.jupiter.api.Test
    void isFalse_Fail() {
        Throwable error = assertThrows(Error.class, () -> Assert.isFalse(true));
        assertEquals("Value is not false", error.getMessage());
    }
}
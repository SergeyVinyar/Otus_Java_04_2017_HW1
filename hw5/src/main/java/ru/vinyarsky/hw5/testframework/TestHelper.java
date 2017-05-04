package ru.vinyarsky.hw5.testframework;

import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * Хелпер тестового фреймворка
 */
public class TestHelper {

    private TestHelper() {
    }

    /**
     * Запуск тестов в заданных классах
     */
    public static void runTests(Class<?>... classes) throws Exception {
        System.out.println("Started");
        for (Class<?> clazz: classes) {
            Set<Method> beforeMethods = new HashSet<>();
            Set<Method> testMethods = new HashSet<>();
            Set<Method> afterMethods = new HashSet<>();

            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getDeclaredAnnotation(Before.class) != null)
                    beforeMethods.add(method);
                if (method.getDeclaredAnnotation(Test.class) != null)
                    testMethods.add(method);
                if (method.getDeclaredAnnotation(After.class) != null)
                    afterMethods.add(method);
            }

            Object object = clazz.newInstance();
            for (Method testMethod : testMethods) {
                System.out.println("----------------------------------------------");
                for (Method beforeMethod : beforeMethods)
                    beforeMethod.invoke(object);

                try {
                    testMethod.invoke(object);
                    System.out.println(String.format("%s::%s - OK", clazz.getCanonicalName(), testMethod.getName()));
                }
                catch (InvocationTargetException e) {
                    System.out.println(String.format("%s::%s - FAILED", clazz.getCanonicalName(), testMethod.getName()));
                    Throwable cause = e.getCause();
                    if (cause != null)
                        cause.printStackTrace(System.out);
                }

                for (Method afterMethod : afterMethods)
                    afterMethod.invoke(object);
            }
        }
        System.out.println("----------------------------------------------");
        System.out.println("Completed");
    }

    /**
     * Запуск всех тестов в заданном пакете
     */
    public static void runTests(String packageName) throws Exception {
        // Для получения классов из пакета будем использовать библиотечку Reflections
        // https://github.com/ronmamo/reflections
        Reflections reflections = new Reflections(packageName, new MethodAnnotationsScanner());
        Class<?>[] classesWithTests = reflections.getMethodsAnnotatedWith(Test.class)
                .stream()
                .map(Method::getDeclaringClass)
                .distinct()
                .toArray(Class[]::new);
        runTests(classesWithTests);
    }
}

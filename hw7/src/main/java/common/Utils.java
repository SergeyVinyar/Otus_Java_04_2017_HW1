package common;

import java.util.concurrent.Callable;

public final class Utils {

    private Utils() {
    }

    /**
     * Ввиду того, что лямбды выполняются как обычные методы, мы не можем просто вставить лямбду в оператор stream'а,
     * мы вынуждены оборачивать checked exceptions в unchecked.
     */
    public static <T> T toRunTimeException(Callable<T> func) {
        try {
            return func.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package ru.vinyarsky.hw4;

/*

Результат запуска на 64Мб памяти. В целом, наверное, получилось логично:
1) SerialGC - параллелизации нет, продолжительность работы GC минимальна, т.к. жрет все доступные
   в рамках одного ядра ресурсы
2) ParallelGC - есть параллелизация, продолжительность работы GC увеличилась, т.к. в этой графе мы
   суммируем продолжительность по всем ядрам. Почему продолжительность в реальном времени в два раза больше, непонятно.
   Вероятно, накладные расходы на распараллеливание.
3) ParNewGC + ConcMarkSweepGC - продолжительность работы GC огромна, т.к. теперь сборка происходит на ограниченных ресурсах,
   но зато без stop-the-world.
   Количество сборок мусора увеличилось, т.к. теперь мусор собирается заранее, а не по факту окончания памяти.
4) G1GC - оказался неимоверно живуч. На 64Мб памяти мне не удалось дождаться OutOfMemory (минут 25 ждал, т.е. в 5 раз дольше
   чем остальные GC прожили). Поэтому данные приведены на 24 Мб.
   Если смотреть на график памяти в jvisualvm видно, что ступеньки становятся все мельче. И чувствуется, что со временем
   деградирует производительность. Похоже G1 не даст помереть приложению до последнего, но ценой уменьшения производительности.
   Так что еще большой вопрос, хорошо ли это для сервера.


-== SerialGC ==-
-== Результаты ==-
Всего сборок молодых поколений: 5
Всего полных сборок: 1
Продолжительность работы до OutOfMemory (сек): 294,473
Продолжительность работы GC в пользовательском режиме (сек): 0,560
Продолжительность работы GC в режиме ядра (сек): 0,030
Продолжительность работы GC в реальном времени (сек): 0,620
Параллелизация: что-то не видно

-== ParallelGC ==-
-== Результаты ==-
Всего сборок молодых поколений: 3
Всего полных сборок: 4
Продолжительность работы до OutOfMemory (сек): 311,623
Продолжительность работы GC в пользовательском режиме (сек): 3,750
Продолжительность работы GC в режиме ядра (сек): 0,060
Продолжительность работы GC в реальном времени (сек): 1,420
Параллелизация: похоже есть

-== ParNewGC + ConcMarkSweepGC ==-
-== Результаты ==-
Всего сборок молодых поколений: 292
Всего полных сборок: 125
Продолжительность работы до OutOfMemory (сек): 246,958
Продолжительность работы GC в пользовательском режиме (сек): 57,070
Продолжительность работы GC в режиме ядра (сек): 0,430
Продолжительность работы GC в реальном времени (сек): 33,840
Параллелизация: похоже есть

-== G1GC ==- (На 24 Мб)
-== Результаты ==-
Всего сборок молодых поколений: 24
Всего полных сборок: 9
Продолжительность работы до OutOfMemory (сек): 46,233
Продолжительность работы GC (сек): 1,462

*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    // [GC (Allocation Failure) [PSYoungGen: 18928K->2528K(18944K)] 37620K->37662K(62976K), 0,0360247 secs] [Times: user=0.13 sys=0.01, real=0.03 secs]
    private static final String MINOR_GC_PATTERN = "^\\[GC.+\\[Times: user=(.+) sys=(.+), real=(.+) secs.+$";
    private static final Pattern minorPattern = Pattern.compile(MINOR_GC_PATTERN);

    // [Full GC (Ergonomics) [PSYoungGen: 11299K->10203K(18944K)] [ParOldGen: 43625K->43631K(44032K)] 54925K->53834K(62976K), [Metaspace: 2753K->2753K(1056768K)], 0,3643538 secs] [Times: user=1.08 sys=0.01, real=0.36 secs]
    private static final String FULL_GC_PATTERN = "^\\[Full GC.+\\[Times: user=(.+) sys=(.+), real=(.+) secs.+$";
    private static final Pattern fullPattern = Pattern.compile(FULL_GC_PATTERN);

    // [CMS-concurrent...] [Times: user=0.13 sys=0.01, real=0.03 secs]
    private static final String CONCURRENT_GC_PATTERN = "^\\[CMS-concurrent.+\\[Times: user=(.+) sys=(.+), real=(.+) secs.+$";
    private static final Pattern concurrentPattern = Pattern.compile(CONCURRENT_GC_PATTERN);

    // [GC (Allocation Failure)  3915K->3915K(5952K), 0.0099047 secs]
    private static final String SIMPLE_MINOR_GC_PATTERN = "^\\[GC.+, (.+) secs.+$";
    private static final Pattern simpleMinorPattern = Pattern.compile(SIMPLE_MINOR_GC_PATTERN);

    // [Full GC (Allocation Failure)  5424K->5243K(5952K), 0.0138773 secs]
    private static final String SIMPLE_FULL_GC_PATTERN = "^\\[Full GC.+, (.+) secs.+$";
    private static final Pattern simpleFullPattern = Pattern.compile(SIMPLE_FULL_GC_PATTERN);

    /**
     * Выводить на экран логи GC? Если false, то выводятся только результаты расчета
     */
    private static final boolean printGCLogs = false;

    /**
     * Количество памяти, выделяемое JVM (в Мб)
     */
    private static final int memoryMb = 64;

    public static void main(String[] args) throws Exception {

        System.out.println("-== SerialGC ==-");
        runMeasureOnDetailedGCLogs("-XX:+UseSerialGC");

        System.out.println();
        System.out.println("-== ParallelGC ==-");
        runMeasureOnDetailedGCLogs("-XX:+UseParallelGC");

        System.out.println();
        System.out.println("-== ParNewGC + ConcMarkSweepGC ==-");
        runMeasureOnDetailedGCLogs("-XX:+UseConcMarkSweepGC");

        // Детализированные логи G1 так страшны, так что будем анализировать простые
        System.out.println();
        System.out.println("-== G1GC ==-");
        runMeasureOnSimpleGCLogs("-XX:+UseG1GC");
    }

    /**
     * Подсчет статистики на основании детализированных логов GC
     */
    private static void runMeasureOnDetailedGCLogs(String javaArgGCType) throws IOException {
        Process process = new ProcessBuilder("java", javaArgGCType, "-XX:+PrintGCDetails", "-Xms" + memoryMb + "m", "-Xmx" + memoryMb + "m", "-cp", "./target/classes", "ru.vinyarsky.hw4.MeasuredMain").start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        long processOverallStartTime = 0;
        int numberOfMinorGC = 0;
        int numberOfFullGC = 0;
        double userSecs = 0;
        double sysSecs = 0;
        double realSecs = 0;

        String line = reader.readLine();
        while (line != null) {
            if (printGCLogs)
                System.out.println(line);
            if (MeasuredMain.STARTED.equals(line))
                processOverallStartTime = System.nanoTime();
            else {
                Matcher matcher = minorPattern.matcher(line);
                if (matcher.matches()) {
                    // Young GC
                    numberOfMinorGC++;
                    userSecs += Double.parseDouble(matcher.group(1));
                    sysSecs += Double.parseDouble(matcher.group(2));
                    realSecs += Double.parseDouble(matcher.group(3));
                }
                else {
                    matcher = fullPattern.matcher(line);
                    if (matcher.matches()) {
                        // Full GC
                        numberOfFullGC++;
                        userSecs += Double.parseDouble(matcher.group(1));
                        sysSecs += Double.parseDouble(matcher.group(2));
                        realSecs += Double.parseDouble(matcher.group(3));
                    }
                    else {
                        matcher = concurrentPattern.matcher(line);
                        if (matcher.matches()) {
                            // Конкурентная часть работы ConcMarkSweepGC
                            // Будем считать, что цикл сборки завершился по завершению sweep
                            // Полагаю, не совсем корректно sweep считать как full GC, ну да ладно...
                            if (line.contains("CMS-concurrent-sweep"))
                                numberOfFullGC++;
                            // А вот продолжительность будем считать суммарную, по всем этапам сборки
                            userSecs += Double.parseDouble(matcher.group(1));
                            sysSecs += Double.parseDouble(matcher.group(2));
                            realSecs += Double.parseDouble(matcher.group(3));
                        }
                    }
                }
            }
            line = reader.readLine();
        }

        double overallDurationInSec = ((System.nanoTime() - processOverallStartTime) / 1000 / 1000) / ((double) 1000);

        if (printGCLogs) {
            String errorLine = errorReader.readLine();
            while (errorLine != null) {
                System.out.println("ERROR: " + errorLine);
                errorLine = errorReader.readLine();
            }
        }

        System.out.println("-== Результаты ==-");
        System.out.println("Всего сборок молодых поколений: " + numberOfMinorGC);
        System.out.println("Всего полных сборок: " + numberOfFullGC);
        System.out.println(String.format("Продолжительность работы до OutOfMemory (сек): %.3f", overallDurationInSec));

        // Если userSecs + sysSecs > realSecs, значит GC работает параллельно на нескольких ядрах
        System.out.println(String.format("Продолжительность работы GC в пользовательском режиме (сек): %.3f", userSecs));
        System.out.println(String.format("Продолжительность работы GC в режиме ядра (сек): %.3f", sysSecs));
        System.out.println(String.format("Продолжительность работы GC в реальном времени (сек): %.3f", realSecs));
        if (userSecs + sysSecs <=  realSecs)
            System.out.println("Параллелизация: что-то не видно");
        else
            System.out.println("Параллелизация: похоже есть");
    }

    /**
     * Подсчет статистики на основании упрощенных логов GC
     */
    private static void runMeasureOnSimpleGCLogs(String javaArgGCType) throws IOException {
        Process process = new ProcessBuilder("java", javaArgGCType, "-XX:+PrintGC", "-Xms" + memoryMb + "m", "-Xmx" + memoryMb + "m", "-cp", "./target/classes", "ru.vinyarsky.hw4.MeasuredMain").start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        long processOverallStartTime = 0;
        int numberOfMinorGC = 0;
        int numberOfFullGC = 0;
        double gcSecs = 0;

        String line = reader.readLine();
        while (line != null) {
            if (printGCLogs)
                System.out.println(line);
            if (MeasuredMain.STARTED.equals(line))
                processOverallStartTime = System.nanoTime();
            else {
                Matcher matcher = simpleMinorPattern.matcher(line);
                if (matcher.matches()) {
                    // Young GC
                    // Фактами сборки будем считать pause и cleanup
                    // Тут есть доля условности:
                    // pause - это копирование между регионами (в результате освобождается память (за счет дефрагментации?))
                    // cleanup - упорядочивание регионов (в результате тоже освобождается память за счет перемаркировки бывших живых объектов как мертвых)
                    if (line.contains("pause") || line.contains("cleanup"))
                        numberOfMinorGC++;
                    gcSecs += Double.parseDouble(matcher.group(1));
                }
                else {
                    matcher = simpleFullPattern.matcher(line);
                    if (matcher.matches()) {
                        // Full GC
                        numberOfFullGC++;
                        gcSecs += Double.parseDouble(matcher.group(1));
                    }
                }
            }
            line = reader.readLine();
        }

        double overallDurationInSec = ((System.nanoTime() - processOverallStartTime) / 1000 / 1000) / ((double) 1000);

        if (printGCLogs) {
            String errorLine = errorReader.readLine();
            while (errorLine != null) {
                System.out.println("ERROR: " + errorLine);
                errorLine = errorReader.readLine();
            }
        }

        System.out.println("-== Результаты ==-");
        System.out.println("Всего сборок молодых поколений: " + numberOfMinorGC);
        System.out.println("Всего полных сборок: " + numberOfFullGC);
        System.out.println(String.format("Продолжительность работы до OutOfMemory (сек): %.3f", overallDurationInSec));

        System.out.println(String.format("Продолжительность работы GC (сек): %.3f", gcSecs));
    }
}
package ru.vinyarsky.hw4;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    // [GC (Allocation Failure) [PSYoungGen: 18928K->2528K(18944K)] 37620K->37662K(62976K), 0,0360247 secs] [Times: user=0.13 sys=0.01, real=0.03 secs]
    static final String MINOR_GC_PATTERN = "^\\[GC.+\\[Times: user=(.+) sys=(.+), real=(.+) secs.+$";

    static final Pattern minorPattern = Pattern.compile(MINOR_GC_PATTERN);

    // [Full GC (Ergonomics) [PSYoungGen: 11299K->10203K(18944K)] [ParOldGen: 43625K->43631K(44032K)] 54925K->53834K(62976K), [Metaspace: 2753K->2753K(1056768K)], 0,3643538 secs] [Times: user=1.08 sys=0.01, real=0.36 secs]
    static final String FULL_GC_PATTERN = "^\\[Full GC.+\\[Times: user=(.+) sys=(.+), real=(.+) secs.+$";

    static final Pattern fullPattern = Pattern.compile(FULL_GC_PATTERN);

    // [CMS-concurrent...] [Times: user=0.13 sys=0.01, real=0.03 secs]
    static final String CONCURRENT_GC_PATTERN = "^\\[CMS-concurrent.+\\[Times: user=(.+) sys=(.+), real=(.+) secs.+$";

    static final Pattern concurrentPattern = Pattern.compile(FULL_GC_PATTERN);


    public static void main(String[] args) throws Exception {

        // Выводить на экран логи GC? Если false, то выводятся только результаты расчета
        boolean printGCLogs = false;

        System.out.println("-== SerialGC ==-");
        runMeasureOnDetailedGCLogs(printGCLogs, "-XX:+UseSerialGC");

        System.out.println();
        System.out.println("-== ParallelGC ==-");
        runMeasureOnDetailedGCLogs(printGCLogs, "-XX:+UseParallelGC");

        System.out.println();
        System.out.println("-== ParNewGC + ConcMarkSweepGC ==-");
        runMeasureOnDetailedGCLogs(printGCLogs, "-XX:+UseConcMarkSweepGC");
    }

    public static void runMeasureOnDetailedGCLogs(boolean printGCLogs, String javaArgGCType) throws IOException {
        Process process = new ProcessBuilder("java", javaArgGCType, "-XX:+PrintGCDetails", "-Xms5m", "-Xmx5m", "-cp", "./target/classes", "ru.vinyarsky.hw4.MeasuredMain").start();
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
}
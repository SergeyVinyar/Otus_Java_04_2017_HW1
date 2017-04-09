package ru.vinyarsky.hw2;

import org.mdkt.compiler.InMemoryJavaCompiler;

import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by sergeyv on 09.04.17.
 */
public class Experiment {

    private StringBuilder builderInit = new StringBuilder();
    private StringBuilder builderFree = new StringBuilder();

    private int syclesCount = 1;
    private int objectNum = 1;
    private String title;

    private String sourceCode;

    public Experiment(String title) {
        this.title = title;
    }

    public Experiment setSyclesCount(int count) {
        this.syclesCount = count;
        return this;
    }

    public Experiment addObject(Class<?> declare) {
        return this.addObject(declare, null, null);
    }

    public <T> Experiment addObject(Class<T> declare, Class<? extends T> initWith, String constructorParameter) {
        String objectName = String.format("object%d", objectNum++);
        builderInit.append(String.format("      %s %s", declare.getCanonicalName(), objectName));
        if (initWith != null)
            builderInit.append(String.format(" = new %s(%s); %s.toString()", initWith.getCanonicalName(), constructorParameter, objectName));
        builderInit.append(String.format(";%n"));
        builderFree.append(String.format("      %s = null;%n", objectName));
        return this;
    }

    public Experiment addArray(Class<?> declare) {
        return this.addArray(declare, null, null, 0);
    }

    public Experiment addArray(Class<?> declare, Class<?> initWith, String constructorParameter, int arraySize) {
        String objectName = String.format("object%d", objectNum++);
        builderInit.append(String.format("      %s[] %s", declare.getCanonicalName(), objectName));
        if (initWith != null) {
            builderInit.append(" = {");
            for (int i = 0; i < arraySize; i++) {
                builderInit.append(String.format("new %s(%s)", initWith.getCanonicalName(), constructorParameter));
                builderInit.append(", ");
            }
            builderInit.append("}");
        }
        builderInit.append(String.format(";%n"));
        builderFree.append(String.format("      %s = null;%n", objectName));
        return this;
    }

    public Experiment execute(boolean printSamleSourceCode) throws Exception {
        final String packageName = this.getClass().getPackage().getName();
        final String className = "Sample" + Integer.toString(Math.abs(new Random().nextInt()));

        StringBuilder b = new StringBuilder();
        b.append(String.format("package %s;%n", packageName));
        b.append(String.format("public class %s implements %s.Experiment.ISample {%n", className, packageName));
        b.append(String.format("  public void run() throws Exception {%n"));
        b.append(String.format("    long memoryBefore = 0;%n"));
        b.append(String.format("    long memoryAfter = 0;%n"));
        b.append(String.format("    long sumOfDeltas = 0;%n"));
        b.append(String.format("    Runtime runtime = Runtime.getRuntime();%n"));

        for (int i = 0; i < this.syclesCount; i++) {
            b.append(String.format("    {%n"));
            b.append(String.format("      System.gc();%n"));
            b.append(String.format("      Thread.sleep(100);%n"));
            b.append(                     builderInit.toString());
            b.append(String.format("      memoryBefore = runtime.totalMemory() - runtime.freeMemory();%n"));
            b.append(String.format("      System.gc();%n"));
            b.append(String.format("      Thread.sleep(100);%n"));
            b.append(                     builderFree.toString());
            b.append(String.format("      System.gc();%n"));
            b.append(String.format("      Thread.sleep(100);%n"));
            b.append(String.format("      memoryAfter = runtime.totalMemory() - runtime.freeMemory();%n"));
            b.append(String.format("      sumOfDeltas += memoryBefore - memoryAfter;%n"));

            b.append(String.format("      System.out.print(\"Memory: \");%n"));
            b.append(String.format("      System.out.print(memoryBefore);%n"));
            b.append(String.format("      System.out.print(\" - \");%n"));
            b.append(String.format("      System.out.print(memoryAfter);%n"));
            b.append(String.format("      System.out.print(\" = \");%n"));
            b.append(String.format("      System.out.print(memoryBefore - memoryAfter);%n"));
            b.append(String.format("      System.out.println();%n"));
            b.append(String.format("    }%n"));
        }

        b.append(String.format("    System.out.print(\"Average: \");%n"));
        b.append(String.format("    System.out.println(sumOfDeltas / %d);%n", syclesCount));
        b.append(String.format("  }%n"));
        b.append(String.format("}%n"));
        this.sourceCode = b.toString();

        Class<?> sampleType = InMemoryJavaCompiler.compile(packageName + "." + className, sourceCode);
        ISample sample = (ISample)sampleType.newInstance();

        System.out.println("===BEGIN=====================================");
        System.out.println("-  " + this.title);
        System.out.println("---------------------------------------------");
        if (printSamleSourceCode)
            System.out.println(this.sourceCode);
        sample.run();
        System.out.println("---END---------------------------------------");
        System.out.println("");
        System.out.flush();

        return this;
    }

    public interface ISample {
        void run() throws Exception;
    }
}

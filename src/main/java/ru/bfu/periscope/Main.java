package ru.bfu.periscope;
import java.util.Random;

public class Main {
    private static int[] array;
    private static int totalSum = 0;
    private static final Object lock = new Object();

    public static void main(String[] args) {
        //int arraySize = 13;
        //int threadsCount = 3;

        if (args.length != 2) {
            System.out.println("Usage: java Program --arraySize=<size> --threadsCount=<count>");
            return;
        }

        int arraySize = Integer.parseInt(args[0].split("=")[1]);
        int threadsCount = Integer.parseInt(args[1].split("=")[1]);

        if (arraySize > 2000000 || threadsCount > arraySize) {
            System.out.println("Invalid input parameters.");
            return;
        }

        array = new int[arraySize];
        Random random = new Random();
        for (int i = 0; i < arraySize; i++) {
            //array [i] = 1;
            array[i] = random.nextInt(2001) - 1000; // Генерация чисел от -1000 до 1000
        }

        // Вычисление суммы стандартным методом
        int standardSum = 0;
        for (int num : array) {
            standardSum += num;
        }
        System.out.println("Sum: " + standardSum);

        // Вычисление суммы с использованием потоков
        Thread[] threads = new Thread[threadsCount];
        int sectionSize = (arraySize + threadsCount - 1) / threadsCount;
        int startIndex = 0;
        int endIndex = sectionSize - 1;

        for (int i = 0; i < threadsCount; i++) {
            if (i == threadsCount - 1) {
                endIndex = arraySize - 1; // Последний поток обрабатывает оставшиеся элементы
            }

            threads[i] = new SumThread(startIndex, endIndex, i + 1);
            threads[i].start();

            startIndex = endIndex + 1;
            endIndex = startIndex + sectionSize - 1;
        }

        // Ожидание завершения всех потоков
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Sum by threads: " + totalSum);
    }

    static class SumThread extends Thread {
        private final int startIndex;
        private final int endIndex;
        private final int threadNumber;

        public SumThread(int startIndex, int endIndex, int threadNumber) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.threadNumber = threadNumber;
        }

        @Override
        public void run() {
            int sum = 0;
            for (int i = startIndex; i <= endIndex; i++) {
                sum += array[i];
            }

            synchronized (lock) {
                totalSum += sum;
            }

            System.out.printf("Thread %d: from %d to %d sum is %d%n", threadNumber, startIndex, endIndex, sum);
        }
    }
}
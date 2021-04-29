package ru.itfbgroup.regex;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("Укажите путь до директории");
            return;
        }

        int threadCount = 3;
        int charCount = 4;

        Map<String, Long> words = new ConcurrentHashMap<>();
        File dir = new File(args[0]);

        if (args.length >= 2 && args[1] != null) {
            charCount = Integer.parseInt(args[1]);
        }

        if (args.length >= 3 && args[2] != null) {
            threadCount = Integer.parseInt(args[2]);
        }

        Pattern pattern = Pattern.compile("[А-яA-z']{" + (charCount + 1) + "}");
        ExecutorService executor = Executors.newFixedThreadPool(threadCount, new CustomThreadFactory());

        if (dir.listFiles() == null) {
            System.out.println("В папке нет файлов или неверно указан путь.");
            return;
        }

        BlockingQueue<File> blockQueue = new ArrayBlockingQueue<>(Objects.requireNonNull(dir.listFiles()).length, true);
        blockQueue.addAll((Arrays.asList(Objects.requireNonNull(dir.listFiles()))));

        for (int i = 0; i < Objects.requireNonNull(dir.listFiles()).length; i++) {
            try {
                Runnable runnable = new WriteFile(blockQueue.take(), pattern, words);
                executor.execute(runnable);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        while (true) {
            if (executor.isTerminated()) break;
        }

        words.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(10).forEach(System.out::println);
    }
}

package ru.itfbgroup.regex;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WriteFile implements Runnable {

    private final File fileName;
    private final Pattern pattern;
    private final Map<String, Long> words;

    public WriteFile(File fileName, Pattern pattern, Map<String, Long> words) {
        this.fileName = fileName;
        this.pattern = pattern;
        this.words = words;
    }

    @Override
    public void run() {
        if (fileName.isDirectory()) {
            System.out.println(fileName + " - это директория.");
            return;
        }

        try {
            System.out.println(Thread.currentThread().getName() + " выполняет чтение файла: " + fileName);
            Map<String, Long> collect = Files.lines(fileName.toPath())
                    .flatMap(s -> {
                        LinkedList<String> w = new LinkedList<>();
                        Matcher matcher = pattern.matcher(s);
                        while (matcher.find()) {
                            w.add(matcher.group());
                        }
                        return w.stream();
                    })
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            collect.forEach((key, value) -> words.merge(key.toLowerCase(), value, Long::sum));
            System.out.println(Thread.currentThread().getName() + " закончил работу с файлом");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

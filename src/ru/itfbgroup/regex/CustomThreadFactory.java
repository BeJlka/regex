package ru.itfbgroup.regex;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger counter = new AtomicInteger();

        public Thread newThread(Runnable r) {
            return new Thread(r, "Thread - " + counter.getAndIncrement());
        }
    }
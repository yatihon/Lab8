package me.tihon.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

public class ThreadPools {

    public static final ForkJoinPool readPool = new ForkJoinPool();
    public static final ExecutorService requestPool = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());
    public static final ForkJoinPool sendPool = new ForkJoinPool();

    public static void shutdown() {
        requestPool.shutdown();
        readPool.shutdown();
        sendPool.shutdown();
    }
}


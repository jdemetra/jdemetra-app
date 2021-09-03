package demetra.desktop.concurrent;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class UIExecutors {

    private UIExecutors() {
        // static class
    }

    @NonNull
    public static ExecutorService newSingleThreadExecutor(ThreadPriority priority) {
        return Executors.newSingleThreadExecutor(getThreadFactory(priority));
    }

    @NonNull
    public static ExecutorService newFixedThreadPool(ThreadPoolSize poolSize, ThreadPriority priority) {
        return Executors.newFixedThreadPool(poolSize.getSize(), getThreadFactory(priority));
    }

    private static ThreadFactory getThreadFactory(ThreadPriority priority) {
        return DefaultThreadFactory
                .builder()
                .daemon(true)
                .priority(priority.getPriority())
                .build();
    }
}

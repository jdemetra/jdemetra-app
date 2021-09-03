package demetra.desktop.concurrent;

import org.checkerframework.checker.index.qual.NonNegative;

@lombok.AllArgsConstructor
public enum ThreadPoolSize {

    SINGLE(1),
    ALL(Runtime.getRuntime().availableProcessors()),
    ALL_BUT_ONE(Math.max(1, Runtime.getRuntime().availableProcessors() - 1));

    private final int value;

    @NonNegative
    public int getSize() {
        return value;
    }
}

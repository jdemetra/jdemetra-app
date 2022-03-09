package demetra.desktop.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;

@lombok.experimental.UtilityClass
public class Caches {
    
    @NonNull
    public static <K, V> Cache<K, V> ttlCache(@NonNull Duration duration) {
        return CacheBuilder.newBuilder()
                .expireAfterWrite(duration.toNanos(), TimeUnit.NANOSECONDS)
                .build();
    }

    @NonNull
    public static <K, V> ConcurrentMap<K, V> ttlCacheAsMap(@NonNull Duration duration) {
        return Caches.<K, V>ttlCache(duration).asMap();
    }

    @NonNull
    public static <K, V> Cache<K, V> softValuesCache() {
        return CacheBuilder.newBuilder().softValues().build();
    }

    @NonNull
    public static <K, V> ConcurrentMap<K, V> softValuesCacheAsMap() {
        return Caches.<K, V>softValuesCache().asMap();
    }   
}

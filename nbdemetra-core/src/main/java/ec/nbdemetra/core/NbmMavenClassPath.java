package ec.nbdemetra.core;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@lombok.experimental.UtilityClass
public class NbmMavenClassPath {

    public static @NonNull List<GAV> parse(@NonNull Manifest manifest) {
        String items = manifest.getMainAttributes().getValue("Maven-Class-Path");
        return items != null
                ? splitAsStream(items, ' ').map(GAV::parse).collect(Collectors.toList())
                : Collections.emptyList();
    }

    private static Stream<String> splitAsStream(String items, char sep) {
        return Arrays.stream(items.split(String.valueOf(sep), -1));
    }

    @lombok.Value
    public static class GAV {

        public static @NonNull GAV parse(@NonNull CharSequence input) throws IllegalArgumentException {
            String[] items = input.toString().split(":", -1);
            if (items.length != 3) {
                throw new IllegalArgumentException("Invalid GAV: '" + input + "'");
            }
            return new GAV(items[0], items[1], items[2]);
        }

        @NonNull String groupId;
        @NonNull String artifactId;
        @NonNull String version;

        public static boolean haveSameVersion(@NonNull List<? extends GAV> list) {
            return list
                    .stream()
                    .map(GAV::getVersion)
                    .distinct()
                    .count() == 1;
        }
    }
}

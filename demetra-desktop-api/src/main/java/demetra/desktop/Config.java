/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop;

import internal.util.SortedMaps;
import nbbrd.design.ThreadSafe;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;

/**
 *
 * @author Philippe Charles
 */
@lombok.Value
@lombok.Builder(toBuilder = true)
public class Config {

    @lombok.NonNull
    String domain;

    @lombok.NonNull
    String name;

    @lombok.NonNull
    String version;

    @lombok.NonNull
    @lombok.Singular
    SortedMap<String, String> parameters;

    @Nullable
    public String getParameter(@NonNull String key) {
        return parameters.get(key);
    }

    @NonNull
    public static Config deepCopyOf(@NonNull String domain, @NonNull String name, @NonNull String version, @NonNull Map<String, String> params) {
        Objects.requireNonNull(domain, "domain");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(params, "params");
        return new Config(domain, name, version, SortedMaps.immutableCopyOf(params));
    }

    @NonNull
    public static Builder builder(@NonNull String domain, @NonNull String name, @NonNull String version) {
        Objects.requireNonNull(domain, "domain");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(version, "version");
        return new Builder().domain(domain).name(name).version(version);
    }

    @ThreadSafe
    public interface Converter<P> {

        @NonNull
        P getDefaultValue();

        @NonNull
        P get(@NonNull Config config);

        void set(@NonNull Builder builder, @Nullable P value);
    }

    public static @NonNull Config checkDomain(@NonNull Config config, @NonNull String domain) throws IllegalArgumentException {
        if (!domain.equals(config.getDomain())) {
            throw new IllegalArgumentException("Not produced here");
        }
        return config;
    }
}

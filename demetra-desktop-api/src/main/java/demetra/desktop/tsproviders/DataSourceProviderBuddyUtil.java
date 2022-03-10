/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved
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
package demetra.desktop.tsproviders;

import demetra.desktop.Config;
import demetra.tsprovider.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.ErrorManager;
import org.openide.nodes.Sheet;

import java.beans.IntrospectionException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Philippe Charles
 */
public final class DataSourceProviderBuddyUtil {

    private DataSourceProviderBuddyUtil() {
        // static class
    }

    @NonNull
    public static String getDataSourceDomain() {
        return DataSource.class.getName();
    }

    @NonNull
    public static DataSource getDataSource(@NonNull Config config) throws IllegalArgumentException {
        String uri = config.getParameter("uri");
        if (uri == null) {
            throw new IllegalArgumentException("Invalid config");
        }
        return DataSource.parse(uri);
    }

    public static Config getConfig(DataSource dataSource, String displayName) {
        return Config.builder(getDataSourceDomain(), displayName, "")
                .parameter("uri", dataSource.toString())
                .build();
    }

    @NonNull
    public static Sheet sheetOf(List<Sheet.Set> sets) {
        Sheet result = new Sheet();
        sets.forEach(result::put);
        return result;
    }

    @NonNull
    public static Sheet sheetOf(Sheet.Set... sets) {
        Sheet result = new Sheet();
        Stream.of(sets).forEach(result::put);
        return result;
    }

    @FunctionalInterface
    public interface IntrospectionFunc<X, Y> {

        Y apply(X x) throws IntrospectionException;
    }

    @NonNull
    public static <X, Y> Function<X, Y> usingErrorManager(IntrospectionFunc<X, Y> func, Supplier<Y> defaultValue) {
        return (X x) -> {
            try {
                return func.apply(x);
            } catch (IntrospectionException ex) {
                ErrorManager.getDefault().log(ex.getMessage());
                return defaultValue.get();
            }
        };
    }
}

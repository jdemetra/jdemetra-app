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
package demetra.desktop.sql.jdbc;

import demetra.desktop.util.XmlConfig;
import internal.util.SortedMaps;
import nbbrd.design.MightBePromoted;
import nbbrd.design.VisibleForTesting;
import nbbrd.io.function.IOFunction;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;
import nbbrd.io.text.TextFormatter;
import nbbrd.io.text.TextParser;
import nbbrd.io.xml.bind.Jaxb;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;

/**
 *
 * @author Philippe Charles
 */
@lombok.Value
@lombok.Builder(toBuilder = true)
public final class DriverBasedConfig {

    @lombok.NonNull
    String driverClass;

    @lombok.NonNull
    String databaseUrl;

    @lombok.NonNull
    String schema;

    @lombok.NonNull
    String displayName;

    @lombok.NonNull
    @lombok.Singular
    private final SortedMap<String, String> params;

    @VisibleForTesting
    ConnectionBean toBean() {
        ConnectionBean bean = new ConnectionBean();
        bean.driverClass = driverClass;
        bean.databaseUrl = databaseUrl;
        bean.schema = schema;
        bean.displayName = displayName;
        bean.params = XmlConfig.ParamBean.fromSortedMap(params);
        return bean;
    }

    @NonNull
    public static DriverBasedConfig deepCopyOf(@NonNull String driverClass, @NonNull String databaseUrl, @NonNull String schema, @NonNull String displayName, @NonNull Map<String, String> params) {
        return new DriverBasedConfig(
                Objects.requireNonNull(driverClass, "driverClass"),
                Objects.requireNonNull(databaseUrl, "databaseUrl"),
                Objects.requireNonNull(schema, "schema"),
                Objects.requireNonNull(displayName, "displayName"),
                SortedMaps.immutableCopyOf(params));
    }

    @NonNull
    public static Builder builder(@NonNull String driverClass, @NonNull String databaseUrl, @NonNull String schema, @NonNull String displayName) {
        Objects.requireNonNull(driverClass, "driverClass");
        Objects.requireNonNull(databaseUrl, "databaseUrl");
        Objects.requireNonNull(schema, "schema");
        Objects.requireNonNull(displayName, "displayName");
        return new Builder().driverClass(driverClass).databaseUrl(databaseUrl).schema(schema).displayName(displayName);
    }

    public static Formatter<DriverBasedConfig> xmlFormatter(boolean formattedOutput) {
        return formattedOutput ? formattedOutputFormatter : defaultFormatter;
    }

    public static Parser<DriverBasedConfig> xmlParser() {
        return defaultParser;
    }

    @XmlRootElement(name = "jdbcConnection")
    static final class ConnectionBean {

        @XmlAttribute
        public String driverClass;
        @XmlAttribute
        public String databaseUrl;
        @XmlAttribute
        public String schema;
        @XmlAttribute
        public String displayName;
        @XmlElement(name = "param")
        public XmlConfig.ParamBean[] params;

        DriverBasedConfig toId() {
            return new DriverBasedConfig(
                    nullToEmpty(driverClass),
                    nullToEmpty(databaseUrl),
                    nullToEmpty(schema),
                    nullToEmpty(displayName),
                    XmlConfig.ParamBean.toSortedMap(params));
        }

        private static @NonNull String nullToEmpty(@Nullable String o) {
            return o == null ? "" : o;
        }

        static ConnectionBean of(DriverBasedConfig config) {
            ConnectionBean bean = new ConnectionBean();
            bean.driverClass = config.getDriverClass();
            bean.databaseUrl = config.getDatabaseUrl();
            bean.schema = config.getSchema();
            bean.displayName = config.getDisplayName();
            bean.params = XmlConfig.ParamBean.fromSortedMap(config.getParams());
            return bean;
        }
    }

    private static final Parser<DriverBasedConfig> defaultParser;
    private static final Formatter<DriverBasedConfig> defaultFormatter;
    private static final Formatter<DriverBasedConfig> formattedOutputFormatter;

    static {
//        try {
            defaultParser = asParser(Jaxb.Parser.of(ConnectionBean.class).andThen(ConnectionBean::toId));
            defaultFormatter = asFormatter(Jaxb.Formatter.of(ConnectionBean.class).withFormatted(false).compose(ConnectionBean::of));
            formattedOutputFormatter = asFormatter(Jaxb.Formatter.of(ConnectionBean.class).withFormatted(true).compose(ConnectionBean::of));
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
    }

    @MightBePromoted
    private static <T> Parser<T> asParser(TextParser<T> parser) {
        return Parser.of(IOFunction.unchecked(parser::parseChars)::apply);
    }

    @MightBePromoted
    private static <T> Formatter<T> asFormatter(TextFormatter<T> formatter) {
        return Formatter.of(IOFunction.unchecked(formatter::formatToString)::apply);
    }
}

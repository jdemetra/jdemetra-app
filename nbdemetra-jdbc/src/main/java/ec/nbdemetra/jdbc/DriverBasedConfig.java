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
package ec.nbdemetra.jdbc;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSortedMap;
import ec.tss.tsproviders.utils.AbstractConfigBuilder;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.tsproviders.utils.IConfig;
import ec.tss.tsproviders.utils.ParamBean;
import ec.tss.tsproviders.utils.Parsers;
import ec.tstoolkit.design.Immutable;
import ec.tstoolkit.design.VisibleForTesting;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import javax.annotation.Nonnull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Philippe Charles
 */
@Immutable
public final class DriverBasedConfig implements IConfig {

    private final String driverClass;
    private final String databaseUrl;
    private final String schema;
    private final String displayName;
    private final ImmutableSortedMap<String, String> params;

    @VisibleForTesting
    DriverBasedConfig(@Nonnull String driverClass, @Nonnull String databaseUrl, @Nonnull String schema, @Nonnull String displayName, @Nonnull ImmutableSortedMap<String, String> params) {
        this.driverClass = driverClass;
        this.databaseUrl = databaseUrl;
        this.schema = schema;
        this.displayName = displayName;
        this.params = params;
    }

    @Nonnull
    public String getDriverClass() {
        return driverClass;
    }

    @Nonnull
    public String getDatabaseUrl() {
        return databaseUrl;
    }

    @Nonnull
    public String getSchema() {
        return schema;
    }

    @Nonnull
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public SortedMap<String, String> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof DriverBasedConfig && equals((DriverBasedConfig) obj));
    }

    private boolean equals(DriverBasedConfig that) {
        return this.driverClass.equals(that.driverClass)
                && this.databaseUrl.equals(that.databaseUrl)
                && this.schema.equals(that.schema)
                && this.displayName.equals(that.displayName)
                && this.params.equals(that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(driverClass, databaseUrl, schema, displayName, params);
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper result = MoreObjects.toStringHelper(displayName + "(" + driverClass + ") url=" + databaseUrl + " schema=" + schema);
        forEach(result::add);
        return result.toString();
    }

    @VisibleForTesting
    ConnectionBean toBean() {
        ConnectionBean bean = new ConnectionBean();
        bean.driverClass = driverClass;
        bean.databaseUrl = databaseUrl;
        bean.schema = schema;
        bean.displayName = displayName;
        bean.params = ParamBean.fromSortedMap(params);
        return bean;
    }

    @Nonnull
    public static DriverBasedConfig deepCopyOf(@Nonnull String driverClass, @Nonnull String databaseUrl, @Nonnull String schema, @Nonnull String displayName, @Nonnull Map<String, String> params) {
        return new DriverBasedConfig(
                Objects.requireNonNull(driverClass, "driverClass"),
                Objects.requireNonNull(databaseUrl, "databaseUrl"),
                Objects.requireNonNull(schema, "schema"),
                Objects.requireNonNull(displayName, "displayName"),
                ImmutableSortedMap.copyOf(Objects.requireNonNull(params, "params")));
    }

    @Nonnull
    public static Builder builder(@Nonnull String driverClass, @Nonnull String databaseUrl, @Nonnull String schema, @Nonnull String displayName) {
        return new Builder(
                Objects.requireNonNull(driverClass, "driverClass"),
                Objects.requireNonNull(databaseUrl, "databaseUrl"),
                Objects.requireNonNull(schema, "schema"),
                Objects.requireNonNull(displayName, "displayName"));
    }

    @Nonnull
    public static Formatters.Formatter<DriverBasedConfig> xmlFormatter(boolean formattedOutput) {
        return formattedOutput ? XML.get().formattedOutputFormatter : XML.get().defaultFormatter;
    }

    @Nonnull
    public static Parsers.Parser<DriverBasedConfig> xmlParser() {
        return XML.get().defaultParser;
    }

    public static final class Builder extends AbstractConfigBuilder<Builder, DriverBasedConfig> {

        private final String driverClass;
        private final String databaseUrl;
        private final String schema;
        private final String displayName;

        @VisibleForTesting
        Builder(@Nonnull String driverClass, @Nonnull String databaseUrl, @Nonnull String schema, @Nonnull String displayName) {
            this.driverClass = driverClass;
            this.databaseUrl = databaseUrl;
            this.schema = schema;
            this.displayName = displayName;
        }

        @Override
        public DriverBasedConfig build() {
            return deepCopyOf(driverClass, databaseUrl, schema, displayName, params);
        }
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
        public ParamBean[] params;

        DriverBasedConfig toId() {
            return new DriverBasedConfig(
                    Strings.nullToEmpty(driverClass),
                    Strings.nullToEmpty(databaseUrl),
                    Strings.nullToEmpty(schema),
                    Strings.nullToEmpty(displayName),
                    ParamBean.toSortedMap(params));
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static final ThreadLocal<Xml> XML = ThreadLocal.withInitial(Xml::new);

    private static final class Xml {

        final static JAXBContext BEAN_CONTEXT;

        static {
            try {
                BEAN_CONTEXT = JAXBContext.newInstance(ConnectionBean.class);
            } catch (JAXBException ex) {
                throw Throwables.propagate(ex);
            }
        }

        final Parsers.Parser<DriverBasedConfig> defaultParser = Parsers.wrap(Parsers.<ConnectionBean>onJAXB(BEAN_CONTEXT).andThen(ConnectionBean::toId));
        final Formatters.Formatter<DriverBasedConfig> defaultFormatter = Formatters.<ConnectionBean>onJAXB(BEAN_CONTEXT, false).compose(DriverBasedConfig::toBean);
        final Formatters.Formatter<DriverBasedConfig> formattedOutputFormatter = Formatters.<ConnectionBean>onJAXB(BEAN_CONTEXT, true).compose(DriverBasedConfig::toBean);
    }
    //</editor-fold>
}

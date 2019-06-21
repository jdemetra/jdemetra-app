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
package ec.nbdemetra.ui;

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
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 *
 * @author Philippe Charles
 */
@Immutable
@XmlJavaTypeAdapter(Config.XmlAdapter.class)
public final class Config implements IConfig, Serializable {

    private final String domain;
    private final String name;
    private final String version;
    private final ImmutableSortedMap<String, String> params;

    @VisibleForTesting
    Config(String domain, String name, String version, ImmutableSortedMap<String, String> params) {
        this.domain = domain;
        this.name = name;
        this.version = version;
        this.params = params;
    }

    public String getDomain() {
        return domain;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public SortedMap<String, String> getParams() {
        return params;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (obj instanceof Config && equals((Config) obj));
    }

    private boolean equals(Config that) {
        return this.domain.equals(that.domain)
                && this.name.equals(that.name)
                && this.version.equals(that.version)
                && this.params.equals(that.params);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain, name, version, params);
    }

    @Override
    public String toString() {
        MoreObjects.ToStringHelper result = MoreObjects.toStringHelper(domain + "/" + name + "(" + version + ")");
        forEach(result::add);
        return result.toString();
    }

    /**
     * Creates a new builder with the content of this config.
     *
     * @return a non-null builder
     * @since 2.2.0
     */
    public Config.@NonNull Builder toBuilder() {
        return new Builder(domain, name, version).putAll(params);
    }

    @VisibleForTesting
    ConfigBean toBean() {
        ConfigBean bean = new ConfigBean();
        bean.domain = domain;
        bean.name = name;
        bean.version = version;
        bean.params = ParamBean.fromSortedMap(params);
        return bean;
    }

    public static Config deepCopyOf(String domain, String name, String version, Map<String, String> params) {
        Objects.requireNonNull(domain, "domain");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(params, "params");
        return new Config(domain, name, version, ImmutableSortedMap.copyOf(params));
    }

    public static Config.Builder builder(String domain, String name, String version) {
        Objects.requireNonNull(domain, "domain");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(version, "version");
        return new Builder(domain, name, version);
    }

    /**
     * Returns a convenient NamedServiceConfig formatter that produces xml
     * output.<p>
     * This formatter is not thread-safe but unique per thread. To use it
     * thread-safely, don't store it but use it directly.
     * <br><code>NamedServiceConfig.xmlFormatter().format(...)</code>
     *
     * @param formattedOutput
     * @see ThreadLocal
     * @return a NamedServiceConfig formatter
     */
    public static Formatters.Formatter<Config> xmlFormatter(boolean formattedOutput) {
        return formattedOutput ? XML.get().formattedOutputFormatter : XML.get().defaultFormatter;
    }

    /**
     * Returns a convenient NamedServiceConfig parser that consumes xml
     * input.<p>
     * This parser is not thread-safe but unique per thread. To use it
     * thread-safely, don't store it but use it directly.
     * <br><code>NamedServiceConfig.xmlParser().parse(...)</code>
     *
     * @see ThreadLocal
     * @return a NamedServiceConfig parser
     */
    public static Parsers.Parser<Config> xmlParser() {
        return XML.get().defaultParser;
    }

    public static class Builder extends AbstractConfigBuilder<Builder, Config> {

        final String service;
        final String name;
        final String version;

        @VisibleForTesting
        Builder(String service, String name, String version) {
            this.service = service;
            this.name = name;
            this.version = version;
        }

        @Override
        public Config build() {
            return deepCopyOf(service, name, version, params);
        }
    }

    public static class XmlAdapter extends javax.xml.bind.annotation.adapters.XmlAdapter<ConfigBean, Config> {

        @Override
        public Config unmarshal(ConfigBean v) throws Exception {
            return v.toId();
        }

        @Override
        public ConfigBean marshal(Config v) throws Exception {
            return v.toBean();
        }
    }

    @XmlRootElement(name = "config")
    public static class ConfigBean {

        @XmlAttribute(name = "domain")
        public String domain;
        @XmlAttribute(name = "name")
        public String name;
        @XmlAttribute(name = "version")
        public String version;
        @XmlElement(name = "param")
        public ParamBean[] params;

        Config toId() {
            return new Config(
                    Strings.nullToEmpty(domain),
                    Strings.nullToEmpty(name),
                    Strings.nullToEmpty(version),
                    ParamBean.toSortedMap(params));
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static final ThreadLocal<Xml> XML = ThreadLocal.withInitial(Xml::new);

    private static final class Xml {

        final static JAXBContext BEAN_CONTEXT;

        static {
            try {
                BEAN_CONTEXT = JAXBContext.newInstance(ConfigBean.class);
            } catch (JAXBException ex) {
                throw Throwables.propagate(ex);
            }
        }

        final Parsers.Parser<Config> defaultParser = Parsers.wrap(Parsers.<ConfigBean>onJAXB(BEAN_CONTEXT).andThen(ConfigBean::toId));
        final Formatters.Formatter<Config> defaultFormatter = Formatters.<ConfigBean>onJAXB(BEAN_CONTEXT, false).compose(Config::toBean);
        final Formatters.Formatter<Config> formattedOutputFormatter = Formatters.<ConfigBean>onJAXB(BEAN_CONTEXT, true).compose(Config::toBean);
    }
    //</editor-fold>
}

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
package ec.nbdemetra.ui.interchange.impl;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.StandardSystemProperty;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.interchange.Exportable;
import ec.nbdemetra.ui.interchange.Importable;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.tsproviders.utils.Parsers;
import ec.tstoolkit.design.Immutable;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nonnull;
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
@XmlJavaTypeAdapter(Configs.XmlAdapter.class)
public final class Configs {

    private final String author;
    private final long creationTime;
    private final ImmutableList<Config> items;

    @VisibleForTesting
    Configs(String author, long creationTime, ImmutableList<Config> items) {
        this.author = author;
        this.creationTime = creationTime;
        this.items = items;
    }

    @Nonnull
    public String getAuthor() {
        return author;
    }

    public long getCreationTime() {
        return creationTime;
    }

    @Nonnull
    public List<Config> getItems() {
        return items;
    }

    @VisibleForTesting
    ConfigsBean toBean() {
        ConfigsBean result = new ConfigsBean();
        result.author = author;
        result.creationTime = creationTime;
        result.items = Iterables.toArray(items, Config.class);
        return result;
    }

    @Nonnull
    public static Formatters.Formatter<Configs> xmlFormatter(boolean formattedOutput) {
        return formattedOutput ? XML.get().formattedOutputFormatter : XML.get().defaultFormatter;
    }

    @Nonnull
    public static Parsers.Parser<Configs> xmlParser() {
        return XML.get().defaultParser;
    }

    public static class XmlAdapter extends javax.xml.bind.annotation.adapters.XmlAdapter<ConfigsBean, Configs> {

        @Override
        public Configs unmarshal(ConfigsBean v) throws Exception {
            return v.toId();
        }

        @Override
        public ConfigsBean marshal(Configs v) throws Exception {
            return v.toBean();
        }
    }

    @XmlRootElement(name = "configs")
    public static final class ConfigsBean {

        @XmlAttribute
        public String author;
        @XmlAttribute
        public long creationTime;
        @XmlElement(name = "config")
        public Config[] items;

        @VisibleForTesting
        Configs toId() {
            return new Configs(Strings.nullToEmpty(author), creationTime, items != null ? ImmutableList.<Config>copyOf(items) : ImmutableList.<Config>of());
        }
    }

    public static Configs fromExportables(List<? extends Exportable> exportables) {
        ImmutableList.Builder<Config> items = ImmutableList.builder();
        for (Exportable o : exportables) {
            items.add(o.exportConfig());
        }
        return new Configs(StandardSystemProperty.USER_NAME.value(), System.currentTimeMillis(), items.build());
    }

    public void performImport(List<? extends Importable> importables) throws IOException, IllegalArgumentException {
        for (Config o : items) {
            for (Importable importable : importables) {
                if (importable.getDomain().equals(o.getDomain())) {
                    importable.importConfig(o);
                    break;
                }
            }
        }
    }

    public boolean canImport(List<? extends Importable> importables) {
        for (Config o : items) {
            for (Importable importable : importables) {
                if (importable.getDomain().equals(o.getDomain())) {
                    return true;
                }
            }
        }
        return false;
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static final ThreadLocal<Xml> XML = new ThreadLocal<Xml>() {
        @Override
        protected Xml initialValue() {
            return new Xml();
        }
    };

    private static final class Xml {

        final static JAXBContext BEAN_CONTEXT;

        static {
            try {
                BEAN_CONTEXT = JAXBContext.newInstance(ConfigsBean.class);
            } catch (JAXBException ex) {
                throw Throwables.propagate(ex);
            }
        }
        final static Function<ConfigsBean, Configs> FROM_BEAN = new Function<ConfigsBean, Configs>() {
            @Override
            public Configs apply(ConfigsBean input) {
                return input.toId();
            }
        };
        final static Function<Configs, ConfigsBean> TO_BEAN = new Function<Configs, ConfigsBean>() {
            @Override
            public ConfigsBean apply(Configs input) {
                return input.toBean();
            }
        };

        final Parsers.Parser<Configs> defaultParser = Parsers.<ConfigsBean>onJAXB(BEAN_CONTEXT).compose(FROM_BEAN);
        final Formatters.Formatter<Configs> defaultFormatter = Formatters.<ConfigsBean>onJAXB(BEAN_CONTEXT, false).compose(TO_BEAN);
        final Formatters.Formatter<Configs> formattedOutputFormatter = Formatters.<ConfigsBean>onJAXB(BEAN_CONTEXT, true).compose(TO_BEAN);
    }
    //</editor-fold>
}

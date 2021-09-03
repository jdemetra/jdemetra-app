package ec.nbdemetra.ui;

import demetra.desktop.Config;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.tsproviders.utils.ParamBean;
import ec.tss.tsproviders.utils.Parsers;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 */
public class XmlConfig {

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
            return Config.builder(Strings.nullToEmpty(domain), Strings.nullToEmpty(name), Strings.nullToEmpty(version))
                    .parameters(ParamBean.toSortedMap(params))
                    .build();
        }

        static ConfigBean of(Config config) {
            ConfigBean bean = new ConfigBean();
            bean.domain = config.getDomain();
            bean.name = config.getName();
            bean.version = config.getVersion();
            bean.params = ParamBean.fromSortedMap(config.getParameters());
            return bean;
        }
    }

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
        final Formatters.Formatter<Config> defaultFormatter = Formatters.<ConfigBean>onJAXB(BEAN_CONTEXT, false).compose(ConfigBean::of);
        final Formatters.Formatter<Config> formattedOutputFormatter = Formatters.<ConfigBean>onJAXB(BEAN_CONTEXT, true).compose(ConfigBean::of);
    }
}

package demetra.desktop.util;

import demetra.desktop.Config;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import nbbrd.design.MightBePromoted;
import nbbrd.io.function.IOFunction;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;
import nbbrd.io.text.TextFormatter;
import nbbrd.io.text.TextParser;
import nbbrd.io.xml.bind.Jaxb;

/**
 *
 */
@Deprecated
@lombok.experimental.UtilityClass
public class XmlConfig {

    public static Formatter<Config> xmlFormatter(boolean formattedOutput) {
        return formattedOutput ? formattedOutputFormatter : defaultFormatter;
    }

    public static Parser<Config> xmlParser() {
        return defaultParser;
    }

    @XmlRootElement(name = "config")
    public static final class ConfigBean {

        @XmlAttribute(name = "domain")
        public String domain;
        @XmlAttribute(name = "name")
        public String name;
        @XmlAttribute(name = "version")
        public String version;
        @XmlElement(name = "param")
        public ParamBean[] params;

        Config toId() {
            return Config.builder(domain != null ? domain : "", name != null ? name : "", version != null ? version : "")
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

    public static final class ParamBean {

        @XmlAttribute(name = "key")
        public String key;
        @XmlAttribute(name = "value")
        public String value;

        String getKeyOrEmpty() {
            return nullToEmpty(key);
        }

        String getValueOrEmpty() {
            return nullToEmpty(value);
        }

        static ParamBean of(Map.Entry<String, String> entry) {
            ParamBean item = new ParamBean();
            item.key = entry.getKey();
            item.value = entry.getValue();
            return item;
        }

        public static SortedMap<String, String> toSortedMap(ParamBean[] params) {
            return params != null
                    ? Stream.of(params).collect(Collectors.toMap(ParamBean::getKeyOrEmpty, ParamBean::getValueOrEmpty, (l, r) -> l, TreeMap::new))
                    : Collections.emptySortedMap();
        }

        public static ParamBean[] fromSortedMap(SortedMap<String, String> sortedMap) {
            return !sortedMap.isEmpty()
                    ? sortedMap.entrySet().stream().map(ParamBean::of).toArray(ParamBean[]::new)
                    : null;
        }
    }

    private static final Parser<Config> defaultParser;
    private static final Formatter<Config> defaultFormatter;
    private static final Formatter<Config> formattedOutputFormatter;

    static {
        defaultParser = asParser(Jaxb.Parser.of(ConfigBean.class).andThen(ConfigBean::toId));
        defaultFormatter = asFormatter(Jaxb.Formatter.of(ConfigBean.class).withFormatted(false).compose(ConfigBean::of));
        formattedOutputFormatter = asFormatter(Jaxb.Formatter.of(ConfigBean.class).withFormatted(true).compose(ConfigBean::of));
    }

    @MightBePromoted
    private static <T> Parser<T> asParser(TextParser<T> parser) {
        return Parser.of(IOFunction.unchecked(parser::parseChars)::apply);
    }

    @MightBePromoted
    private static <T> Formatter<T> asFormatter(TextFormatter<T> formatter) {
        return Formatter.of(IOFunction.unchecked(formatter::formatToString)::apply);
    }

    @MightBePromoted
    private static String nullToEmpty(String input) {
        return input != null ? input : "";
    }
}

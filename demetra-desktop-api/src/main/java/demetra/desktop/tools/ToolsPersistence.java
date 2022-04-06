/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.tools;

import demetra.timeseries.TsCollection;
import demetra.desktop.TsManager;
import demetra.desktop.components.parts.HasTsCollection;
import demetra.desktop.Config;
import demetra.desktop.DemetraOptions;
import demetra.desktop.Persistable;
import demetra.timeseries.Ts;
import demetra.timeseries.TsInformationType;
import demetra.timeseries.TsMoniker;
import demetra.desktop.util.XmlConfig;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import nbbrd.io.function.IOFunction;
import nbbrd.io.text.Formatter;
import nbbrd.io.text.Parser;
import nbbrd.io.xml.Xml;
import nbbrd.io.xml.bind.Jaxb;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Philippe Charles
 */
public final class ToolsPersistence {

    private ToolsPersistence() {
        // static class
    }

    private static <T> Optional<T> tryGet(Properties p, String key, Parser<T> parser, boolean escape) {
        String stringValue = p.getProperty(key);
        if (stringValue == null) {
            return Optional.empty();
        }
        if (escape) {
            stringValue = unescape(stringValue);
            if (stringValue == null) {
                return Optional.empty();
            }
        }
        return parser.parseValue(stringValue);
    }

    private static <T> boolean tryPut(Properties p, String key, Formatter<T> formatter, boolean escape, T value) {
        String stringValue = formatter.formatAsString(value);
        if (stringValue == null) {
            return false;
        }
        if (escape) {
            stringValue = escape(stringValue);
            if (stringValue == null) {
                return false;
            }
        }
        p.setProperty(key, stringValue);
        return true;
    }

    public static void writeTsCollection(@NonNull HasTsCollection view, @NonNull Properties p) {
        if (view instanceof Persistable) {
            Config config = ((Persistable) view).getConfig();
            tryPut(p, "config", XmlConfig.xmlFormatter(false)::format, true, config);
        }
        if (DemetraOptions.getDefault().isPersistToolsContent()) {
            Content content = new Content(
                    view.getTsCollection().getItems(),
                    view.getTsSelectionStream().collect(Collectors.toList()));
            tryPut(p, "content", IOFunction.unchecked(CONTENT_FORMATTER::formatToString)::apply, true, content);
        }
    }

    public static void readTsCollection(@NonNull HasTsCollection view, @NonNull Properties p) {
        if (DemetraOptions.getDefault().isPersistToolsContent()) {
            tryGet(p, "content", IOFunction.unchecked(CONTENT_PARSER::parseChars)::apply, true).ifPresent(o -> {
                view.setTsCollection(TsCollection.of(o.collection));
                view.getTsSelectionModel().clearSelection();
                o.collection
                        .stream()
                        .mapToInt(view.getTsCollection().getItems()::indexOf)
                        .forEach(i -> view.getTsSelectionModel().addSelectionInterval(i, i));
            });
        }
        if (view instanceof Persistable) {
            tryGet(p, "config", XmlConfig.xmlParser()::parse, true).ifPresent(((Persistable) view)::setConfig);
        }
    }

    private static final Xml.Formatter<Content> CONTENT_FORMATTER;
    private static final Xml.Parser<Content> CONTENT_PARSER;

    static {
        CONTENT_FORMATTER = Jaxb.Formatter.of(ContentBean.class).compose(Content::toBean);
        CONTENT_PARSER = Jaxb.Parser.of(ContentBean.class).andThen(Content::fromBean);
    }

    private static String escape(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String unescape(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    @lombok.AllArgsConstructor
    private static class Content {

        final List<Ts> collection;
        final List<Ts> selection;

        ContentBean toBean() {
            List<ContentItemBean> items = new ArrayList<>();
            for (Ts o : collection) {
                TsMoniker moniker = o.getMoniker();
                if (!moniker.isNull()) {
                    ContentItemBean bean = new ContentItemBean();
                    bean.name = o.getName();
                    bean.source = moniker.getSource();
                    bean.id = moniker.getId();
                    bean.selected = selection.contains(o);
                    items.add(bean);
                }
            }
            ContentBean result = new ContentBean();
            result.items = items.stream().toArray(ContentItemBean[]::new);
            return result;
        }

        static Content fromBean(ContentBean input) {
            Content result = new Content(new ArrayList<>(), new ArrayList<>());
            if (input.items != null) {
                for (ContentItemBean o : input.items) {
                    Ts ts = TsManager.getDefault()
                            .makeTs(TsMoniker.of(o.source, o.id), TsInformationType.Definition)
                            .toBuilder()
                            .name(o.name)
                            .build();
                    if (o.selected) {
                        result.selection.add(ts);
                    }
                    result.collection.add(ts);
                }
            }
            return result;
        }
    }

    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    static class ContentBean {

        ContentItemBean[] items;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    static class ContentItemBean {

        String source;
        String id;
        String name;
        boolean selected;
    }
}

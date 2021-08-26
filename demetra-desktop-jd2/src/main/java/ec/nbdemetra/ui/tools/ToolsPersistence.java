/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tools;

import demetra.bridge.TsConverter;
import demetra.timeseries.TsCollection;
import demetra.ui.TsManager;
import demetra.ui.components.parts.HasTsCollection;
import demetra.ui.Config;
import demetra.ui.DemetraOptions;
import demetra.ui.Persistable;
import ec.nbdemetra.ui.XmlConfig;
import ec.tss.Ts;
import ec.tss.TsMoniker;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.tsproviders.utils.IFormatter;
import ec.tss.tsproviders.utils.IParser;
import ec.tss.tsproviders.utils.Parsers;
import ec.tstoolkit.utilities.URLEncoder2;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Philippe Charles
 */
public final class ToolsPersistence {

    private ToolsPersistence() {
        // static class
    }

    private static <T> Optional<T> tryGet(Properties p, String key, IParser<T> parser, boolean escape) {
        CharSequence stringValue = p.getProperty(key);
        if (stringValue == null) {
            return Optional.empty();
        }
        if (escape) {
            stringValue = Decoder.INSTANCE.parse(stringValue);
            if (stringValue == null) {
                return Optional.empty();
            }
        }
        return parser.parseValue(stringValue);
    }

    private static <T> boolean tryPut(Properties p, String key, IFormatter<T> formatter, boolean escape, T value) {
        String stringValue = formatter.formatAsString(value);
        if (stringValue == null) {
            return false;
        }
        if (escape) {
            stringValue = Encoder.INSTANCE.formatAsString(stringValue);
            if (stringValue == null) {
                return false;
            }
        }
        p.setProperty(key, stringValue);
        return true;
    }

    public static void writeTsCollection(HasTsCollection view, Properties p) {
        if (view instanceof Persistable) {
            Config config = ((Persistable) view).getConfig();
            tryPut(p, "config", XmlConfig.xmlFormatter(false), true, config);
        }
        if (DemetraOptions.getDefault().isPersistToolsContent()) {
            Content content = new Content(
                    view.getTsCollection().stream().map(TsConverter::fromTs).collect(Collectors.toList()),
                    view.getTsSelectionStream().map(TsConverter::fromTs).collect(Collectors.toList()));
            tryPut(p, "content", CONTENT_FORMATTER, true, content);
        }
    }

    public static void readTsCollection(HasTsCollection view, Properties p) {
        if (DemetraOptions.getDefault().isPersistToolsContent()) {
            tryGet(p, "content", CONTENT_PARSER, true).ifPresent(o -> {
                List<demetra.timeseries.Ts> tmp = o.collection.stream().map(TsConverter::toTs).collect(Collectors.toList());
                view.setTsCollection(TsCollection.of(tmp));
                view.getTsSelectionModel().clearSelection();
                tmp
                        .stream()
                        .mapToInt(view.getTsCollection().getItems()::indexOf)
                        .forEach(i -> view.getTsSelectionModel().addSelectionInterval(i, i));
            });
        }
        if (view instanceof Persistable) {
            tryGet(p, "config", XmlConfig.xmlParser(), true).ifPresent(((Persistable) view)::setConfig);
        }
    }

    private static final IFormatter<Content> CONTENT_FORMATTER = Formatters.onJAXB(ContentBean.class, false).compose(Content::toBean);
    private static final IParser<Content> CONTENT_PARSER = Parsers.onJAXB(ContentBean.class).andThen(Content::fromBean);

    private static class Content {

        final List<Ts> collection;
        final List<Ts> selection;

        Content(List<Ts> collection, List<Ts> selection) {
            this.collection = collection;
            this.selection = selection;
        }

        ContentBean toBean() {
            List<ContentItemBean> items = new ArrayList<>();
            for (Ts o : collection) {
                TsMoniker moniker = o.getMoniker();
                if (!moniker.isAnonymous()) {
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
                    Ts ts = TsConverter.fromTs(
                            TsManager.getDefault()
                                    .makeTs(demetra.timeseries.TsMoniker.of(o.source, o.id), demetra.timeseries.TsInformationType.Definition)
                                    .toBuilder()
                                    .name(o.name)
                                    .build()
                    );
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

    private static class Encoder extends Formatters.Formatter<CharSequence> {

        static final Encoder INSTANCE = new Encoder();

        @Override
        public CharSequence format(CharSequence value) throws NullPointerException {
            return URLEncoder2.encode(value.toString(), StandardCharsets.UTF_8);
        }
    }

    private static class Decoder extends Parsers.FailSafeParser<CharSequence> {

        static final Decoder INSTANCE = new Decoder();

        @Override
        protected CharSequence doParse(CharSequence input) throws Exception {
            return URLDecoder.decode(input.toString(), StandardCharsets.UTF_8.name());
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.ui.tools;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import demetra.ui.TsManager;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.DemetraUI;
import ec.nbdemetra.ui.IConfigurable;
import ec.tss.Ts;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.tsproviders.utils.Formatters;
import ec.tss.tsproviders.utils.IFormatter;
import ec.tss.tsproviders.utils.IParser;
import ec.tss.tsproviders.utils.Parsers;
import ec.tstoolkit.utilities.URLEncoder2;
import ec.ui.interfaces.ITsCollectionView;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
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

    public static void writeTsCollection(ITsCollectionView view, Properties p) {
        if (view instanceof IConfigurable) {
            Config config = ((IConfigurable) view).getConfig();
            tryPut(p, "config", Config.xmlFormatter(false), true, config);
        }
        if (DemetraUI.getDefault().isPersistToolsContent()) {
            Content content = new Content(Lists.newArrayList(view.getTsCollection()), Arrays.asList(view.getSelection()));
            tryPut(p, "content", CONTENT_FORMATTER, true, content);
        }
    }

    public static void readTsCollection(ITsCollectionView view, Properties p) {
        if (DemetraUI.getDefault().isPersistToolsContent()) {
            tryGet(p, "content", CONTENT_PARSER, true).ifPresent(o -> {
                view.getTsCollection().append(o.collection);
                view.getTsCollection().load(TsInformationType.Data);
                view.setSelection(Iterables.toArray(o.selection, Ts.class));
            });
        }
        if (view instanceof IConfigurable) {
            tryGet(p, "config", Config.xmlParser(), true).ifPresent(o -> {
                ((IConfigurable) view).setConfig(o);
            });
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
            result.items = Iterables.toArray(items, ContentItemBean.class);
            return result;
        }

        static Content fromBean(ContentBean input) {
            Content result = new Content(new ArrayList<>(), new ArrayList<>());
            if (input.items != null) {
                for (ContentItemBean o : input.items) {
                    TsMoniker moniker = new TsMoniker(o.source, o.id);
                    Ts ts = TsManager.getDefault().lookupTs(o.name, moniker, TsInformationType.Definition);
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

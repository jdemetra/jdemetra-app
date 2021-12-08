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
package demetra.desktop;

import demetra.information.Explorable;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDocument;
import demetra.timeseries.TsInformationType;
import demetra.timeseries.TsMoniker;
import demetra.timeseries.TsProvider;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import nbbrd.service.ServiceProvider;

/**
 *
 * @author Jean Palate
 */
@ServiceProvider(TsProvider.class)
public final class TsDynamicProvider implements TsProvider {

    public static final String COMPONENT = "@component@", INFO = "@info";
    public static final String COMPOSITE = "@composite@";

    public static final String DYNAMIC = "DYNAMIC";

    public TsDynamicProvider() {
    }

    public static TsMoniker monikerOf(TsDocument doc, String id) {
        StringBuilder builder = new StringBuilder();
        builder.append(doc.getKey()).append('@').append(id);
        return TsMoniker.of(DYNAMIC, builder.toString());
    }

    private static final Map< UUID, WeakReference<TsDocument>> DOCUMENTS = new HashMap<>();
//    private static final Map< UUID, Set<String>> DOCITEMS = new HashMap<>();

    @Override
    public void clearCache() {
    }

    @Override
    public void close() {
    }

    @Override
    public TsCollection getTsCollection(TsMoniker moniker, TsInformationType type) throws IOException, IllegalArgumentException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private Ts invalidTs(TsMoniker moniker, String cause) {
        return Ts.builder()
                .moniker(moniker)
                .data(TsData.empty(cause))
                .build();
    }

    @Override
    public Ts getTs(TsMoniker moniker, TsInformationType type) throws IOException, IllegalArgumentException {
        synchronized (DOCUMENTS) {
            if (moniker.getSource().equals(DYNAMIC)) {
                String[] items = moniker.getId().split("@");
                if (items.length != 2) {
                    return invalidTs(moniker, "invalid id");
                }
                UUID uuid = UUID.fromString(items[0]);
                WeakReference<TsDocument> wdoc = DOCUMENTS.get(uuid);
                if (wdoc == null) {
                    return invalidTs(moniker, "closed document");
                }
                TsDocument doc = wdoc.get();
                if (doc == null) {
                    return invalidTs(moniker, "closed document");
                }
                Explorable result = doc.getResult();
                if (result == null) {
                    return invalidTs(moniker, "unprocessed document");
                }
                CompositeTs composite = CompositeTs.decode(items[1]);
                if (composite != null) {
                    return compositeTs(moniker, result, composite);
                } else {
                    return simpleTs(moniker, result, items[1]);
                }
            } else {
                return invalidTs(moniker, "invalid source");
            }
        }
    }

    @Override
    public String getSource() {
        return DYNAMIC;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

//    private static void updateItems(UUID uuid, String item) {
//        Set<String> ids = DOCITEMS.get(uuid);
//        if (ids == null) {
//            ids = new HashSet<>();
//            DOCITEMS.put(uuid, ids);
//        }
//        ids.add(item);
//    }
//
//    public static Set<String> itemsForDocument(TsDocument doc) {
//        Set<String> items = DOCITEMS.get(doc.getKey());
//        if (items == null) {
//            return Collections.emptySet();
//        } else {
//            return Collections.unmodifiableSet(items);
//        }
//    }
    public static void OnDocumentClosing(TsDocument doc) {
        if (doc == null) {
            return;
        }
        synchronized (DOCUMENTS) {
            UUID uuid = doc.getKey();
            DOCUMENTS.remove(uuid);
//            DOCITEMS.remove(uuid);
        }
    }

    public static void OnDocumentOpened(TsDocument doc) {
        if (doc == null) {
            return;
        }
        synchronized (DOCUMENTS) {
            UUID uuid = doc.getKey();
            DOCUMENTS.put(uuid, new WeakReference(doc));
        }
    }

    public static void OnDocumentChanged(TsDocument doc) {
        if (doc == null) {
            return;
        }
        TsManager.getDefault().notify(m -> m.getSource().equals(DYNAMIC) && m.getId().startsWith(doc.getKey().toString()));
    }

    private Ts simpleTs(TsMoniker moniker, Explorable result, String item) {
        TsData data = result.getData(item, TsData.class);
        if (data == null) {
            return invalidTs(moniker, "unsupported id");
        }
//                updateItems(uuid, items[1]);
        return Ts.builder()
                .moniker(moniker)
                .name(item)
                .data(data)
                .type(TsInformationType.All)
                .build();
    }

    private Ts compositeTs(TsMoniker moniker, Explorable result, CompositeTs composite) {

        Map<String, String> md = new HashMap<>();
        TsData data = makeCompositeData(composite, result, md);
        if (data == null) {
            return invalidTs(moniker, "unsupported id");
        }
//                updateItems(uuid, items[1]);
        return Ts.builder()
                .moniker(moniker)
                .name(composite.name)
                .data(data)
                .meta(md)
                .type(TsInformationType.All)
                .build();
    }

    private TsData makeCompositeData(CompositeTs item, Explorable source, Map<String, String> md) {
        TsData data = null;
        LocalDate beg = null, end = null;
        TsData b = null, n = null, f = null;
        if (item.back != null) {
            b = source.getData(item.back, TsData.class);
        }
        if (item.now != null) {
            n = source.getData(item.now, TsData.class);
            if (n != null) {
                beg = n.getStart().start().toLocalDate();
                end = n.getEnd().end().toLocalDate();
            }
        }
        if (item.fore != null) {
            f = source.getData(item.fore, TsData.class);
        }
        data = TsData.concatenate(b, n, f);
        if (beg != null) {
            md.put(Ts.BEG, beg.toString());
        } else {
            md.remove(Ts.BEG);
        }
        if (end != null) {
            md.put(Ts.END, end.toString());
        } else {
            md.remove(Ts.END);
        }
        return data;
    }

    @lombok.Value
    @lombok.Builder(builderClassName = "Builder")
    public static class CompositeTs {

        static CompositeTs decode(String str) {
            int cur = 0;
            int pos = str.indexOf('=');
            if (pos < 0) {
                return null;
            }
            Builder builder = CompositeTs.builder();
            if (pos > cur) {
                builder.name(str.substring(cur, pos));
            }
            cur = pos + 1;
            pos = str.indexOf(',', cur);
            if (pos < 0) {
                return null;
            } else if (pos > cur) {
                builder.back(str.substring(cur, pos));
            }
            cur = pos + 1;
            pos = str.indexOf(',', cur);
            if (pos < 0) {
                return null;
            } else if (pos > cur) {
                builder.now(str.substring(cur, pos));
            }
            cur = pos + 1;
            if (cur < str.length()) {
                builder.fore(str.substring(cur));
            }
            return builder.build();
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(name).append('=');
            if (back != null) {
                builder.append(back);
            }
            builder.append(',');
            if (now != null) {
                builder.append(now);
            }
            builder.append(',');
            if (fore != null) {
                builder.append(fore);
            }
            return builder.toString();
        }

        String name;
        String back, now, fore;
    }

}

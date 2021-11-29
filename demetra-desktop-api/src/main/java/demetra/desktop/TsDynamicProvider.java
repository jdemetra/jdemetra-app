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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private static final Map< UUID, Set<String>> DOCITEMS = new HashMap<>();

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
                TsData data = result.getData(items[1], TsData.class);
                if (data == null) {
                    return invalidTs(moniker, "unsupported id");
                }
                updateItems(uuid, items[1]);
                return Ts.builder()
                        .moniker(moniker)
                        .name(items[1])
                        .data(data)
                        .build();
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

    private static void updateItems(UUID uuid, String item) {
        Set<String> ids = DOCITEMS.get(uuid);
        if (ids == null) {
            ids = new HashSet<>();
            DOCITEMS.put(uuid, ids);
        }
        ids.add(item);
    }

    public static Set<String> itemsForDocument(TsDocument doc) {
        Set<String> items = DOCITEMS.get(doc.getKey());
        if (items == null) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(items);
        }
    }
    
    public static void OnDocumentClosing(TsDocument doc){
        synchronized (DOCUMENTS) {
            UUID uuid=doc.getKey();
            DOCUMENTS.remove(uuid);
            DOCITEMS.remove(uuid);
        }
    }

    public static void OnDocumentOpened(TsDocument doc){
        synchronized (DOCUMENTS) {
            UUID uuid=doc.getKey();
            DOCUMENTS.put(uuid, new WeakReference(doc));
        }
    }

    public static void OnDocumentChanged(TsDocument doc){
        synchronized (DOCUMENTS) {
            Set<String> items = itemsForDocument(doc);
            for (String s: items){
                TsMoniker moniker=monikerOf(doc, s);
                TsManager.getDefault().notify(moniker, other->other.equals(moniker));
            }
        }
    }

    static class CompositeTs {

        static CompositeTs decode(String str) {
            CompositeTs cmp = new CompositeTs();
            int cur = 0;
            int pos = str.indexOf('=');
            if (pos < 0) {
                return null;
            } else if (pos > cur) {
                cmp.name = str.substring(cur, pos);
            }
            cur = pos + 1;
            pos = str.indexOf(',', cur);
            if (pos < 0) {
                return null;
            } else if (pos > cur) {
                cmp.back = str.substring(cur, pos);
            }
            cur = pos + 1;
            pos = str.indexOf(',', cur);
            if (pos < 0) {
                return null;
            } else if (pos > cur) {
                cmp.now = str.substring(cur, pos);
            }
            cur = pos + 1;
            if (cur < str.length()) {
                cmp.fore = str.substring(cur);
            }
            return cmp;
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
        String back, now, fore;
        String name;
        TsMoniker moniker;
    }

}

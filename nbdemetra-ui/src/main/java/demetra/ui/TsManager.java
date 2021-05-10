/*
 * Copyright 2018 National Bank of Belgium
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
package demetra.ui;

import demetra.bridge.TsConverter;
import ec.nbdemetra.core.GlobalService;
import ec.tss.ITsProvider;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsEvent;
import ec.tss.TsFactory;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.IFileLoader;
import ec.tstoolkit.MetaData;
import ec.tstoolkit.design.NewObject;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.Files2;
import ec.util.various.swing.OnAnyThread;
import ec.util.various.swing.OnEDT;
import java.io.File;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collector;
import java.util.stream.Stream;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.swing.SwingUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@GlobalService
@ServiceProvider(service = TsManager.class)
public class TsManager implements NextTsManager {

    @NonNull
    public static TsManager getDefault() {
        return Lookup.getDefault().lookup(TsManager.class);
    }

    private final TsFactory delegate;
    private final ConcurrentLinkedQueue<TsEvent> events;
    private final List<UpdateListener> updateListeners;

    public TsManager() {
        this.delegate = TsFactory.instance;
        this.events = new ConcurrentLinkedQueue<>();
        this.updateListeners = new ArrayList<>();
        delegate.addObserver(this::onDelegateChange);
    }

    @OnAnyThread
    private void onDelegateChange(Observable o, Object arg) {
        if (arg instanceof TsEvent) {
            TsEvent evt = (TsEvent) arg;
            if (!(evt.isSeries() && evt.ts.getMoniker().isAnonymous())
                    && !(evt.isCollection() && evt.tscollection.getMoniker().isAnonymous())) {
                events.add(evt);
                SwingUtilities.invokeLater(this::notifyUpdateListeners);
            }
        }
    }

    @OnEDT
    private void notifyUpdateListeners() {
        TsEvent event;
        while ((event = events.poll()) != null) {
            if (event.isSeries()) {
                demetra.timeseries.TsMoniker tsMoniker = TsConverter.toTsMoniker(event.ts.getMoniker());
                updateListeners.forEach(listener -> listener.accept(this, tsMoniker));
            } else {
                TsCollection col = event.tscollection;
                demetra.timeseries.TsMoniker colMoniker = TsConverter.toTsMoniker(col.getMoniker());
                updateListeners.forEach(listener -> listener.accept(this, colMoniker));
                col.stream()
                        .map(Ts::getMoniker)
                        .map(TsConverter::toTsMoniker)
                        .forEach(tsMoniker -> updateListeners.forEach(listener -> listener.accept(this, tsMoniker)));
            }
        }
    }

    @NonNull
    @NewObject
    public TsCollection newTsCollection() {
        return delegate.createTsCollection();
    }

    @NonNull
    @NewObject
    public TsCollection newTsCollectionWithName(@Nullable String name) {
        return delegate.createTsCollection(name);
    }

    @NonNull
    public TsCollection lookupTsCollection(@Nullable String name, @NonNull TsMoniker moniker, @NonNull TsInformationType type) {
        return delegate.createTsCollection(name, moniker, type);
    }

    @Override
    public demetra.timeseries.@NonNull TsCollection lookupTsCollection2(@Nullable String name, demetra.timeseries.@NonNull TsMoniker moniker, demetra.timeseries.@NonNull TsInformationType type) {
        return TsConverter.toTsCollection(delegate.createTsCollection(name, TsConverter.fromTsMoniker(moniker), TsConverter.fromType(type)));
    }

    @NonNull
    @NewObject
    public Ts newTs(@Nullable String string, @Nullable MetaData md, @Nullable TsData tsdata) {
        return delegate.createTs(string, md, tsdata);
    }

    @NonNull
    public Ts lookupTs(@Nullable String name, @NonNull TsMoniker moniker, @NonNull TsInformationType type) {
        return delegate.createTs(name, moniker, type);
    }

    @NonNull
    @NewObject
    public Ts newTsWithName(@Nullable String name) {
        return delegate.createTs(name);
    }

    @Nullable
    public Ts lookupTs(@NonNull TsMoniker moniker) {
        return delegate.getTs(moniker);
    }

    @Override
    public demetra.timeseries.@Nullable Ts lookupTs2(demetra.timeseries.@NonNull TsMoniker moniker) {
        return TsConverter.toTs(delegate.getTs(TsConverter.fromTsMoniker(moniker)));
    }

    @NonNull
    public Collector<Ts, ?, TsCollection> getTsCollector() {
        return Collector.<Ts, List<Ts>, TsCollection>of(ArrayList::new, List::add, (l, r) -> {
            l.addAll(r);
            return l;
        }, o -> delegate.createTsCollection(null, null, null, o));
    }

    public boolean register(@NonNull ITsProvider provider) {
        return delegate.add(provider);
    }

    public void unregister(@NonNull ITsProvider provider) {
        delegate.remove(provider.getSource());
    }

    @Override
    public void close() {
        delegate.dispose();
    }

    @Override
    public void addUpdateListener(UpdateListener listener) {
        updateListeners.add(listener);
    }

    @Override
    public void removeUpdateListener(UpdateListener listener) {
        updateListeners.remove(listener);
    }

    @NonNull
    public <T extends ITsProvider> Optional<T> lookup(@NonNull Class<T> clazz, @NonNull String providerName) {
        ITsProvider result = delegate.getProvider(providerName);
        return clazz.isInstance(result) ? Optional.of(clazz.cast(result)) : Optional.empty();
    }

    @NonNull
    public <T extends ITsProvider> Optional<T> lookup(@NonNull Class<T> clazz, @NonNull DataSource dataSource) {
        return lookup(clazz, dataSource.getProviderName());
    }

    @NonNull
    public <T extends ITsProvider> Optional<T> lookup(@NonNull Class<T> clazz, @NonNull DataSet dataSet) {
        return lookup(clazz, dataSet.getDataSource());
    }

    @NonNull
    public <T extends ITsProvider> Optional<T> lookup(@NonNull Class<T> clazz, @NonNull TsMoniker moniker) {
        String providerName = moniker.getSource();
        return providerName != null ? lookup(clazz, providerName) : Optional.empty();
    }

    @NonNull
    public Stream<ITsProvider> all() {
        return asList().stream();
    }

    @NonNull
    private List<ITsProvider> asList() {
        final String[] providers = delegate.getProviders();
        return new AbstractList<ITsProvider>() {
            @Override
            public ITsProvider get(int index) {
                return delegate.getProvider(providers[index]);
            }

            @Override
            public int size() {
                return providers.length;
            }
        };
    }

    @NonNull
    public Optional<File> tryGetFile(@NonNull DataSource dataSource) {
        Optional<IFileLoader> loader = lookup(IFileLoader.class, dataSource.getProviderName());
        if (loader.isPresent()) {
            File file = loader.get().decodeBean(dataSource).getFile();
            File realFile = Files2.getAbsoluteFile(loader.get().getPaths(), file);
            return Optional.ofNullable(realFile);
        }
        return Optional.empty();
    }

    @NonNull
    public Optional<TsCollection> getTsCollection(@NonNull DataSource dataSource, @NonNull TsInformationType type) {
        IDataSourceProvider provider = lookup(IDataSourceProvider.class, dataSource).orElse(null);
        if (provider == null) {
            return Optional.empty();
        }
        String name = provider.getDisplayName(dataSource);
        TsMoniker moniker = provider.toMoniker(dataSource);
        return Optional.of(delegate.createTsCollection(name, moniker, type));
    }

    @NonNull
    public Optional<TsCollection> getTsCollection(@NonNull DataSet dataSet, @NonNull TsInformationType type) {
        IDataSourceProvider provider = lookup(IDataSourceProvider.class, dataSet).orElse(null);
        if (provider == null) {
            return Optional.empty();
        }
        String name = provider.getDisplayName(dataSet);
        TsMoniker moniker = provider.toMoniker(dataSet);
        switch (dataSet.getKind()) {
            case COLLECTION:
                return Optional.of(delegate.createTsCollection(name, moniker, type));
            case DUMMY:
                return Optional.of(delegate.createTsCollection(name));
            case SERIES:
                TsCollection result = delegate.createTsCollection();
                result.quietAdd(delegate.createTs(name, moniker, type));
                return Optional.of(result);
        }
        throw new RuntimeException("Not implemented");
    }

    @NonNull
    public Optional<Ts> getTs(@NonNull DataSet dataSet, @NonNull TsInformationType type) {
        IDataSourceProvider provider = lookup(IDataSourceProvider.class, dataSet).orElse(null);
        if (provider == null) {
            return Optional.empty();
        }
        String name = provider.getDisplayName(dataSet);
        TsMoniker moniker = provider.toMoniker(dataSet);
        switch (dataSet.getKind()) {
            case SERIES:
                Ts ts = delegate.createTs(name, moniker, type);
                return Optional.of(ts);
        }
        throw new RuntimeException("Not implemented");
    }

    public void loadAsync(demetra.timeseries.@NonNull Ts ts, demetra.timeseries.@NonNull TsInformationType type) {
        TsConverter.fromTs(ts).query(TsConverter.fromType(type));
    }

    public demetra.timeseries.@NonNull Ts load(demetra.timeseries.@NonNull Ts ts, demetra.timeseries.@NonNull TsInformationType type) {
        Ts tmp = TsConverter.fromTs(ts);
        tmp.load(TsConverter.fromType(type));
        return TsConverter.toTs(tmp);
    }

    public void load(@NonNull Ts ts, @NonNull TsInformationType type) {
        ts.load(type);
    }
}

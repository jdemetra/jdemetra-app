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
import java.util.EventListener;
import java.util.List;
import java.util.Observable;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collector;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.SwingUtilities;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@GlobalService
@ServiceProvider(service = TsManager.class)
public class TsManager implements AutoCloseable {

    public interface UpdateListener extends EventListener {

        void accept(@Nonnull TsMoniker moniker);
    }

    @Nonnull
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
            events.add((TsEvent) arg);
            SwingUtilities.invokeLater(this::notifyUpdateListeners);
        }
    }

    @OnEDT
    private void notifyUpdateListeners() {
        TsEvent event;
        while ((event = events.poll()) != null) {
            if (event.isSeries()) {
                TsMoniker tsMoniker = event.ts.getMoniker();
                updateListeners.stream().forEach(o -> o.accept(tsMoniker));
            } else {
                TsCollection col = event.tscollection;
                updateListeners.stream().forEach(o -> {
                    o.accept(col.getMoniker());
                    col.forEach(ts -> o.accept(ts.getMoniker()));
                });
            }
        }
    }

    @Nonnull
    @NewObject
    public TsCollection newTsCollection() {
        return delegate.createTsCollection();
    }

    @Nonnull
    @NewObject
    public TsCollection newTsCollectionWithName(@Nullable String name) {
        return delegate.createTsCollection(name);
    }

    @Nonnull
    public TsCollection lookupTsCollection(@Nullable String name, @Nonnull TsMoniker moniker, @Nonnull TsInformationType type) {
        return delegate.createTsCollection(name, moniker, type);
    }

    @Nonnull
    @NewObject
    public Ts newTs(@Nullable String string, @Nullable MetaData md, @Nullable TsData tsdata) {
        return delegate.createTs(string, md, tsdata);
    }

    @Nonnull
    public Ts lookupTs(@Nullable String name, @Nonnull TsMoniker moniker, @Nonnull TsInformationType type) {
        return delegate.createTs(name, moniker, type);
    }

    @Nonnull
    @NewObject
    public Ts newTsWithName(@Nullable String name) {
        return delegate.createTs(name);
    }

    @Nullable
    public Ts lookupTs(@Nonnull TsMoniker moniker) {
        return delegate.getTs(moniker);
    }

    @Nonnull
    public Collector<Ts, ?, TsCollection> getTsCollector() {
        return Collector.<Ts, List<Ts>, TsCollection>of(ArrayList::new, List::add, (l, r) -> {
            l.addAll(r);
            return l;
        }, o -> delegate.createTsCollection(null, null, null, o));
    }

    public boolean register(@Nonnull ITsProvider provider) {
        return delegate.add(provider);
    }

    public void unregister(@Nonnull ITsProvider provider) {
        delegate.remove(provider.getSource());
    }

    @Override
    public void close() {
        delegate.dispose();
    }

    @OnEDT
    public void addWeakUpdateListener(@Nonnull UpdateListener listener) {
        addUpdateListener(WeakListeners.create(TsManager.UpdateListener.class, listener, this));
    }

    @OnEDT
    public void addUpdateListener(@Nonnull UpdateListener listener) {
        updateListeners.add(listener);
    }

    @OnEDT
    public void removeUpdateListener(@Nonnull UpdateListener listener) {
        updateListeners.remove(listener);
    }

    @Nonnull
    public <T extends ITsProvider> Optional<T> lookup(@Nonnull Class<T> clazz, @Nonnull String providerName) {
        ITsProvider result = delegate.getProvider(providerName);
        return clazz.isInstance(result) ? Optional.of(clazz.cast(result)) : Optional.empty();
    }

    @Nonnull
    public <T extends ITsProvider> Optional<T> lookup(@Nonnull Class<T> clazz, @Nonnull DataSource dataSource) {
        return lookup(clazz, dataSource.getProviderName());
    }

    @Nonnull
    public <T extends ITsProvider> Optional<T> lookup(@Nonnull Class<T> clazz, @Nonnull DataSet dataSet) {
        return lookup(clazz, dataSet.getDataSource());
    }

    @Nonnull
    public <T extends ITsProvider> Optional<T> lookup(@Nonnull Class<T> clazz, @Nonnull TsMoniker moniker) {
        String providerName = moniker.getSource();
        return providerName != null ? lookup(clazz, providerName) : Optional.empty();
    }

    @Nonnull
    public Stream<ITsProvider> all() {
        return asList().stream();
    }

    @Nonnull
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

    @Nonnull
    public Optional<File> tryGetFile(@Nonnull DataSource dataSource) {
        Optional<IFileLoader> loader = lookup(IFileLoader.class, dataSource.getProviderName());
        if (loader.isPresent()) {
            File file = loader.get().decodeBean(dataSource).getFile();
            File realFile = Files2.getAbsoluteFile(loader.get().getPaths(), file);
            return Optional.ofNullable(realFile);
        }
        return Optional.empty();
    }

    @Nonnull
    public Optional<TsCollection> getTsCollection(@Nonnull DataSource dataSource, @Nonnull TsInformationType type) {
        IDataSourceProvider provider = lookup(IDataSourceProvider.class, dataSource).orElse(null);
        if (provider == null) {
            return Optional.empty();
        }
        String name = provider.getDisplayName(dataSource);
        TsMoniker moniker = provider.toMoniker(dataSource);
        return Optional.of(delegate.createTsCollection(name, moniker, type));
    }

    @Nonnull
    public Optional<TsCollection> getTsCollection(@Nonnull DataSet dataSet, @Nonnull TsInformationType type) {
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

    @Nonnull
    public Optional<Ts> getTs(@Nonnull DataSet dataSet, @Nonnull TsInformationType type) {
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
}

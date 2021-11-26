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
package demetra.desktop;

import demetra.desktop.design.GlobalService;
import demetra.desktop.util.LazyGlobalService;
import demetra.timeseries.*;
import demetra.tsprovider.DataSource;
import demetra.tsprovider.DataSourceFactory;
import demetra.tsprovider.DataSourceListener;
import demetra.tsprovider.DataSourceProvider;
import ec.util.various.swing.OnAnyThread;
import ec.util.various.swing.OnEDT;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.util.WeakListeners;

import javax.swing.*;
import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 *
 * @author Philippe Charles
 */
@GlobalService
public final class TsManager implements DataSourceFactory, Closeable {

    @NonNull
    public static TsManager getDefault() {
        return LazyGlobalService.get(TsManager.class, TsManager::new);
    }

    private final ConcurrentMap<String, TsProvider> providers;
    private final ConcurrentLinkedQueue<TsEvent> events;
    private final List<TsListener> updateListeners;
    private final DataSourceListener listener;
    private final ExecutorService executor;

    private TsManager() {
        this.providers = new ConcurrentHashMap<>();
        this.events = new ConcurrentLinkedQueue<>();
        this.updateListeners = new ArrayList<>();
        this.listener = new DataSourceListenerImpl();
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void close() {
        executor.shutdown();
    }

    @OnAnyThread
    public boolean register(@NonNull TsProvider provider) {
        providers.put(provider.getSource(), provider);
        if (provider instanceof DataSourceProvider) {
            ((DataSourceProvider) provider).addDataSourceListener(listener);
        }
        return true;
    }

    @OnAnyThread
    public boolean unregister(@NonNull TsProvider provider) {
        TsProvider removedProvider = providers.remove(provider.getSource());
        if (removedProvider instanceof DataSourceProvider) {
            ((DataSourceProvider) removedProvider).removeDataSourceListener(listener);
        }
        return true;
    }

    @OnEDT
    public void addWeakListener(@NonNull TsListener listener) {
        addListener(WeakListeners.create(TsListener.class, listener, this));
    }

    @OnEDT
    public void addListener(@NonNull TsListener listener) {
        updateListeners.add(listener);
    }

    @OnEDT
    public void removeListener(@NonNull TsListener listener) {
        updateListeners.remove(listener);
    }

    @Override
    public Optional<TsProvider> getProvider(String name) {
        return Optional.ofNullable(providers.get(name));
    }

    @OnEDT
    public void loadAsync(@NonNull Ts ts, @NonNull TsInformationType info, @NonNull Consumer<? super Ts> onLoaded) {
        executor.execute(() -> {
            Ts loaded = makeTs(ts.getMoniker(), info);
            SwingUtilities.invokeLater(() -> onLoaded.accept(loaded));
        });
    }

    @OnEDT
    public void loadAsync(@NonNull TsCollection col, @NonNull TsInformationType info, @NonNull Consumer<? super TsCollection> onLoaded) {
        executor.execute(() -> {
            if (col.getMoniker().isProvided()) {
                TsCollection loaded = makeTsCollection(col.getMoniker(), info);
                SwingUtilities.invokeLater(() -> onLoaded.accept(loaded));
            }else{
                // One by one
                TsCollection loaded = col.getItems().stream().map(s->makeTs(s.getMoniker(), info)).collect(TsCollection.toTsCollection());
                SwingUtilities.invokeLater(() -> onLoaded.accept(loaded));
            }
        });
    }

    @Override
    public Stream<TsProvider> getProviders() {
        return providers.values().stream();
    }

    @OnAnyThread
    private void notify(TsMoniker moniker, Predicate<TsMoniker> related) {
        events.add(new TsEvent(this, moniker, related));
        SwingUtilities.invokeLater(this::notifyUpdateListeners);
    }

    @OnEDT
    private void notifyUpdateListeners() {
        TsEvent event;
        while ((event = events.poll()) != null) {
            for (TsListener o : updateListeners) {
                o.tsUpdated(event);
            }
        }
    }

    private final class DataSourceListenerImpl implements DataSourceListener {

        @Override
        public void opened(DataSource ds) {
        }

        @Override
        public void closed(DataSource ds) {
        }

        @OnAnyThread
        @Override
        public void changed(DataSource ds) {
            Optional<DataSourceProvider> provider = getProvider(DataSourceProvider.class, ds);
            if (provider.isPresent()) {
                TsMoniker dataSourceMoniker = provider.get().toMoniker(ds);
                TsManager.this.notify(dataSourceMoniker, dataSetMoniker -> isRelated(provider.get(), ds, dataSetMoniker));
            }
        }

        private boolean isRelated(DataSourceProvider provider, DataSource dataSource, TsMoniker dataSetMoniker) {
            return dataSetMoniker.getSource().equals(provider.getSource())
                    && provider
                            .toDataSet(dataSetMoniker)
                            .filter(dataSet -> dataSet.getDataSource().equals(dataSource))
                            .isPresent();
        }

        @Override
        public void allClosed(String string) {
        }
    }
}

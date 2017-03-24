/*
 * Copyright 2015 National Bank of Belgium
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
package ec.nbdemetra.ui.demo;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import ec.tss.ITsProvider;
import ec.tss.TsAsyncMode;
import ec.tss.TsCollection;
import ec.tss.TsCollectionInformation;
import ec.tss.TsFactory;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.HasDataDisplayName;
import ec.tss.tsproviders.HasDataHierarchy;
import ec.tss.tsproviders.HasDataMoniker;
import ec.tss.tsproviders.HasDataSourceList;
import ec.tss.tsproviders.IDataSourceListener;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.cursor.HasTsCursor;
import ec.tss.tsproviders.cursor.TsCursor;
import ec.tss.tsproviders.cursor.TsCursorAsFiller;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.OptionalTsData;
import ec.tss.tsproviders.utils.Params;
import ec.tss.tsproviders.utils.TsFillerAsProvider;
import ec.tstoolkit.timeseries.TsPeriodSelector;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@lombok.extern.slf4j.Slf4j
@ServiceProvider(service = ITsProvider.class)
public final class FakeTsProvider implements IDataSourceProvider {

    private enum DataType {

        NORMAL, FAILING_DATA, FAILING_META, UPDATING;

    }

    private static final String NAME = "Fake";

    private static final IParam<DataSource, DataType> TYPE_PARAM = Params.onEnum(DataType.NORMAL, "type");
    private static final IParam<DataSet, Integer> INDEX_PARAM = Params.onInteger(-1, "idx");

    private final FakeSupport fakeSupport;
    private final HasDataSourceList listSupport;
    private final HasDataMoniker monikerSupport;
    private final ITsProvider tsSupport;

    private final ConcurrentLinkedDeque<Runnable> onTick;
    private final Service updater;

    public FakeTsProvider() {
        this.fakeSupport = new FakeSupport();
        this.listSupport = HasDataSourceList.of(NAME, log, createDataSources());
        this.monikerSupport = HasDataMoniker.usingUri(NAME);
        this.tsSupport = TsFillerAsProvider.of(NAME, TsAsyncMode.Dynamic, TsCursorAsFiller.of(log, fakeSupport, monikerSupport, fakeSupport));

        final TsMoniker updatingMoniker = toMoniker(getDataSources().stream().filter(o -> TYPE_PARAM.get(o).equals(DataType.UPDATING)).findFirst().get());

        this.onTick = new ConcurrentLinkedDeque<>();
        this.updater = new AbstractExecutionThreadService() {

            private final TsCollection hardRef = TsFactory.instance.createTsCollection("", updatingMoniker, TsInformationType.None);

            @Override
            protected Executor executor() {
                return Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setPriority(Thread.MIN_PRIORITY).build());
            }

            @Override
            protected void run() throws Exception {
                while (isRunning()) {
                    onTick.forEach(Runnable::run);
                    queryTsCollection(hardRef.getMoniker(), TsInformationType.All);
                    TimeUnit.SECONDS.sleep(1);
                }
            }
        };
        updater.startAsync();
    }

    //<editor-fold defaultstate="collapsed" desc="ITsProvider">
    @Override
    public String getSource() {
        return tsSupport.getSource();
    }

    @Override
    public void clearCache() {
        tsSupport.clearCache();
    }

    @Override
    public void dispose() {
        updater.stopAsync();
        tsSupport.dispose();
    }

    @Override
    public boolean get(TsCollectionInformation info) {
        return tsSupport.get(info);
    }

    @Override
    public boolean get(TsInformation info) {
        return tsSupport.get(info);
    }

    @Override
    public TsAsyncMode getAsyncMode() {
        return tsSupport.getAsyncMode();
    }

    @Override
    public boolean queryTs(TsMoniker ts, TsInformationType type) {
        return tsSupport.queryTs(ts, type);
    }

    @Override
    public boolean queryTsCollection(TsMoniker collection, TsInformationType info) {
        return tsSupport.queryTsCollection(collection, info);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="IDataSourceProvider">
    @Override
    public void reload(DataSource dataSource) throws IllegalArgumentException {
        listSupport.reload(dataSource);
    }

    @Override
    public List<DataSource> getDataSources() {
        return listSupport.getDataSources();
    }

    @Override
    public List<DataSet> children(DataSource dataSource) throws IllegalArgumentException, IOException {
        return fakeSupport.children(dataSource);
    }

    @Override
    public List<DataSet> children(DataSet parent) throws IllegalArgumentException, IOException {
        return fakeSupport.children(parent);
    }

    @Override
    public void addDataSourceListener(IDataSourceListener listener) {
        listSupport.addDataSourceListener(listener);
    }

    @Override
    public void removeDataSourceListener(IDataSourceListener listener) {
        listSupport.removeDataSourceListener(listener);
    }

    @Override
    public TsMoniker toMoniker(DataSource dataSource) throws IllegalArgumentException {
        return monikerSupport.toMoniker(dataSource);
    }

    @Override
    public TsMoniker toMoniker(DataSet dataSet) throws IllegalArgumentException {
        return monikerSupport.toMoniker(dataSet);
    }

    @Override
    public DataSet toDataSet(TsMoniker moniker) throws IllegalArgumentException {
        return monikerSupport.toDataSet(moniker);
    }

    @Override
    public DataSource toDataSource(TsMoniker moniker) throws IllegalArgumentException {
        return monikerSupport.toDataSource(moniker);
    }

    @Override
    public String getDisplayName(DataSource dataSource) throws IllegalArgumentException {
        return fakeSupport.getDisplayName(dataSource);
    }

    @Override
    public String getDisplayName(DataSet dataSet) throws IllegalArgumentException {
        return fakeSupport.getDisplayName(dataSet);
    }

    @Override
    public String getDisplayNodeName(DataSet dataSet) throws IllegalArgumentException {
        return fakeSupport.getDisplayNodeName(dataSet);
    }
    //</editor-fold>

    public void addTickListener(Runnable r) {
        onTick.add(r);
    }

    private static List<DataSource> createDataSources() {
        List<DataSource> result = new ArrayList<>();
        DataSource.Builder builder = DataSource.builder(NAME, "");
        for (DataType o : DataType.values()) {
            result.add(builder.put(TYPE_PARAM, o).build());
        }
        return result;
    }

    private static final class FakeSupport implements HasTsCursor, HasDataHierarchy, HasDataDisplayName {

        private final FakeConfig config = new FakeConfig();
        private final FakeData data = new FakeData(config);

        @Override
        public String getDisplayName(DataSource dataSource) throws IllegalArgumentException {
            switch (TYPE_PARAM.get(dataSource)) {
                case NORMAL:
                    return "Normal async";
                case FAILING_META:
                    return "Exception on meta";
                case FAILING_DATA:
                    return "Exception on data";
                case UPDATING:
                    return "Auto updating";
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public String getDisplayName(DataSet dataSet) throws IllegalArgumentException {
            return getDisplayName(dataSet.getDataSource()) + System.lineSeparator() + getDisplayNodeName(dataSet);
        }

        @Override
        public String getDisplayNodeName(DataSet dataSet) throws IllegalArgumentException {
            int obsCount = config.getObsCount(TYPE_PARAM.get(dataSet.getDataSource()), INDEX_PARAM.get(dataSet));
            return obsCount >= 0 ? ("#" + obsCount) : "no data";
        }

        @Override
        public List<DataSet> children(DataSource dataSource) throws IllegalArgumentException, IOException {
            List<DataSet> result = new ArrayList<>();
            try (TsCursor<DataSet> cursor = data.getData(TYPE_PARAM.get(dataSource), TsInformationType.Definition).transform(getDataSetFunc(dataSource))) {
                while (cursor.nextSeries()) {
                    result.add(cursor.getSeriesId());
                }
            }
            return result;
        }

        @Override
        public List<DataSet> children(DataSet parent) throws IllegalArgumentException, IOException {
            throw new IllegalArgumentException("Invalid hierarchy");
        }

        @Override
        public TsCursor<DataSet> getData(DataSource dataSource, TsInformationType type) throws IllegalArgumentException, IOException {
            return data.getData(TYPE_PARAM.get(dataSource), type).transform(getDataSetFunc(dataSource));
        }

        @Override
        public TsCursor<DataSet> getData(DataSet dataSet, TsInformationType type) throws IllegalArgumentException, IOException {
            if (!dataSet.getKind().equals(DataSet.Kind.SERIES)) {
                throw new IllegalArgumentException("Invalid hierarchy");
            }
            int seriesIndex = INDEX_PARAM.get(dataSet);
            return data.getData(TYPE_PARAM.get(dataSet.getDataSource()), type).filter(o -> o == seriesIndex).transform(o -> dataSet);
        }

        private static Function<Integer, DataSet> getDataSetFunc(DataSource dataSource) {
            DataSet.Builder b = DataSet.builder(dataSource, DataSet.Kind.SERIES);
            return o -> b.put(INDEX_PARAM, o).build();
        }
    }

    private static final class FakeData {

        private final FakeConfig config;
        private final Function<Integer, OptionalTsData> normalData;
        private final Function<Integer, OptionalTsData> updatingData;

        public FakeData(FakeConfig config) {
            this.config = config;
            normalData = createData(config, DataType.NORMAL)::get;
            updatingData = shiftingValues(createData(config, DataType.UPDATING));
        }

        private Iterator<Integer> seriesIndexIterator(DataType dt) {
            return IntStream.range(0, config.getSeriesCount(dt)).iterator();
        }

        private Function<Integer, OptionalTsData> getDataFunc(Function<Integer, OptionalTsData> delegate, TsInformationType type) {
            return type.encompass(TsInformationType.Data)
                    ? delegate
                    : o -> OptionalTsData.absent("Data not requested");
        }

        private Function<Integer, Map<String, String>> getMetaFunc(DataType dt, TsInformationType type) {
            return type.encompass(TsInformationType.MetaData)
                    ? o -> ImmutableMap.of("Type", dt.name(), "Index", String.valueOf(o))
                    : o -> Collections.emptyMap();
        }

        public TsCursor<Integer> getData(DataType dt, TsInformationType type) throws IOException {
            Iterator<Integer> iter = seriesIndexIterator(dt);
            switch (dt) {
                case NORMAL:
                    sleep(type);
                    return TsCursor.from(iter, getDataFunc(normalData, type), getMetaFunc(dt, type));
                case FAILING_META:
                    sleep(type);
                    if (type.encompass(TsInformationType.MetaData)) {
                        throw new IOException("Cannot load meta");
                    }
                    return TsCursor.from(iter);
                case FAILING_DATA:
                    sleep(type);
                    if (type.encompass(TsInformationType.Data)) {
                        throw new IOException("Cannot load data");
                    }
                    return TsCursor.from(iter);
                case UPDATING:
                    return TsCursor.from(iter, getDataFunc(updatingData, type), getMetaFunc(dt, type));
                default:
                    throw new IllegalArgumentException("Invalid data type");
            }
        }

        private static void sleep(TsInformationType type) {
            try {
                TimeUnit.MILLISECONDS.sleep(type.needsData() ? 2000 : 150);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }

        private static List<OptionalTsData> createData(FakeConfig config, DataType dt) {
            return IntStream.range(0, config.getSeriesCount(dt))
                    .mapToObj(o -> createData(config, dt, o))
                    .collect(Collectors.toList());
        }

        private static OptionalTsData createData(FakeConfig config, DataType dt, int seriesIndex) {
            int obsCount = config.getObsCount(dt, seriesIndex);
            return obsCount >= 0
                    ? OptionalTsData.present(TsData.random(TsFrequency.Monthly).select(selector(obsCount)))
                    : OptionalTsData.absent("No data");
        }

        private static TsPeriodSelector selector(int obsCount) {
            TsPeriodSelector result = new TsPeriodSelector();
            result.first(obsCount);
            return result;
        }

        private static Function<Integer, OptionalTsData> shiftingValues(List<OptionalTsData> list) {
            return o -> {
                OptionalTsData result = shiftValues(list.get(o));
                list.set(o, result);
                return result;
            };
        }

        private static OptionalTsData shiftValues(OptionalTsData input) {
            if (input.isPresent()) {
                TsData data = input.get();
                shiftValues(data);
                return OptionalTsData.present(data);
            }
            return input;
        }

        private static void shiftValues(TsData data) {
            if (data.getObsCount() > 1) {
                double[] values = data.internalStorage();
                double first = values[0];
                System.arraycopy(values, 1, values, 0, values.length - 1);
                values[values.length - 1] = first;
            }
        }
    }

    private static final class FakeConfig {

        private final EnumMap<DataType, int[]> obsCounts;

        public FakeConfig() {
            this.obsCounts = new EnumMap<>(DataType.class);
            obsCounts.put(DataType.NORMAL, new int[]{-1, 0, 1, 24, 60, 120});
            obsCounts.put(DataType.FAILING_META, new int[]{24});
            obsCounts.put(DataType.FAILING_DATA, new int[]{24});
            obsCounts.put(DataType.UPDATING, new int[]{-1, 0, 24, 60, 120});
        }

        public int getSeriesCount(DataType type) {
            return obsCounts.get(type).length;
        }

        public int getObsCount(DataType type, int seriesIndex) {
            return obsCounts.get(type)[seriesIndex];
        }
    }
}

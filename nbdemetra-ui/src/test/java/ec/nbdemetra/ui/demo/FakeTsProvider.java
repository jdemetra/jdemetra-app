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

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import ec.tss.TsAsyncMode;
import ec.tss.TsCollectionInformation;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.HasDataDisplayName;
import ec.tss.tsproviders.HasDataHierarchy;
import ec.tss.tsproviders.utils.AbstractDataSourceProvider;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.Params;
import ec.tstoolkit.timeseries.simplets.TsData;
import internal.RandomTsBuilder;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Philippe Charles
 */
public final class FakeTsProvider extends AbstractDataSourceProvider<Void> {

    private enum DataType {

        NORMAL, FAILING_DATA, FAILING_META, UPDATING;

    }

    private static final String NAME = "Fake";

    private static final IParam<DataSource, DataType> TYPE_PARAM = Params.onEnum(DataType.NORMAL, "type");
    private static final IParam<DataSet, Integer> INDEX_PARAM = Params.onInteger(-1, "idx");

    private final HasDataHierarchy dataHierarchy;
    private final HasDataDisplayName dataDisplayName;
    private final RandomTsBuilder tsBuilder;
    private final List<TsData> normalData;
    private final ConcurrentLinkedDeque<Runnable> onTick;
    private final Service updater;

    public FakeTsProvider() {
        super(LoggerFactory.getLogger(FakeTsProvider.class), NAME, TsAsyncMode.Dynamic);

        this.dataHierarchy = new FakeDataHierarchy();
        this.dataDisplayName = new FakeDataDisplayName();
        this.tsBuilder = new RandomTsBuilder();
        this.normalData = createNormalData(tsBuilder, getSeriesCount(DataType.NORMAL));

        Map<DataType, DataSource> dataSources = createDataSources();
        dataSources.values().forEach(support::open);

        this.onTick = new ConcurrentLinkedDeque<>();
        this.updater = new AbstractExecutionThreadService() {
            final TsMoniker updatingMoniker = toMoniker(dataSources.get(DataType.UPDATING));

            @Override
            protected Executor executor() {
                return Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setPriority(Thread.MIN_PRIORITY).build());
            }

            @Override
            protected void run() throws Exception {
                while (isRunning()) {
                    onTick.forEach(Runnable::run);
                    queryTsCollection(updatingMoniker, TsInformationType.All);
                    TimeUnit.SECONDS.sleep(3);
                }
            }
        }.startAsync();
    }

    public void addTickListener(Runnable r) {
        onTick.add(r);
    }

    @Override
    protected Void loadFromDataSource(DataSource key) throws Exception {
        return null;
    }

    @Override
    protected void fillCollection(TsCollectionInformation info, DataSource dataSource) throws IOException {
        sleep(info.type);
        switch (TYPE_PARAM.get(dataSource)) {
            case NORMAL:
                DataSet.Builder b1 = DataSet.builder(dataSource, DataSet.Kind.SERIES);
                for (int i = 0; i < getSeriesCount(DataType.NORMAL); i++) {
                    INDEX_PARAM.set(b1, i);
                    TsInformation item = newTsInformation(b1.build(), info.type);
                    if (info.type.needsData()) {
                        item.data = normalData.get(i);
                    }
                    info.items.add(item);
                }
                break;
            case FAILING_META:
                throw new IOException("Cannot load datasource");
            case FAILING_DATA:
                if (info.type.needsData()) {
                    throw new IOException("Cannot load datasource");
                }
                DataSet.Builder b2 = DataSet.builder(dataSource, DataSet.Kind.SERIES);
                for (int i = 0; i < getSeriesCount(DataType.FAILING_DATA); i++) {
                    INDEX_PARAM.set(b2, i);
                    info.items.add(newTsInformation(b2.build(), info.type));
                }
                break;
            case UPDATING:
                DataSet.Builder b3 = DataSet.builder(dataSource, DataSet.Kind.SERIES);
                for (int i = 0; i < getSeriesCount(DataType.UPDATING); i++) {
                    INDEX_PARAM.set(b3, i);
                    TsInformation item = newTsInformation(b3.build(), info.type);
                    if (info.type.needsData()) {
                        item.data = createData(tsBuilder, DataType.UPDATING, i);
                    }
                    info.items.add(item);
                }
                break;
            default:
                throw new IOException("???");
        }
    }

    @Override
    protected void fillCollection(TsCollectionInformation info, DataSet dataSet) throws IOException {
        throw new IOException("Not supported yet.");
    }

    @Override
    protected void fillSeries(TsInformation info, DataSet dataSet) throws IOException {
        sleep(info.type);
        fillSeries(info, TYPE_PARAM.get(dataSet.getDataSource()), INDEX_PARAM.get(dataSet));
    }

    private void fillSeries(TsInformation info, DataType type, int seriesIndex) throws IOException {
        info.name = getSeriesName(type, seriesIndex);
        switch (type) {
            case NORMAL:
                if (info.type.needsData()) {
                    info.data = normalData.get(seriesIndex);
                }
                break;
            case FAILING_META:
                throw new IOException("Cannot load dataset");
            case FAILING_DATA:
                if (info.type.needsData()) {
                    throw new IOException("Cannot load dataset");
                }
                break;
            case UPDATING:
                if (info.type.needsData()) {
                    info.data = createData(tsBuilder, DataType.UPDATING, seriesIndex);
                }
                break;
            default:
                throw new IOException("???");
        }
    }

    @Override
    public List<DataSet> children(DataSource dataSource) throws IllegalArgumentException, IOException {
        return dataHierarchy.children(dataSource);
    }

    @Override
    public List<DataSet> children(DataSet parent) throws IllegalArgumentException, IOException {
        return dataHierarchy.children(parent);
    }

    @Override
    public String getDisplayName(DataSource dataSource) throws IllegalArgumentException {
        return dataDisplayName.getDisplayName(dataSource);
    }

    @Override
    public String getDisplayName(DataSet dataSet) throws IllegalArgumentException {
        return dataDisplayName.getDisplayName(dataSet);
    }

    @Override
    public String getDisplayNodeName(DataSet dataSet) throws IllegalArgumentException {
        return dataDisplayName.getDisplayNodeName(dataSet);
    }

    @Override
    public void dispose() {
        super.dispose();
        updater.stopAsync();
    }

    private static void sleep(TsInformationType type) {
        try {
            TimeUnit.MILLISECONDS.sleep(type.needsData() ? 2000 : 150);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static List<TsData> createNormalData(RandomTsBuilder tsBuilder, int seriesCount) {
        return IntStream.range(0, seriesCount)
                .mapToObj(o -> createData(tsBuilder, DataType.NORMAL, o))
                .collect(Collectors.toList());
    }

    private static TsData createData(RandomTsBuilder tsBuilder, DataType dataType, int seriesIndex) {
        return tsBuilder
                .withObsCount(getObsCount(dataType, seriesIndex))
                .build().data;
    }

    private static SortedMap<DataType, DataSource> createDataSources() {
        SortedMap<DataType, DataSource> result = new TreeMap<>();
        DataSource.Builder builder = DataSource.builder(NAME, "");
        for (DataType o : DataType.values()) {
            TYPE_PARAM.set(builder, o);
            result.put(o, builder.build());
        }
        return result;
    }

    private static int getSeriesCount(DataType type) {
        return 3;
    }

    private static int getObsCount(DataType type, int seriesIndex) {
        switch (seriesIndex) {
            case 0:
                return 24;
            case 1:
                return 12 * 5;
            case 2:
                return 12 * 10;
            default:
                throw new RuntimeException();
        }
    }

    private static String getSeriesName(DataType type, int seriesIndex) {
        return "={" + type + "#" + seriesIndex + "}=";
    }

    private static final class FakeDataHierarchy implements HasDataHierarchy {

        @Override
        public List<DataSet> children(DataSource dataSource) throws IllegalArgumentException, IOException {
            DataSet.Builder builder = DataSet.builder(dataSource, DataSet.Kind.SERIES);
            return IntStream.range(0, getSeriesCount(TYPE_PARAM.get(dataSource)))
                    .mapToObj(o -> {
                        INDEX_PARAM.set(builder, o);
                        return builder.build();
                    })
                    .collect(Collectors.toList());
        }

        @Override
        public List<DataSet> children(DataSet parent) throws IllegalArgumentException, IOException {
            throw new IllegalArgumentException();
        }
    }

    private static final class FakeDataDisplayName implements HasDataDisplayName {

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
                    return "Auto updating data";
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public String getDisplayName(DataSet dataSet) throws IllegalArgumentException {
            return getDisplayName(dataSet.getDataSource()) + " " + getDisplayNodeName(dataSet);
        }

        @Override
        public String getDisplayNodeName(DataSet dataSet) throws IllegalArgumentException {
            return "#" + INDEX_PARAM.get(dataSet);
        }
    }
}

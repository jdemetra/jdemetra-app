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
import ec.tss.Ts;
import ec.tss.TsAsyncMode;
import ec.tss.TsCollection;
import ec.tss.TsCollectionInformation;
import ec.tss.TsInformation;
import ec.tss.TsInformationType;
import ec.tss.TsMoniker;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.utils.AbstractDataSourceProvider;
import ec.tss.tsproviders.utils.IParam;
import ec.tss.tsproviders.utils.OptionalTsData;
import ec.tss.tsproviders.utils.Params;
import ec.ui.DemoUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Philippe Charles
 */
public final class FakeTsProvider extends AbstractDataSourceProvider<Void> {

    private enum DataType {

        NORMAL, FAILING_DATA, FAILING_META, UPDATING;

    }

    private final IParam<DataSource, DataType> dataTypeParam = Params.onEnum(DataType.NORMAL, "type");
    private final IParam<DataSet, Integer> indexParam = Params.onInteger(0, "index");
    private final DemoUtils.RandomTsCollectionBuilder dataBuilder;
    private final TsCollection normalData;
    private final Service service;

    public FakeTsProvider() {
        super(LoggerFactory.getLogger(FakeTsProvider.class), "Fake", TsAsyncMode.Once);

        dataBuilder = new DemoUtils.RandomTsCollectionBuilder();
        normalData = dataBuilder.build();

        DataSource.Builder builder = DataSource.builder(providerName, "");
        for (DataType o : DataType.values()) {
            dataTypeParam.set(builder, o);
            support.open(builder.build());
        }

        dataTypeParam.set(builder, DataType.UPDATING);
        final TsMoniker updatingMoniker = toMoniker(builder.build());

        this.service = new AbstractExecutionThreadService() {

            @Override
            protected Executor executor() {
                return Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setDaemon(true).setPriority(Thread.MIN_PRIORITY).build());
            }

            @Override
            protected void run() throws Exception {
                while (isRunning()) {
                    queryTsCollection(updatingMoniker, TsInformationType.All);
                    TimeUnit.SECONDS.sleep(3);
                }
            }
        }.startAsync();
    }

    @Override
    protected Void loadFromDataSource(DataSource key) throws Exception {
        return null;
    }

    private void sleep(TsInformationType type) {
        try {
            TimeUnit.MILLISECONDS.sleep(type.needsData() ? 2000 : 150);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    protected void fillCollection(TsCollectionInformation info, DataSource dataSource) throws IOException {
        sleep(info.type);
        switch (dataTypeParam.get(dataSource)) {
            case NORMAL:
                DataSet.Builder builder = DataSet.builder(dataSource, DataSet.Kind.SERIES);
                for (int i = 0; i < normalData.getCount(); i++) {
                    indexParam.set(builder, i);
                    TsInformation tsInfo = newTsInformation(builder.build(), info.type);
                    if (info.type.needsData()) {
                        support.fillSeries(tsInfo, OptionalTsData.present(0, 0, normalData.get(i).getTsData()), false);
                        info.type = TsInformationType.All; // FIXME: TsInformationType.Data fails ???
                    }
                    info.items.add(tsInfo);
                }
                break;
            case FAILING_META:
                throw new IOException("Cannot load datasource");
            case FAILING_DATA:
                if (info.type.needsData()) {
                    throw new IOException("Cannot load datasource");
                }
                DataSet.Builder b2 = DataSet.builder(dataSource, DataSet.Kind.SERIES);
                for (int i = 0; i < normalData.getCount(); i++) {
                    indexParam.set(b2, i);
                    TsInformation tsInfo = newTsInformation(b2.build(), info.type);
                    info.items.add(tsInfo);
                }
                break;
            case UPDATING:
                DataSet.Builder b3 = DataSet.builder(dataSource, DataSet.Kind.SERIES);
                TsCollection data = dataBuilder.build();
                for (int i = 0; i < data.getCount(); i++) {
                    indexParam.set(b3, i);
                    TsInformation tsInfo = newTsInformation(b3.build(), info.type);
                    if (info.type.needsData()) {
                        support.fillSeries(tsInfo, OptionalTsData.present(0, 0, data.get(i).getTsData()), false);
                        info.type = TsInformationType.All;
                    }
                    info.items.add(tsInfo);
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
        switch (dataTypeParam.get(dataSet.getDataSource())) {
            case NORMAL:
                if (info.type.needsData()) {
                    Ts ts = normalData.get(indexParam.get(dataSet));
                    support.fillSeries(info, OptionalTsData.present(0, 0, ts.getTsData()), false);
                    info.type = TsInformationType.All;
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
                    Ts ts = dataBuilder.build().get(0);
                    support.fillSeries(info, OptionalTsData.present(0, 0, ts.getTsData()), false);
                    info.type = TsInformationType.All;
                }
                break;
            default:
                throw new IOException("???");
        }
    }

    @Override
    public String getDisplayName() {
        return getSource();
    }

    @Override
    public List<DataSet> children(DataSource dataSource) throws IllegalArgumentException, IOException {
        switch (dataTypeParam.get(dataSource)) {
            case NORMAL:
            case FAILING_META:
            case FAILING_DATA:
            case UPDATING:
                List<DataSet> result = new ArrayList<>();
                DataSet.Builder builder = DataSet.builder(dataSource, DataSet.Kind.SERIES);
                for (int i = 0; i < normalData.getCount(); i++) {
                    indexParam.set(builder, i);
                    result.add(builder.build());
                }
                return result;
            default:
                throw new IOException("???");
        }
    }

    @Override
    public List<DataSet> children(DataSet parent) throws IllegalArgumentException, IOException {
        throw new IOException("Not supported yet.");
    }

    @Override
    public String getDisplayName(DataSource dataSource) throws IllegalArgumentException {
        switch (dataTypeParam.get(dataSource)) {
            case NORMAL:
                return "Normal async";
            case FAILING_META:
                return "Exception on meta";
            case FAILING_DATA:
                return "Exception on data";
            case UPDATING:
                return "Auto updating data";
            default:
                return "???";
        }
    }

    @Override
    public String getDisplayName(DataSet dataSet) throws IllegalArgumentException {
        return getDisplayName(dataSet.getDataSource()) + " " + getDisplayNodeName(dataSet);
    }

    @Override
    public String getDisplayNodeName(DataSet dataSet) throws IllegalArgumentException {
        return normalData.get(indexParam.get(dataSet)).getName();
    }

    @Override
    public void dispose() {
        super.dispose();
        service.stopAsync();
    }
}

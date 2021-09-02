/*
 * Copyright 2013 National Bank of Belgium
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
package internal.ui;

import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsInformationType;
import demetra.tsprovider.DataSet;
import demetra.tsprovider.DataSourceProvider;
import demetra.ui.IconManager;
import demetra.ui.TsActionsOpenSpi;
import demetra.ui.TsManager;
import demetra.ui.components.parts.HasChart;
import demetra.ui.components.parts.HasTsCollection.TsUpdateMode;
import demetra.ui.util.NbComponents;
import ec.nbdemetra.ui.tools.ChartTopComponent;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;

import java.util.Optional;

/**
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider
public final class SimpleChartTsAction implements TsActionsOpenSpi {

    @Override
    public String getName() {
        return "SimpleChartTsAction";
    }

    @Override
    public String getDisplayName() {
        return "Simple chart";
    }

    @Override
    public void open(Ts ts) {
        String topComponentName = getTopComponentName(ts);
        NbComponents.findTopComponentByNameAndClass(topComponentName, ChartTopComponent.class)
                .orElseGet(() -> createComponent(topComponentName, ts))
                .requestActive();
    }

    private String getTopComponentName(Ts ts) {
        return getName() + ts.getMoniker();
    }

    private static ChartTopComponent createComponent(String topComponentName, Ts ts) {
        ChartTopComponent result = new ChartTopComponent();
        result.setName(topComponentName);
        result.setDisplayName(getDisplayName(ts));
        result.setIcon(IconManager.getDefault().getImage(ts.getMoniker()));

        TsCollection col = TsCollection.of(ts);

        result.getChart().setTsCollection(col);
        result.getChart().setTsUpdateMode(TsUpdateMode.None);
        result.getChart().setLegendVisible(false);
        result.getChart().setTitle(ts.getName());
        result.getChart().setLinesThickness(HasChart.LinesThickness.Thick);
        result.open();

        TsManager.getDefault().loadAsync(col, TsInformationType.All, result.getChart()::replaceTsCollection);
        return result;
    }

    private static String getDisplayName(Ts ts) {
        Optional<DataSourceProvider> provider = TsManager.getDefault().getProvider(DataSourceProvider.class, ts.getMoniker());
        if (provider.isPresent()) {
            Optional<DataSet> dataSet = provider.get().toDataSet(ts.getMoniker());
            if (dataSet.isPresent()) {
                return provider.get().getDisplayNodeName(dataSet.get());
            }
        }
        return ts.getName();
    }
}

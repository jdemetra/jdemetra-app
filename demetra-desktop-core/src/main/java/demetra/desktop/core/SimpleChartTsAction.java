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
package demetra.desktop.core;

import demetra.desktop.TsManager;
import demetra.desktop.components.parts.HasChart;
import demetra.desktop.components.parts.HasTsCollection.TsUpdateMode;
import demetra.desktop.tsproviders.DataSourceManager;
import demetra.desktop.util.NbComponents;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsInformationType;
import demetra.tsprovider.DataSet;
import demetra.tsprovider.DataSourceProvider;
import demetra.desktop.core.tools.JTsChartTopComponent;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;

import java.beans.BeanInfo;
import java.util.Optional;
import demetra.desktop.TsActionOpenSpi;

/**
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider
public final class SimpleChartTsAction implements TsActionOpenSpi {

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
        NbComponents.findTopComponentByNameAndClass(topComponentName, JTsChartTopComponent.class)
                .orElseGet(() -> createComponent(topComponentName, ts))
                .requestActive();
    }

    private String getTopComponentName(Ts ts) {
        return getName() + ts.getMoniker();
    }

    private static JTsChartTopComponent createComponent(String topComponentName, Ts ts) {
        JTsChartTopComponent result = new JTsChartTopComponent();
        result.setName(topComponentName);
        result.setDisplayName(getDisplayName(ts));
        result.setIcon(DataSourceManager.get().getImage(ts.getMoniker(), BeanInfo.ICON_COLOR_16x16, false));

        TsCollection col = TsCollection.of(ts);

        result.getChart().setTsCollection(col);
        result.getChart().setTsUpdateMode(TsUpdateMode.None);
        result.getChart().setLegendVisible(false);
        result.getChart().setTitle(ts.getName());
        result.getChart().setLinesThickness(HasChart.LinesThickness.Thick);
        result.open();

        TsManager.get().loadAsync(col, TsInformationType.All, result.getChart()::replaceTsCollection);
        return result;
    }

    private static String getDisplayName(Ts ts) {
        Optional<DataSourceProvider> provider = TsManager.get().getProvider(DataSourceProvider.class, ts.getMoniker());
        if (provider.isPresent()) {
            Optional<DataSet> dataSet = provider.get().toDataSet(ts.getMoniker());
            if (dataSet.isPresent()) {
                return provider.get().getDisplayNodeName(dataSet.get());
            }
        }
        return ts.getName();
    }
}

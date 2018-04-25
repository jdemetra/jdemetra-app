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
package ec.nbdemetra.ui.tsaction;

import ec.nbdemetra.ui.NbComponents;
import ec.nbdemetra.ui.ns.AbstractNamedService;
import ec.nbdemetra.ui.tools.ChartTopComponent;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.tss.Ts;
import ec.tss.TsInformationType;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.IDataSourceProvider;
import ec.tss.tsproviders.TsProviders;
import ec.ui.interfaces.ITsChart;
import ec.ui.interfaces.ITsCollectionView.TsUpdateMode;
import java.beans.BeanInfo;
import java.util.Optional;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = ITsAction.class)
public class SimpleChartTsAction extends AbstractNamedService implements ITsAction {

    public static final String NAME = "SimpleChartTsAction";

    public SimpleChartTsAction() {
        super(ITsAction.class, NAME);
    }

    @Override
    public String getDisplayName() {
        return "Simple chart";
    }

    @Override
    public void open(Ts ts) {
        ts.query(TsInformationType.All);
        String name = NAME + ts.getMoniker().toString();
        ChartTopComponent c = NbComponents.findTopComponentByNameAndClass(name, ChartTopComponent.class);
        if (c == null) {
            c = new ChartTopComponent();
            c.setName(name);

            Optional<IDataSourceProvider> provider = TsProviders.lookup(IDataSourceProvider.class, ts.getMoniker()).toJavaUtil();
            if (provider.isPresent()) {
                DataSet dataSet = provider.get().toDataSet(ts.getMoniker());
                if (dataSet != null) {
                    c.setIcon(DataSourceProviderBuddySupport.getDefault().getIcon(ts.getMoniker(), BeanInfo.ICON_COLOR_16x16, false).orElse(null));
                    c.setDisplayName(provider.get().getDisplayNodeName(dataSet));
                }
            } else {
                c.setDisplayName(ts.getName());
            }

            c.getChart().getTsCollection().add(ts);
            c.getChart().setTsUpdateMode(TsUpdateMode.None);
            c.getChart().setLegendVisible(false);
            c.getChart().setTitle(ts.getName());
            c.getChart().setLinesThickness(ITsChart.LinesThickness.Thick);
            c.open();
        }
        c.requestActive();
    }
}

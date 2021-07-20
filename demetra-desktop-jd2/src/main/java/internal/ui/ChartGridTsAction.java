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
import demetra.ui.components.parts.HasChart.LinesThickness;
import demetra.ui.components.parts.HasTsCollection.TsUpdateMode;
import ec.nbdemetra.ui.MonikerUI;
import demetra.ui.util.NbComponents;
import ec.nbdemetra.ui.tools.ChartTopComponent;
import ec.nbdemetra.ui.tools.GridTopComponent;
import ec.nbdemetra.ui.tsproviders.DataSourceProviderBuddySupport;
import ec.tss.tsproviders.utils.MultiLineNameUtil;
import demetra.ui.components.JTsGrid;
import java.awt.Image;
import java.beans.BeanInfo;
import javax.swing.Icon;
import org.netbeans.core.spi.multiview.*;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import demetra.ui.TsManager;
import demetra.ui.TsActionsOpenSpi;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = TsActionsOpenSpi.class)
public final class ChartGridTsAction implements TsActionsOpenSpi {

    public static final String NAME = "ChartGridTsAction";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getDisplayName() {
        return "Chart & grid";
    }

    @Override
    public void open(Ts ts) {
        String name = getName() + ts.getMoniker().toString();
        TopComponent c = NbComponents.findTopComponentByName(name);
        if (c == null) {
            MultiViewDescription[] descriptions = {new ChartTab(ts), new GridTab(ts)};
            c = MultiViewFactory.createMultiView(descriptions, descriptions[0], null);
            c.setName(name);
            c.setIcon(DataSourceProviderBuddySupport.getDefault().getIcon(ts.getMoniker(), BeanInfo.ICON_COLOR_16x16, false).orElse(null));
            applyText(ts.getName(), c);
            c.open();
        }
        c.requestActive();
    }

    private void applyText(String text, TopComponent c) {
        if (text.isEmpty()) {
            c.setDisplayName(" ");
            c.setToolTipText(null);
        } else if (text.startsWith("<html>")) {
            c.setDisplayName(text);
            c.setToolTipText(text);
        } else {
            c.setDisplayName(MultiLineNameUtil.lastWithMax(text, 40));
            c.setToolTipText(MultiLineNameUtil.toHtml(text));
        }
    }

    @lombok.AllArgsConstructor
    private static final class ChartTab implements MultiViewDescription {

        private final Ts ts;

        @Override
        public int getPersistenceType() {
            return TopComponent.PERSISTENCE_NEVER;
        }

        @Override
        public String getDisplayName() {
            return "Chart";
        }

        @Override
        public Image getIcon() {
            Icon icon = MonikerUI.getDefault().getIcon(ts.getMoniker());
            return icon != null ? ImageUtilities.icon2Image(icon) : null;
        }

        @Override
        public HelpCtx getHelpCtx() {
            return null;
        }

        @Override
        public String preferredID() {
            return "Chart";
        }

        @Override
        public MultiViewElement createElement() {
            TsCollection col = TsCollection.of(ts);
            ChartTopComponent result = new ChartTopComponent();
            result.getChart().setTsCollection(col);
            result.getChart().setTsUpdateMode(TsUpdateMode.None);
            result.getChart().setLegendVisible(true);
            result.getChart().setTitleVisible(false);
            result.getChart().setLinesThickness(LinesThickness.Thick);
            TsManager.getDefault().loadAsync(col, demetra.timeseries.TsInformationType.All, result.getChart()::replaceTsCollection);
            return result;
        }
    }

    @lombok.AllArgsConstructor
    private static final class GridTab implements MultiViewDescription {

        private final Ts ts;

        @Override
        public int getPersistenceType() {
            return TopComponent.PERSISTENCE_NEVER;
        }

        @Override
        public String getDisplayName() {
            return "Grid";
        }

        @Override
        public Image getIcon() {
            Icon icon = MonikerUI.getDefault().getIcon(ts.getMoniker());
            return icon != null ? ImageUtilities.icon2Image(icon) : null;
        }

        @Override
        public HelpCtx getHelpCtx() {
            return null;
        }

        @Override
        public String preferredID() {
            return "Grid";
        }

        @Override
        public MultiViewElement createElement() {
            TsCollection col = TsCollection.of(ts);
            GridTopComponent result = new GridTopComponent();
            result.getGrid().setTsCollection(col);
            result.getGrid().setTsUpdateMode(TsUpdateMode.None);
            result.getGrid().setMode(JTsGrid.Mode.SINGLETS);
            TsManager.getDefault().loadAsync(col, demetra.timeseries.TsInformationType.All, result.getGrid()::replaceTsCollection);
            return result;
        }
    }
}

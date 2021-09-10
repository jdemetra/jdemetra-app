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

import demetra.desktop.TsActionsOpenSpi;
import demetra.desktop.TsManager;
import demetra.desktop.components.JTsGrid;
import demetra.desktop.components.parts.HasChart.LinesThickness;
import demetra.desktop.components.parts.HasTsCollection.TsUpdateMode;
import demetra.desktop.tsproviders.DataSourceProviderBuddySupport;
import demetra.desktop.util.NbComponents;
import demetra.timeseries.Ts;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsInformationType;
import demetra.tsprovider.util.MultiLineNameUtil;
import demetra.desktop.core.tools.JTsChartTopComponent;
import demetra.desktop.core.tools.JTsGridTopComponent;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.util.HelpCtx;
import org.openide.windows.TopComponent;

import java.awt.*;
import java.beans.BeanInfo;

/**
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider
public final class ChartGridTsAction implements TsActionsOpenSpi {

    @Override
    public String getName() {
        return "ChartGridTsAction";
    }

    @Override
    public String getDisplayName() {
        return "Chart & grid";
    }

    @Override
    public void open(Ts ts) {
        String topComponentName = getTopComponentName(ts);
        NbComponents.findTopComponentByName(topComponentName)
                .orElseGet(() -> createComponent(topComponentName, ts))
                .requestActive();
    }

    private String getTopComponentName(Ts ts) {
        return getName() + ts.getMoniker();
    }

    private static TopComponent createComponent(String topComponentName, Ts ts) {
        MultiViewDescription[] descriptions = {new ChartTab(ts), new GridTab(ts)};
        TopComponent c = MultiViewFactory.createMultiView(descriptions, descriptions[0], null);
        c.setName(topComponentName);
        c.setIcon(DataSourceProviderBuddySupport.getDefault().getImage(ts.getMoniker(), BeanInfo.ICON_COLOR_16x16, false));
        applyText(ts.getName(), c);
        c.open();
        return c;
    }

    private static void applyText(String text, TopComponent c) {
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
            return DataSourceProviderBuddySupport.getDefault().getImage(ts.getMoniker(), BeanInfo.ICON_COLOR_16x16, false);
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
            JTsChartTopComponent result = new JTsChartTopComponent();
            result.getChart().setTsCollection(col);
            result.getChart().setTsUpdateMode(TsUpdateMode.None);
            result.getChart().setLegendVisible(true);
            result.getChart().setTitleVisible(false);
            result.getChart().setLinesThickness(LinesThickness.Thick);
            TsManager.getDefault().loadAsync(col, TsInformationType.All, result.getChart()::replaceTsCollection);
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
            return DataSourceProviderBuddySupport.getDefault().getImage(ts.getMoniker(), BeanInfo.ICON_COLOR_16x16, false);
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
            JTsGridTopComponent result = new JTsGridTopComponent();
            result.getGrid().setTsCollection(col);
            result.getGrid().setTsUpdateMode(TsUpdateMode.None);
            result.getGrid().setMode(JTsGrid.Mode.SINGLETS);
            TsManager.getDefault().loadAsync(col, TsInformationType.All, result.getGrid()::replaceTsCollection);
            return result;
        }
    }
}

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
package ec.ui.view.tsprocessing;

import demetra.bridge.TsConverter;
import demetra.timeseries.TsCollection;
import demetra.ui.components.HasTsCollection.TsUpdateMode;
import ec.tss.Ts;
import ec.tss.html.HtmlUtil;
import ec.tss.html.IHtmlElement;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.IPool;
import ec.tstoolkit.utilities.Pools;
import ec.ui.AHtmlView;
import demetra.ui.components.JTsChart;
import demetra.ui.components.JTsGrid;
import demetra.ui.components.JTsGrid.Mode;
import demetra.ui.components.JTsGrowthChart;
import ec.ui.interfaces.IDisposable;
import ec.ui.html.JHtmlView;
import ec.ui.view.SpectralView;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 *
 * @author Jean Palate
 */
public final class TsViewToolkit implements ITsViewToolkit {

    private static final TsViewToolkit INSTANCE = new TsViewToolkit();

    private final IPool<JTsChart> chartPool;
    private final IPool<JTsGrowthChart> growthchartPool;
    private final IPool<JTsGrid> gridPool;
    private final IPool<AHtmlView> htmlPool;

    private TsViewToolkit() {
        this.chartPool = Pools.on(new ChartFactory(), 10);
        this.growthchartPool = Pools.on(new GrowthChartFactory(), 10);
        this.gridPool = Pools.on(new GridFactory(), 10);
        this.htmlPool = Pools.on(new HtmlFactory(), 10);
    }

    /**
     * @return the instance_
     */
    public static TsViewToolkit getInstance() {
        return INSTANCE;
    }

    @Override
    public JComponent getGrid(Iterable<Ts> series) {
        JTsGrid result = gridPool.getOrCreate();
        result.setTsUpdateMode(TsUpdateMode.None);
        result.setMode(Mode.MULTIPLETS);
        result.setTsCollection(toTsCollection(series));

        return new JDisposable(result) {
            @Override
            public void dispose() {
                gridPool.recycle(result);
            }
        };
    }

    @Override
    public JComponent getGrid(Ts series) {
        JTsGrid result = gridPool.getOrCreate();
        result.setTsUpdateMode(TsUpdateMode.None);
        result.setMode(Mode.SINGLETS);
        result.setTsCollection(TsCollection.builder().data(TsConverter.toTs(series)).build());

        return new JDisposable(result) {
            @Override
            public void dispose() {
                gridPool.recycle(result);
            }
        };
    }

    @Override
    public JComponent getChart(Iterable<Ts> series) {
        final JTsChart result = chartPool.getOrCreate();
        result.setTsUpdateMode(TsUpdateMode.None);
        result.setTsCollection(toTsCollection(series));

        return new JDisposable(result) {
            @Override
            public void dispose() {
                chartPool.recycle(result);
            }
        };
    }

    @Override
    public JComponent getGrowthChart(Iterable<Ts> series) {
        final JTsGrowthChart result = growthchartPool.getOrCreate();
        result.setTsUpdateMode(TsUpdateMode.None);
        result.setTsCollection(toTsCollection(series));

        return new JDisposable(result) {
            @Override
            public void dispose() {
                growthchartPool.recycle(result);
            }
        };
    }

    @Override
    public JComponent getHtmlViewer(IHtmlElement html) {
        final AHtmlView result = htmlPool.getOrCreate();
        result.loadContent(HtmlUtil.toString(html));

        return new JDisposable(result) {
            @Override
            public void dispose() {
                htmlPool.recycle(result);
            }
        };
    }

    public JComponent getSpectralView(TsData s, boolean wn) {
        if (s == null) {
            return null;
        }
        SpectralView spectrum = new SpectralView();
        spectrum.set(s, wn);
        return spectrum;
    }

    @Override
    public JComponent getMessageViewer(String msg) {
        JLabel result = new JLabel();
        result.setHorizontalAlignment(SwingConstants.CENTER);
        result.setFont(result.getFont().deriveFont(result.getFont().getSize2D() * 3 / 2));
        result.setText("<html><center>" + msg);
        return result;
    }

    static abstract class JDisposable extends JComponent implements IDisposable {

        JDisposable(Component c) {
            setLayout(new BorderLayout());
            add(c, BorderLayout.CENTER);
        }
    }

    private static class ChartFactory implements IPool.Factory<JTsChart> {

        @Override
        public JTsChart create() {
            return new JTsChart();
        }

        @Override
        public void reset(JTsChart o) {
        }

        @Override
        public void destroy(JTsChart o) {
        }
    }

    private static class GrowthChartFactory implements IPool.Factory<JTsGrowthChart> {

        @Override
        public JTsGrowthChart create() {
            return new JTsGrowthChart();
        }

        @Override
        public void reset(JTsGrowthChart o) {
        }

        @Override
        public void destroy(JTsGrowthChart o) {
        }
    }

    private static class GridFactory implements IPool.Factory<JTsGrid> {

        @Override
        public JTsGrid create() {
            return new JTsGrid();
        }

        @Override
        public void reset(JTsGrid o) {
        }

        @Override
        public void destroy(JTsGrid o) {
        }
    }

    private static class HtmlFactory implements IPool.Factory<AHtmlView> {

        @Override
        public AHtmlView create() {
            return new JHtmlView();
        }

        @Override
        public void reset(AHtmlView o) {
        }

        @Override
        public void destroy(AHtmlView o) {
        }
    }

    private static TsCollection toTsCollection(Iterable<Ts> list) {
        TsCollection.Builder col = TsCollection.builder();
        for (Ts ts : list) {
            col.data(TsConverter.toTs(ts));
        }
        return col.build();
    }
}

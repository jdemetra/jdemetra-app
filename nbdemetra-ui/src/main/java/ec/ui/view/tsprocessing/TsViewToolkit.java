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

import ec.nbdemetra.ui.ComponentFactory;
import ec.tss.Ts;
import ec.tss.html.HtmlUtil;
import ec.tss.html.IHtmlElement;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.tstoolkit.utilities.IPool;
import ec.tstoolkit.utilities.Pools;
import ec.ui.AHtmlView;
import ec.ui.ATsChart;
import ec.ui.ATsGrid;
import ec.ui.ATsGrowthChart;
import ec.ui.interfaces.IDisposable;
import ec.ui.interfaces.ITsCollectionView.TsUpdateMode;
import ec.ui.interfaces.ITsGrid.Mode;
import ec.ui.view.SpectralView;
import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.text.html.StyleSheet;

/**
 *
 * @author Jean Palate
 */
public final class TsViewToolkit implements ITsViewToolkit {

    private static final TsViewToolkit INSTANCE = new TsViewToolkit();
    // 
    private final IPool<ATsChart> chartPool;
    private final IPool<ATsGrowthChart> growthchartPool;
    private final IPool<ATsGrid> gridPool;
    private final IPool<AHtmlView> htmlPool;
    private StyleSheet styleSheet;

    private TsViewToolkit() {
        this.chartPool = Pools.on(new ChartFactory(), 10);
        this.growthchartPool = Pools.on(new GrowthChartFactory(), 10);
        this.gridPool = Pools.on(new GridFactory(), 10);
        this.htmlPool = Pools.on(new HtmlFactory(), 10);
        this.styleSheet = createStyleSheet(13, 13, 12, 12, 11, true);
    }

    /**
     * @return the instance_
     */
    public static TsViewToolkit getInstance() {
        return INSTANCE;
    }

    @Override
    public JComponent getGrid(Iterable<Ts> series) {
        final ATsGrid result = gridPool.getOrCreate();
        result.setTsUpdateMode(TsUpdateMode.None);
        result.setMode(Mode.MULTIPLETS);
        result.getTsCollection().replace(series);

        return new JDisposable(result) {
            @Override
            public void dispose() {
                gridPool.recycle(result);
            }
        };
    }

    @Override
    public JComponent getGrid(Ts series) {
        final ATsGrid result = gridPool.getOrCreate();
        result.setTsUpdateMode(TsUpdateMode.None);
        result.setMode(Mode.SINGLETS);
        result.getTsCollection().replace(series);

        return new JDisposable(result) {
            @Override
            public void dispose() {
                gridPool.recycle(result);
            }
        };
    }

    @Override
    public JComponent getChart(Iterable<Ts> series) {
        final ATsChart result = chartPool.getOrCreate();
        result.setTsUpdateMode(TsUpdateMode.None);
        result.getTsCollection().replace(series);

        return new JDisposable(result) {
            @Override
            public void dispose() {
                chartPool.recycle(result);
            }
        };
    }

    @Override
    public JComponent getGrowthChart(Iterable<Ts> series) {
        final ATsGrowthChart result = growthchartPool.getOrCreate();
        result.setTsUpdateMode(TsUpdateMode.None);
        result.getTsCollection().replace(series);

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
        result.setFont(result.getFont().deriveFont(result.getFont().getSize2D()*3/2));
        result.setText("<html><center>" + msg);
        return result;
    }

    public void setStyleSheet(StyleSheet styleSheet) {
        this.styleSheet = styleSheet;
    }

    public static StyleSheet createStyleSheet(int h1, int h2, int h3, int h4, int body, boolean tableBorder) {
        StyleSheet result = new StyleSheet();
        result.addRule("body {font-family: arial, verdana;}");
        result.addRule("body {font-size: " + Integer.toString(body) + ";}");
        result.addRule("h1 {font-size: " + Integer.toString(h1) + ";}");
        result.addRule("h2 {font-size: " + Integer.toString(h2) + ";}");
        result.addRule("h3 {font-size: " + Integer.toString(h3) + ";}");
        result.addRule("h4 {font-size: " + Integer.toString(h4) + ";}");
        //ss.addRule("h4 {color: blue;}");
        result.addRule("td, th{text-align: right; margin-left: 5px; margin-right: 5 px");
        if (tableBorder) {
            result.addRule("table {border-style: outset;}");
        }
        return result;
    }

    static abstract class JDisposable extends JComponent implements IDisposable {

        JDisposable(Component c) {
            setLayout(new BorderLayout());
            add(c, BorderLayout.CENTER);
        }
    }

    private static abstract class DisposableFactory<T extends IDisposable> implements IPool.Factory<T> {

        @Override
        public void destroy(T o) {
            o.dispose();
        }

        @Override
        public void reset(T o) {
        }
    }

    private static class ChartFactory extends DisposableFactory<ATsChart> {

        @Override
        public ATsChart create() {
            return ComponentFactory.getDefault().newTsChart();
        }
    }

    private static class GrowthChartFactory extends DisposableFactory<ATsGrowthChart> {

        @Override
        public ATsGrowthChart create() {
            return ComponentFactory.getDefault().newTsGrowthChart();
        }
    }

    private static class GridFactory extends DisposableFactory<ATsGrid> {

        @Override
        public ATsGrid create() {
            return ComponentFactory.getDefault().newTsGrid();
        }
    }

    private static class HtmlFactory extends DisposableFactory<AHtmlView> {

        @Override
        public AHtmlView create() {
            return ComponentFactory.getDefault().newHtmlView();
        }
    }
}

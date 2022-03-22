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
package ec.util.grid.swing.ext;

import demetra.desktop.components.parts.HasColorScheme;
import demetra.desktop.components.parts.HasColorSchemeResolver;
import demetra.desktop.components.parts.HasColorSchemeSupport;
import ec.util.chart.swing.SwingColorSchemeSupport;
import ec.util.grid.swing.JGrid;
import ec.util.spreadsheet.Sheet;
import ec.util.spreadsheet.helpers.ArrayBook;
import ec.util.various.swing.JCommand;
import java.awt.Component;
import java.awt.Font;
import java.util.AbstractList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.JTabbedPane;
import demetra.desktop.util.Pools;
import demetra.tsprovider.util.ObsFormat;
import demetra.util.MultiLineNameUtil;
import demetra.desktop.util.Pool;


/**
 *
 * @author Philippe Charles
 */
public final class SpreadSheetView extends javax.swing.JPanel implements HasColorScheme {

    public static final String ZOOM_RATIO_PROPERTY = "zoomRatio";
    public static final String INVERT_COLORS_PROPERTY = "invertColors";
    public static final String MODEL_PROPERTY = "model";

    @lombok.experimental.Delegate
    private final HasColorScheme colorScheme = HasColorSchemeSupport.of(this::firePropertyChange);

    private final HasColorSchemeResolver colorSchemeResolver = new HasColorSchemeResolver(colorScheme, this::onColorSchemeChange);
    
    private final Pool<JGrid> gridPool;
    private final ObsFormat dataFormat;
    private int zoomRatio;
    private boolean invertColors;
    private ArrayBook model;

    /**
     * Creates new form SpreadSheetView
     */
    public SpreadSheetView() {
        initComponents();

        this.gridPool = Pools.on(new GridPoolFactory(), 100);
        this.dataFormat = ObsFormat.getSystemDefault();
        this.zoomRatio = 100;
        this.invertColors = false;
        this.model = ArrayBook.builder().build();

        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case COLOR_SCHEME_PROPERTY:
                    onColorSchemeChange();
                    break;
                case ZOOM_RATIO_PROPERTY:
                    onZoomRatioChange();
                    break;
                case INVERT_COLORS_PROPERTY:
                    onInvertColorsChange();
                    break;
                case MODEL_PROPERTY:
                    onModelChange();
                    break;
                case "componentPopupMenu":
                    onComponentPopupMenuChange();
                    break;
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tabbedPane = new javax.swing.JTabbedPane();

        setLayout(new java.awt.BorderLayout());
        add(tabbedPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    private void onColorSchemeChange() {
        SwingColorSchemeSupport themeSupport = colorSchemeResolver.resolve();
        SheetCellRenderer cellRenderer = new SheetCellRenderer(dataFormat, themeSupport, invertColors);
        gridStream().forEach(o -> {
            o.setGridColor(themeSupport.getGridColor());
            o.setDefaultRenderer(Object.class, cellRenderer);
        });
    }

    private void onZoomRatioChange() {
        Font font = computeFont(getFont(), zoomRatio);
        gridStream().forEach(o -> o.setFont(font));
    }

    private void onInvertColorsChange() {
        onColorSchemeChange();
    }

    private void onModelChange() {
        List<JGrid> old = gridStream().collect(Collectors.toList());
        tabbedPane.removeAll();
        old.stream().forEach(gridPool::recycle);
        for (int s = 0; s < model.getSheetCount(); s++) {
            Sheet sheet = model.getSheet(s);
            JGrid grid = gridPool.getOrCreate();
            grid.setModel(new SheetGridModel(sheet));
            tabbedPane.add(MultiLineNameUtil.lastWithMax(sheet.getName(), 30), grid);
            tabbedPane.setToolTipTextAt(s, sheet.getName());
        }
        onComponentPopupMenuChange();
        onColorSchemeChange();
        onZoomRatioChange();
    }

    private void onComponentPopupMenuChange() {
        gridStream().forEach(o -> o.setComponentPopupMenu(getComponentPopupMenu()));
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public int getZoomRatio() {
        return zoomRatio;
    }

    public void setZoomRatio(int zoomRatio) {
        int old = this.zoomRatio;
        this.zoomRatio = zoomRatio >= 10 && zoomRatio <= 200 ? zoomRatio : 100;
        firePropertyChange(ZOOM_RATIO_PROPERTY, old, this.zoomRatio);
    }

    public boolean isInvertColors() {
        return invertColors;
    }

    public void setInvertColors(boolean invertColors) {
        boolean old = this.invertColors;
        this.invertColors = invertColors;
        firePropertyChange(INVERT_COLORS_PROPERTY, old, this.invertColors);
    }

    public ArrayBook getModel() {
        return model;
    }

    public void setModel(ArrayBook model) {
        ArrayBook old = this.model;
        this.model = model != null ? model : ArrayBook.builder().build();
        firePropertyChange(MODEL_PROPERTY, old, this.model);
    }
    //</editor-fold>

    public static JCommand<SpreadSheetView> applyZoomRatio(int zoomRatio) {
        return new ZoomRatioCmd(zoomRatio);
    }

    public static JCommand<SpreadSheetView> invertColors() {
        return new InvertColorsCmd();
    }

    public static JCommand<SpreadSheetView> copySelection() {
        return new CurrentGridCmd(SheetGridCommand.copySelection(false, false));
    }

    public static JCommand<SpreadSheetView> copyAll() {
        return new CurrentGridCmd(SheetGridCommand.copyAll(false, false));
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private Stream<JGrid> gridStream() {
        return asStream(tabbedPane)
                .filter(JGrid.class::isInstance)
                .map(JGrid.class::cast);
    }

    private static final class GridPoolFactory implements Pool.Factory<JGrid> {

        private final SheetGridCommand copy = SheetGridCommand.copySelection(false, false);

        @Override
        public JGrid create() {
            JGrid result = new JGrid();
            result.setRowSelectionAllowed(true);
            result.setColumnSelectionAllowed(true);
            result.getActionMap().put("copy", copy.toAction(result));
            return result;
        }

        @Override
        public void reset(JGrid o) {
            o.setModel(null);
            o.setComponentPopupMenu(null);
        }

        @Override
        public void destroy(JGrid o) {
            reset(o);
        }
    }

    private static Font computeFont(Font originalFont, int zoomRatio) {
        Font font = originalFont;
        float floatRatio = ((float) zoomRatio) / 100.0f;
        if (floatRatio != 1) {
            float scaledSize = originalFont.getSize2D() * floatRatio;
            font = originalFont.deriveFont(scaledSize);
        }
        return font;
    }

    private static Stream<Component> asStream(final JTabbedPane pane) {
        return new AbstractList<Component>() {
            @Override
            public Component get(int index) {
                return pane.getComponentAt(index);
            }

            @Override
            public int size() {
                return pane.getTabCount();
            }
        }.stream();
    }

    private static final class ZoomRatioCmd extends JCommand<SpreadSheetView> {

        private final int zoomRatio;

        public ZoomRatioCmd(int zoomRatio) {
            this.zoomRatio = zoomRatio;
        }

        @Override
        public void execute(SpreadSheetView component) throws Exception {
            component.setZoomRatio(zoomRatio);
        }

        @Override
        public boolean isSelected(SpreadSheetView component) {
            return zoomRatio == component.getZoomRatio();
        }

        @Override
        public JCommand.ActionAdapter toAction(SpreadSheetView component) {
            return super.toAction(component).withWeakPropertyChangeListener(component, ZOOM_RATIO_PROPERTY);
        }
    }

    private static final class InvertColorsCmd extends JCommand<SpreadSheetView> {

        @Override
        public void execute(SpreadSheetView component) throws Exception {
            component.setInvertColors(!component.isInvertColors());
        }

        @Override
        public boolean isSelected(SpreadSheetView component) {
            return component.isInvertColors();
        }

        @Override
        public JCommand.ActionAdapter toAction(SpreadSheetView component) {
            return super.toAction(component).withWeakPropertyChangeListener(component, INVERT_COLORS_PROPERTY);
        }
    }

    private static final class CurrentGridCmd extends JCommand<SpreadSheetView> {

        private final JCommand<JGrid> delegate;

        public CurrentGridCmd(JCommand<JGrid> delegate) {
            this.delegate = delegate;
        }

        @Override
        public void execute(SpreadSheetView component) throws Exception {
            Component grid = component.tabbedPane.getSelectedComponent();
            if (grid instanceof JGrid) {
                delegate.execute((JGrid) grid);
            }
        }
    }
    //</editor-fold>

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}

/*
 * Copyright 2013 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
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
package demetra.desktop.anomalydetection.ui;

import demetra.desktop.anomalydetection.OutlierEstimation;
import demetra.timeseries.TsCollection;
import demetra.desktop.components.parts.HasHoveredObs;
import demetra.desktop.components.parts.HasTsCollection;
import demetra.desktop.components.JTsGrid;
import demetra.desktop.components.TsGridObs;
import ec.util.chart.ObsIndex;
import ec.util.list.swing.JLists;
import demetra.desktop.components.TsSelectionBridge;
import demetra.desktop.ui.properties.l2fprod.OutlierColorChooser;
import demetra.desktop.design.SwingComponent;
import demetra.desktop.design.SwingProperty;
import demetra.modelling.TransformationType;
import demetra.timeseries.Ts;
import demetra.timeseries.TsInformationType;
import demetra.tramo.OutlierSpec;
import demetra.tramo.TramoException;
import demetra.tramo.TramoSpec;
import demetra.tramo.TransformSpec;
import java.awt.BorderLayout;
import java.awt.Component;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import jdplus.regsarima.regular.RegSarimaModel;
import jdplus.tramo.TramoKernel;
import nbbrd.design.SkipProcessing;
import org.netbeans.api.progress.ProgressHandle;

/**
 * A grid component used to display outliers found in time series. The outliers
 * are highlighted in the grid in color corresponding to the outlier's type
 *
 * @author Mats Maggi
 */
@SwingComponent
public final class JTsAnomalyGrid extends JComponent {

    @SwingProperty
    public static final String DEFAULT_SPEC_PROPERTY = "defaultSpec";

    @SwingProperty
    public static final String CRITICAL_VALUE_PROPERTY = "criticalValue";

    @SkipProcessing(target = SwingProperty.class, reason = "to be refactored")
    @SwingProperty
    public static final String COLLECTION_PROPERTY = "collection";

    @SkipProcessing(target = SwingProperty.class, reason = "to be refactored")
    @SwingProperty
    public static final String STATE_PROPERTY = "state";

    @SkipProcessing(target = SwingProperty.class, reason = "to be refactored")
    @SwingProperty
    public static final String TYPES_PROPERTY = "types";

    @SwingProperty
    public static final String TRANSFORMATION_PROPERTY = "transformation";

    @SwingProperty
    public static final String HOVERED_OBS_PROPERTY = HasHoveredObs.HOVERED_OBS_PROPERTY;

    private final JTsGrid grid;
    private List<OutlierEstimation[]> outliers;
    private TramoKernel preprocessor;
    private RegSarimaModel model;
    private TransformationType transformation;
    private TramoSpec defaultSpec = TramoSpec.TRfull;
    private TramoSpec spec = TramoSpec.TRfull;
    private double criticalValue = .0;
    private boolean defaultCritical = true;
    private ProgressHandle progressHandle;
    private SwingWorker<Void, Ts> worker;
    private boolean showAO = true;
    private boolean showLS = true;
    private boolean showTC = true;
    private boolean showSO = false;

    // <editor-fold defaultstate="collapsed" desc="Constructors">
    /**
     * Constructs a new JTsAnomalyGrid
     */
    public JTsAnomalyGrid() {
        super();
        setLayout(new BorderLayout());
        grid = new JTsGrid(TsInformationType.Data);
        outliers = new ArrayList<>();
        grid.setCellRenderer(new AnomalyCellRenderer(grid.getCellRenderer()));
//        grid.setFreezeOnImport(true);

        // Listening to a data change to calculate the new outliers
        grid.addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case HasTsCollection.TS_COLLECTION_PROPERTY:
                    onCollectionChange((TsCollection) evt.getOldValue(), (TsCollection) evt.getNewValue());
                    break;
                case JTsGrid.ZOOM_RATIO_PROPERTY:
                    firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
                    break;
                case TsSelectionBridge.TS_SELECTION_PROPERTY:
                    firePropertyChange(evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
                    break;
                case HasHoveredObs.HOVERED_OBS_PROPERTY:
                    firePropertyChange(HOVERED_OBS_PROPERTY, evt.getOldValue(), evt.getNewValue());
                    break;
            }
        });

        addPropertyChangeListener(evt -> {
            String p = evt.getPropertyName();
            if (p.equals(STATE_PROPERTY)) {
                onStateChange();
                firePropertyChange(OutliersTopComponent.STATE_CHANGED, null, worker.getState());
            }
        });

        add(grid, BorderLayout.CENTER);

        preprocessor = TramoKernel.of(spec, null);
        transformation = TransformationType.None;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters / Setters">
    public void setDefaultCritical(boolean def) {
        defaultCritical = def;
    }

    public boolean isDefaultCritical() {
        return defaultCritical;
    }

    public TransformationType getTransformation() {
        return transformation;
    }

    public void setTransformation(TransformationType transformation) {
        TransformationType old = this.transformation;
        this.transformation = transformation;
        firePropertyChange(TRANSFORMATION_PROPERTY, old, this.transformation);
    }

    public void setZoomPercentage(int percentage) {
        grid.setZoomRatio(percentage);
    }

    public int getZoomPercentage() {
        return grid.getZoomRatio();
    }

    public RegSarimaModel getModelOfSelection() {
        Ts selectedItem = getSelectedItem();
        return selectedItem != null
                ? preprocessor.process(selectedItem.getData(), null)
                : null;
    }

    public Ts getSelectedItem() {
        OptionalInt singleSelection = JLists.getSelectionIndexStream(grid.getTsSelectionModel()).findFirst();
        return singleSelection.isPresent() ? grid.getTsCollection().get(singleSelection.getAsInt()) : null;
    }

    public boolean isShowAO() {
        return showAO;
    }

    public void setShowAO(boolean showAO) {
        this.showAO = showAO;
        refreshOutliersDisplayed();
    }

    public boolean isShowLS() {
        return showLS;
    }

    public void setShowLS(boolean showLS) {
        this.showLS = showLS;
        refreshOutliersDisplayed();
    }

    public boolean isShowTC() {
        return showTC;
    }

    public void setShowTC(boolean showTC) {
        this.showTC = showTC;
        refreshOutliersDisplayed();
    }

    public boolean isShowSO() {
        return showSO;
    }

    public void setShowSO(boolean showSO) {
        this.showSO = showSO;
        refreshOutliersDisplayed();
    }

    public void setDefaultSpec(TramoSpec spec) {
        TramoSpec old = this.spec;
        this.defaultSpec = spec;
        this.spec = spec;
        refreshOutliersDisplayed();
        firePropertyChange(DEFAULT_SPEC_PROPERTY, old, this.spec);
    }

    public TramoSpec getDefaultSpec() {
        return defaultSpec;
    }

    public void setCriticalValue(double value) {
        try {
            double old = criticalValue;
            criticalValue = value;
            refreshOutliersDisplayed();
            firePropertyChange(CRITICAL_VALUE_PROPERTY, old, criticalValue);
        } catch (TramoException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public double getCriticalValue() {
        return criticalValue;
    }

    public int getSelectionIndex() {
        ListSelectionModel selection = grid.getTsSelectionModel();
        return selection.isSelectionEmpty() ? -1 : selection.getMinSelectionIndex();
    }

    public SwingWorker.StateValue getState() {
        return worker != null ? worker.getState() : SwingWorker.StateValue.PENDING;
    }

    public TsCollection getTsCollection() {
        return grid.getTsCollection();
    }

    @NonNull
    public ObsIndex getHoveredObs() {
        return grid.getHoveredObs();
    }

    public void setHoveredObs(@Nullable ObsIndex hoveredObs) {
        grid.setHoveredObs(hoveredObs);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Event Handlers">
    private void refreshOutliersDisplayed() {
        outliers.clear();
        grid.getTsSelectionModel().clearSelection();
        OutlierSpec oldspec = spec.getOutliers();
        OutlierSpec newspec = oldspec
                .toBuilder()
                .ao(showAO)
                .ls(showLS)
                .tc(showTC)
                .so(showSO)
                .criticalValue(defaultCritical ? 0.0 : criticalValue)
                .build();
        TransformSpec.Builder tbuilder = spec.getTransform().toBuilder()
                .function(transformation);
        spec = spec.toBuilder()
                .transform(tbuilder.build())
                .outliers(newspec)
                .build();
        preprocessor = TramoKernel.of(spec, null);
        firePropertyChange(TYPES_PROPERTY, oldspec, newspec);
    }

    private void onCollectionChange(TsCollection oldcol, TsCollection newcol) {
        firePropertyChange(COLLECTION_PROPERTY, oldcol, newcol);
    }

    protected void onStateChange() {
        switch (getState()) {
            case DONE:
                if (progressHandle != null) {
                    progressHandle.finish();
                }
                break;
            case PENDING:
                break;
            case STARTED:
                progressHandle = ProgressHandle.createHandle("Calculating Outliers...", () -> worker.cancel(true));
                progressHandle.start(100);
                break;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Thread Management">
    public void calculateOutliers() {
        refreshOutliersDisplayed();
        stop();
        start(false);
    }

    private class SwingWorkerImpl extends SwingWorker<Void, Ts> {

        private int progressCount = 0;
        private int nseries;

        @Override
        protected void done() {
            super.done();
            grid.repaint();
        }

        @Override
        protected Void doInBackground() throws Exception {
            outliers.clear();
            grid.repaint();
            outliers = new ArrayList<>();
            List<Ts> list = grid.getTsCollection().toList();
            nseries = list.size();

            int i = 0;
            for (Ts s : list) {
                if (isCancelled()) {
                    progressHandle.finish();
                    return null;
                }
                if (s.getData().isEmpty()) {
                    outliers.add(i, null);
                } else {
                    OutlierEstimation[] o;
                    model = preprocessor.process(s.getData(), null);
                    if (model != null) {
                        o = OutlierEstimation.of(model);
                    } else {
                        o = null;
                    }

                    if (o != null && o.length > 0) {
                        outliers.add(i, o);
                    } else {
                        outliers.add(i, null);
                    }
                }
                publish(s);
                ++i;
            }
            return null;
        }

        @Override
        protected void process(List<Ts> chunks) {
            // FIXME: find an alternative
//            grid.fireTableDataChanged();
            progressCount += chunks.size();
            if (progressHandle != null && !chunks.isEmpty()) {
                progressHandle.progress(100 * progressCount / nseries);
            }
        }
    }

    public boolean start(boolean local) {
        worker = new JTsAnomalyGrid.SwingWorkerImpl();
        worker.addPropertyChangeListener(evt -> firePropertyChange(STATE_PROPERTY, null, worker.getState()));
        worker.execute();
        return true;
    }

    public boolean stop() {
        return worker != null && worker.cancel(true);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Cell Renderer">
    /*
     * Renderer coloring the cells containing outliers
     */
    private class AnomalyCellRenderer extends DefaultTableCellRenderer {

        private final TableCellRenderer delegate;
        private final DecimalFormat df = new DecimalFormat("0.0000");
        private final JToolTip toolTip;
        private OutlierEstimation currentOutlier;

        public AnomalyCellRenderer(TableCellRenderer delegate) {
            this.delegate = delegate;
            this.toolTip = super.createToolTip();
            this.currentOutlier = null;
            setHorizontalAlignment(TRAILING);
        }

        @Override
        public JToolTip createToolTip() {
            if (currentOutlier != null) {
                toolTip.setBackground(getBackground());
                toolTip.setForeground(getForeground());
            } else {
                toolTip.setBackground(null);
                toolTip.setForeground(null);
            }
            return toolTip;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component resource = delegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (resource instanceof JLabel) {
                JLabel label = (JLabel) resource;
                setBackground(resource.getBackground());
                setForeground(resource.getForeground());
                setBorder(label.getBorder());
                setFont(resource.getFont());
            }

            if (value instanceof TsGridObs) {
                TsGridObs obs = (TsGridObs) value;
                currentOutlier = null;
                switch (obs.getStatus()) {
                    case AFTER:
                    case BEFORE:
                    case EMPTY:
                    case UNUSED:
                        break;
                    case PRESENT:
                        if (Double.isNaN(obs.getValue())) {
                            setText(".");
                        } else {
                            /*
                         * Try to find a match between the outlier and the current cell
                         * using the TsPeriods
                             */
                            setText(String.valueOf(obs.getValue()));
                            if (outliers.size() <= obs.getSeriesIndex() || outliers.get(obs.getSeriesIndex()) == null) {
                                setToolTipText("<html>Period : " + obs.getPeriod().toString() + "<br>"
                                        + "Value : " + df.format(obs.getValue()));
                            } else {
                                OutlierEstimation[] est = outliers.get(obs.getSeriesIndex());
                                int i = 0;
                                while (currentOutlier == null && i < est.length) {
                                    if (est[i].getPeriod().equals(obs.getPeriod())) {
                                        currentOutlier = est[i];
                                    }
                                    i++;
                                }

                                setText(String.valueOf(obs.getValue()));
                                if (currentOutlier != null) {
                                    setToolTipText("<html>Period : " + obs.getPeriod().toString() + "<br>"
                                            + "Value : " + df.format(obs.getValue()) + "<br>"
                                            + "Outlier Value : " + df.format(currentOutlier.getValue()) + "<br>"
                                            + "Std Err : " + df.format(currentOutlier.getStderr()) + "<br>"
                                            + "TStat : " + df.format(currentOutlier.getTstat()) + "<br>"
                                            + "Outlier type : " + currentOutlier.getCode());
                                    setBackground(OutlierColorChooser.getColor(currentOutlier.getCode()));
                                    setForeground(OutlierColorChooser.getForeColor(currentOutlier.getCode()));
                                } else {
                                    setToolTipText("<html>Period : " + obs.getPeriod().toString() + "<br>"
                                            + "Value : " + df.format(obs.getValue()));
                                }
                            }
                        }
                        break;
                }
            }

            return this;
        }
    }
    // </editor-fold>
}

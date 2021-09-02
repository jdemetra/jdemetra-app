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
package demetra.ui.components;

import demetra.desktop.design.SwingAction;
import demetra.desktop.design.SwingComponent;
import demetra.desktop.design.SwingProperty;
import demetra.ui.beans.PropertyChangeSource;
import demetra.ui.components.parts.*;
import internal.ui.components.DemoTsBuilder;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.beans.Beans;

/**
 * UI component allowing view of TSCollection in a grid.
 *
 * @author Jeremy Demortier
 * @author Philippe Charles
 * @author Mats Maggi
 */
@SwingComponent
public final class JTsGrid extends JComponent implements TimeSeriesComponent, PropertyChangeSource.WithWeakListeners,
        HasTsCollection, HasTsAction, HasCrosshair, HasColorScheme, HasObsFormat, HasHoveredObs, HasZoomRatio {

    @SwingAction
    public static final String TRANSPOSE_ACTION = "transpose";

    @SwingAction
    public static final String REVERSE_ACTION = "reverse";

    @SwingAction
    public static final String SINGLE_TS_ACTION = "singleTs";

    @SwingAction
    public static final String MULTI_TS_ACTION = "multiTs";

    @SwingAction
    public static final String TOGGLE_MODE_ACTION = "toggleMode";

    /**
     * Defines the order the data are displayed
     */
    public enum Chronology {

        /**
         * Oldest data first
         */
        ASCENDING,
        /**
         * Newest data first
         */
        DESCENDING
    }

    public enum Mode {

        /**
         * Display only a single timeseries in the grid
         */
        SINGLETS,
        /**
         * Display 1 to n timeseries in the grid
         */
        MULTIPLETS
    }

    /**
     * Defines the orientation of a JTSGrid
     */
    public enum Orientation {

        /**
         * Timeline is along vertical axis (in most cases, table height > table
         * width)
         */
        NORMAL,
        /**
         * Timeline is along horizontal axis (in most cases, table height <
         * table width)
         */
        REVERSED
    }

    @SwingProperty
    public static final String ORIENTATION_PROPERTY = "orientation";

    @SwingProperty
    public static final String CHRONOLOGY_PROPERTY = "chronology";

    @SwingProperty
    public static final String MODE_PROPERTY = "mode";

    @SwingProperty
    public static final String SINGLE_TS_INDEX_PROPERTY = "singleTsIndex";

    @SwingProperty
    public static final String USE_COLOR_SCHEME_PROPERTY = "useColorScheme";

    @SwingProperty
    public static final String SHOW_BARS_PROPERTY = "showBars";

    @SwingProperty
    public static final String CELL_RENDERER_PROPERTY = "cellRenderer";

    // DEFAULT PROPERTIES
    private static final Orientation DEFAULT_ORIENTATION = Orientation.NORMAL;
    private static final Chronology DEFAULT_CHRONOLOGY = Chronology.ASCENDING;
    private static final Mode DEFAULT_MODE = Mode.MULTIPLETS;
    private static final int DEFAULT_SINGLE_SERIES_INDEX = 0;
    private static final boolean DEFAULT_USE_COLOR_SCHEME = false;
    private static final boolean DEFAULT_SHOW_BARS = false;

    // PROPERTIES
    private Orientation orientation;
    private Chronology chronology;
    private Mode mode;
    private int singleTsIndex;
    private boolean useColorScheme;
    private boolean showBars;
    private TableCellRenderer cellRenderer;

    @lombok.experimental.Delegate
    private final HasTsCollection collection;

    @lombok.experimental.Delegate
    private final HasTsAction tsAction;

    @lombok.experimental.Delegate
    private final HasCrosshair crosshair;

    @lombok.experimental.Delegate
    private final HasColorScheme colorScheme;

    @lombok.experimental.Delegate
    private final HasObsFormat obsFormat;

    @lombok.experimental.Delegate
    private final HasHoveredObs hoveredObs;

    @lombok.experimental.Delegate
    private final HasZoomRatio zoomRatio;

    private final TsSelectionBridge tsSelectionBridge;

    public JTsGrid() {
        this.collection = HasTsCollectionSupport.of(this::firePropertyChange);
        this.crosshair = HasCrosshairSupport.of(this::firePropertyChange);
        this.tsAction = HasTsActionSupport.of(this::firePropertyChange);
        this.colorScheme = HasColorSchemeSupport.of(this::firePropertyChange);
        this.obsFormat = HasObsFormatSupport.of(this::firePropertyChange);
        this.hoveredObs = HasHoveredObsSupport.of(this::firePropertyChange);
        this.zoomRatio = HasZoomRatioSupport.of(this::firePropertyChange);
        this.orientation = DEFAULT_ORIENTATION;
        this.chronology = DEFAULT_CHRONOLOGY;
        this.mode = DEFAULT_MODE;
        this.singleTsIndex = DEFAULT_SINGLE_SERIES_INDEX;
        this.useColorScheme = DEFAULT_USE_COLOR_SCHEME;
        this.showBars = DEFAULT_SHOW_BARS;
        this.cellRenderer = new DefaultTableCellRenderer();

        this.tsSelectionBridge = new TsSelectionBridge(this::firePropertyChange);
        tsSelectionBridge.register(this);

        ComponentBackend.getDefault().install(this);

        applyDesignTimeProperties();
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        Orientation old = this.orientation;
        this.orientation = orientation != null ? orientation : DEFAULT_ORIENTATION;
        firePropertyChange(ORIENTATION_PROPERTY, old, this.orientation);
    }

    public Chronology getChronology() {
        return chronology;
    }

    public void setChronology(Chronology chronology) {
        Chronology old = this.chronology;
        this.chronology = chronology != null ? chronology : DEFAULT_CHRONOLOGY;
        firePropertyChange(CHRONOLOGY_PROPERTY, old, this.chronology);
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        Mode old = this.mode;
        this.mode = mode != null ? mode : DEFAULT_MODE;
        firePropertyChange(MODE_PROPERTY, old, this.mode);
    }

    public void setSingleTsIndex(int singleTsIndex) {
        int old = this.singleTsIndex;
        this.singleTsIndex = singleTsIndex >= 0 ? singleTsIndex : DEFAULT_SINGLE_SERIES_INDEX;
        firePropertyChange(SINGLE_TS_INDEX_PROPERTY, old, this.singleTsIndex);
    }

    public int getSingleTsIndex() {
        return singleTsIndex;
    }

    public boolean isUseColorScheme() {
        return useColorScheme;
    }

    public void setUseColorScheme(boolean useColorScheme) {
        boolean old = this.useColorScheme;
        this.useColorScheme = useColorScheme;
        firePropertyChange(USE_COLOR_SCHEME_PROPERTY, old, this.useColorScheme);
    }

    public boolean isShowBars() {
        return showBars;
    }

    public void setShowBars(boolean showBars) {
        boolean old = this.showBars;
        this.showBars = showBars;
        firePropertyChange(SHOW_BARS_PROPERTY, old, this.showBars);
    }

    @NonNull
    public TableCellRenderer getCellRenderer() {
        return cellRenderer;
    }

    public void setCellRenderer(@Nullable TableCellRenderer cellRenderer) {
        TableCellRenderer old = this.cellRenderer;
        this.cellRenderer = cellRenderer != null ? cellRenderer : new DefaultTableCellRenderer();
        firePropertyChange(CELL_RENDERER_PROPERTY, old, this.cellRenderer);
    }

    private void applyDesignTimeProperties() {
        if (Beans.isDesignTime()) {
            setTsCollection(DemoTsBuilder.randomTsCollection(3));
            setTsUpdateMode(TsUpdateMode.None);
            setPreferredSize(new Dimension(200, 150));
        }
    }
}

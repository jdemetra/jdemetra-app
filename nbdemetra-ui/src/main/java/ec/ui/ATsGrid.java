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
package ec.ui;

import ec.ui.commands.TsGridCommand;
import ec.ui.interfaces.ITsGrid;
import static ec.ui.interfaces.ITsGrid.SINGLE_TS_INDEX_PROPERTY;
import static ec.ui.interfaces.ITsGrid.ZOOM_PROPERTY;
import ec.util.chart.ColorScheme;
import ec.util.various.swing.FontAwesome;
import javax.swing.ActionMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author Philippe Charles
 */
public abstract class ATsGrid extends ATsCollectionView implements ITsGrid {

    // ACTION KEYS
    public static final String TRANSPOSE_ACTION = "transpose";
    public static final String REVERSE_ACTION = "reverse";
    public static final String SINGLE_TS_ACTION = "singleTs";
    public static final String MULTI_TS_ACTION = "multiTs";
    public static final String TOGGLE_MODE_ACTION = "toggleMode";

    // DEFAULT PROPERTIES
    protected static final Orientation DEFAULT_ORIENTATION = Orientation.NORMAL;
    protected static final Chronology DEFAULT_CHRONOLOGY = Chronology.ASCENDING;
    protected static final Mode DEFAULT_MODE = Mode.MULTIPLETS;
    protected static final int DEFAULT_SINGLE_SERIES_INDEX = 0;
    protected static final int DEFAULT_ZOOM_RATIO = 100;

    // PROPERTIES
    protected Orientation orientation;
    protected Chronology chronology;
    protected Mode mode;
    protected int singleTsIndex;
    protected int zoomRatio;

    public ATsGrid() {
        this.orientation = DEFAULT_ORIENTATION;
        this.chronology = DEFAULT_CHRONOLOGY;
        this.mode = DEFAULT_MODE;
        this.singleTsIndex = DEFAULT_SINGLE_SERIES_INDEX;
        this.zoomRatio = DEFAULT_ZOOM_RATIO;

        enableProperties();
        registerActions();
    }

    private void registerActions() {
        ActionMap am = getActionMap();
        am.put(TRANSPOSE_ACTION, TsGridCommand.transpose().toAction(this));
        am.put(REVERSE_ACTION, TsGridCommand.reverseChronology().toAction(this));
        am.put(SINGLE_TS_ACTION, TsGridCommand.applyMode(ITsGrid.Mode.SINGLETS).toAction(this));
        am.put(MULTI_TS_ACTION, TsGridCommand.applyMode(ITsGrid.Mode.MULTIPLETS).toAction(this));
        am.put(TOGGLE_MODE_ACTION, TsGridCommand.toggleMode().toAction(this));
    }

    private void enableProperties() {
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case ORIENTATION_PROPERTY:
                    onOrientationChange();
                    break;
                case CHRONOLOGY_PROPERTY:
                    onChronologyChange();
                    break;
                case MODE_PROPERTY:
                    onModeChange();
                    break;
                case SINGLE_TS_INDEX_PROPERTY:
                    onSingleTsIndexChange();
                    break;
                case ZOOM_PROPERTY:
                    onZoomChange();
                    break;
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    abstract protected void onOrientationChange();

    abstract protected void onChronologyChange();

    abstract protected void onModeChange();

    abstract protected void onSingleTsIndexChange();

    abstract protected void onZoomChange();
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Override
    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    public void setOrientation(Orientation orientation) {
        Orientation old = this.orientation;
        this.orientation = orientation != null ? orientation : DEFAULT_ORIENTATION;
        firePropertyChange(ORIENTATION_PROPERTY, old, this.orientation);
    }

    @Override
    public Chronology getChronology() {
        return chronology;
    }

    @Override
    public void setChronology(Chronology chronology) {
        Chronology old = this.chronology;
        this.chronology = chronology != null ? chronology : DEFAULT_CHRONOLOGY;
        firePropertyChange(CHRONOLOGY_PROPERTY, old, this.chronology);
    }

    @Override
    public Mode getMode() {
        return mode;
    }

    @Override
    public void setMode(Mode mode) {
        Mode old = this.mode;
        this.mode = mode != null ? mode : DEFAULT_MODE;
        firePropertyChange(MODE_PROPERTY, old, this.mode);
    }

    @Override
    public void setSingleTsIndex(int singleTsIndex) {
        int old = this.singleTsIndex;
        this.singleTsIndex = singleTsIndex >= 0 ? singleTsIndex : DEFAULT_SINGLE_SERIES_INDEX;
        firePropertyChange(SINGLE_TS_INDEX_PROPERTY, old, this.singleTsIndex);
    }

    @Override
    public int getSingleTsIndex() {
        return singleTsIndex;
    }

    @Override
    public ColorScheme getColorScheme() {
        return themeSupport.getLocalColorScheme();
    }

    @Override
    public void setColorScheme(ColorScheme colorScheme) {
        themeSupport.setLocalColorScheme(colorScheme);
    }

    @Deprecated
    @Override
    public void zoom(int percentage) {
        setZoomRatio(percentage);
    }

    @Override
    public int getZoomRatio() {
        return zoomRatio;
    }

    @Override
    public void setZoomRatio(int zoomRatio) {
        int old = this.zoomRatio;
        this.zoomRatio = zoomRatio >= 10 && zoomRatio <= 200 ? zoomRatio : DEFAULT_ZOOM_RATIO;
        firePropertyChange(ZOOM_PROPERTY, old, this.zoomRatio);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Menus">
    protected JMenu buildModeMenu() {
        ActionMap am = getActionMap();
        JMenu result = new JMenu("Mode");

        JMenuItem item;

        item = new JCheckBoxMenuItem(am.get(SINGLE_TS_ACTION));
        item.setText("Display only one timeseries");
        result.add(item);

        item = new JCheckBoxMenuItem(am.get(MULTI_TS_ACTION));
        item.setText("Display multiple timeseries");
        result.add(item);

        return result;
    }

    protected JMenu buildGridMenu() {
        ActionMap am = getActionMap();

        JMenu result = buildMenu();

        int index = 0;
        JMenuItem item;

        index += 10;
        result.insertSeparator(index++);

        item = new JCheckBoxMenuItem(am.get(TRANSPOSE_ACTION));
        item.setText("Transpose");
        result.add(item, index++);

        item = new JCheckBoxMenuItem(am.get(REVERSE_ACTION));
        item.setText("Reverse chronology");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_SORT_NUMERIC_DESC));
        result.add(item, index++);

        item = new JCheckBoxMenuItem(am.get(TOGGLE_MODE_ACTION));
        item.setText("Single time series");
        result.add(item, index++);

        return result;
    }
    //</editor-fold>
}

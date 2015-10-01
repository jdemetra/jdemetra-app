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

import ec.nbdemetra.ui.IConfigurable;
import static ec.ui.ATsControl.FORMAT_ACTION;
import ec.ui.commands.TsChartCommand;
import ec.ui.interfaces.ITsChart;
import ec.util.chart.ColorScheme;
import ec.util.various.swing.FontAwesome;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ActionMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public abstract class ATsChart extends ATsCollectionView implements ITsChart {

    // ACTION KEYS
    public static final String TITLE_VISIBLE_ACTION = "titleVisible";
    public static final String LEGEND_VISIBLE_ACTION = "legendVisible";
    public static final String THIN_LINE_ACTION = "thinLine";
    public static final String THICK_LINE_ACTION = "thickLine";
    public static final String SHOW_ALL_ACTION = "showAll";
    public static final String SPLIT_ACTION = "splitIntoYearlyComponents";
    
    // DEFAULT PROPERTIES
    protected static final boolean DEFAULT_LEGENDVISIBLE = true;
    protected static final boolean DEFAULT_TITLEVISIBLE = true;
    protected static final boolean DEFAULT_AXISVISIBLE = true;
    protected static final String DEFAULT_TITLE = "";
    protected static final LinesThickness DEFAULT_LINES_THICKNESS = LinesThickness.Thin;

    // PROPERTIES
    protected boolean legendVisible;
    protected boolean titleVisible;
    protected boolean axisVisible;
    protected String title;
    protected LinesThickness linesThickness;

    public ATsChart() {
        this.legendVisible = DEFAULT_LEGENDVISIBLE;
        this.titleVisible = DEFAULT_TITLEVISIBLE;
        this.axisVisible = DEFAULT_AXISVISIBLE;
        this.title = DEFAULT_TITLE;
        this.linesThickness = DEFAULT_LINES_THICKNESS;

        enableProperties();
        registerActions();
    }

    private void registerActions() {
        ActionMap am = getActionMap();
        am.put(TITLE_VISIBLE_ACTION, TsChartCommand.toggleTitleVisibility().toAction(this));
        am.put(LEGEND_VISIBLE_ACTION, TsChartCommand.toggleLegendVisibility().toAction(this));
        am.put(THIN_LINE_ACTION, TsChartCommand.applyLineThickNess(LinesThickness.Thin).toAction(this));
        am.put(THICK_LINE_ACTION, TsChartCommand.applyLineThickNess(LinesThickness.Thick).toAction(this));
        am.put(SHOW_ALL_ACTION, TsChartCommand.showAll().toAction(this));
        am.put(SPLIT_ACTION, TsChartCommand.splitIntoYearlyComponents().toAction(this));
    }

    private void enableProperties() {
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                switch (evt.getPropertyName()) {
                    case LEGEND_VISIBLE_PROPERTY:
                        onLegendVisibleChange();
                        break;
                    case TITLE_VISIBLE_PROPERTY:
                        onTitleVisibleChange();
                        break;
                    case AXIS_VISIBLE_PROPERTY:
                        onAxisVisibleChange();
                        break;
                    case TITLE_PROPERTY:
                        onTitleChange();
                        break;
                    case LINES_THICKNESS_PROPERTY:
                        onLinesThicknessChange();
                        break;
                }
            }
        });
    }

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    abstract protected void onLegendVisibleChange();

    abstract protected void onTitleVisibleChange();

    abstract protected void onAxisVisibleChange();

    abstract protected void onTitleChange();

    abstract protected void onLinesThicknessChange();
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Override
    public boolean isLegendVisible() {
        return legendVisible;
    }

    @Override
    public void setLegendVisible(boolean show) {
        boolean old = this.legendVisible;
        this.legendVisible = show;
        firePropertyChange(LEGEND_VISIBLE_PROPERTY, old, this.legendVisible);
    }

    @Override
    public boolean isTitleVisible() {
        return titleVisible;
    }

    @Override
    public void setTitleVisible(boolean show) {
        boolean old = this.titleVisible;
        this.titleVisible = show;
        firePropertyChange(TITLE_VISIBLE_PROPERTY, old, this.titleVisible);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        String old = this.title;
        this.title = title;
        firePropertyChange(TITLE_PROPERTY, old, this.title);
    }

    @Override
    public boolean isAxisVisible() {
        return axisVisible;
    }

    @Override
    public void setAxisVisible(boolean showingAxis) {
        boolean old = this.axisVisible;
        this.axisVisible = showingAxis;
        firePropertyChange(AXIS_VISIBLE_PROPERTY, old, this.axisVisible);
    }

    @Override
    public ColorScheme getColorScheme() {
        return themeSupport.getLocalColorScheme();
    }

    @Override
    public void setColorScheme(ColorScheme colorScheme) {
        themeSupport.setLocalColorScheme(colorScheme);
    }

    @Override
    public LinesThickness getLinesThickness() {
        return linesThickness;
    }

    @Override
    public void setLinesThickness(LinesThickness linesThickness) {
        LinesThickness old = this.linesThickness;
        this.linesThickness = linesThickness != null ? linesThickness : DEFAULT_LINES_THICKNESS;
        firePropertyChange(LINES_THICKNESS_PROPERTY, old, this.linesThickness);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Menus">
    protected JMenu buildLinesThicknessMenu() {
        ActionMap am = getActionMap();
        JMenu result = new JMenu("Lines thickness");
        
        JMenuItem item;
        
        item = new JCheckBoxMenuItem(am.get(THIN_LINE_ACTION));
        item.setText("Thin");
        result.add(item);
        
        item = new JCheckBoxMenuItem(am.get(THICK_LINE_ACTION));
        item.setText("Thick");
        result.add(item);
        
        return result;
    }
    
    protected JMenu buildExportImageMenu() {
        ActionMap am = getActionMap();
        JMenu result = new JMenu("Export image to");
        
        JMenuItem item;
        
        item = new JMenuItem(am.get(PRINT_ACTION));
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_PRINT));
        item.setText("Printer...");
        result.add(item);
        
        return result;
    }
    
    protected JMenu buildChartMenu() {
        ActionMap am = getActionMap();
        JMenu result = buildMenu();
        
        int index = 0;
        JMenuItem item;
        
        index += 7;
        item = new JMenuItem(am.get(SPLIT_ACTION));
        item.setText("Split into yearly components");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_CHAIN_BROKEN));
        ExtAction.hideWhenDisabled(item);
        result.add(item, index++);
        
        index += 3;
        result.insertSeparator(index++);
        
        item = new JCheckBoxMenuItem(am.get(TITLE_VISIBLE_ACTION));
        item.setText("Show title");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_FONT));
        result.add(item, index++);
        
        item = new JCheckBoxMenuItem(am.get(LEGEND_VISIBLE_ACTION));
        item.setText("Show legend");
        result.add(item, index++);
        
        item = new JMenuItem(getActionMap().get(FORMAT_ACTION));
        item.setText("Edit format...");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_GLOBE));
        result.add(item, index++);
        
        result.add(buildColorSchemeMenu(), index++);
        result.add(buildLinesThicknessMenu(), index++);
        
        if (!(this instanceof IConfigurable)) {
            result.insertSeparator(index);
        }
        index++;
        
        item = new JMenuItem(am.get(SHOW_ALL_ACTION));
        item.setText("Show all");
        item.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_EYE));
        result.add(item, index++);
        
        result.add(buildExportImageMenu(), index++);
        
        return result;
    }
    //</editor-fold>
}

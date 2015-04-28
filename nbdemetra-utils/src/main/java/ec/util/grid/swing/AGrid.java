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
package ec.util.grid.swing;

import ec.util.grid.CellIndex;
import ec.util.various.swing.StandardSwingColor;
import java.awt.Color;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;

/**
 *
 * @author Philippe Charles
 */
abstract class AGrid extends JComponent {

    public static final String MODEL_PROPERTY = "model";
    public static final String ROW_SELECTION_ALLOWED_PROPERTY = "rowSelectionAllowed";
    public static final String COLUMN_SELECTION_ALLOWED_PROPERTY = "columnSelectionAllowed";
    public static final String HOVERED_CELL_PROPERTY = "hoveredCell";
    public static final String SELECTED_CELL_PROPERTY = "selectedCell";

    public static final String DRAG_ENABLED_PROPERTY = "dragEnabled";
    public static final String GRID_COLOR_PROPERTY = "gridColor";
    public static final String NO_DATA_RENDERER_PROPERTY = "noDataRenderer";
    public static final String ROW_SELECTION_MODEL_PROPERTY = "rowSelectionModel";
    public static final String COLUMN_SELECTION_MODEL_PROPERTY = "columnSelectionModel";

    private static final GridModel DEFAULT_MODEL = GridModels.empty();
    private static final boolean DEFAULT_ROW_SELECTION_ALLOWED = true;
    private static final boolean DEFAULT_COLUMN_SELECTION_ALLOWED = false;
    private static final CellIndex DEFAULT_HOVERED_CELL = CellIndex.NULL;
    private static final CellIndex DEFAULT_SELECTED_CELL = CellIndex.NULL;

    private static final boolean DEFAULT_DRAG_ENABLED = false;
    private static final XTable.NoDataRenderer DEFAULT_NO_DATA_RENDERER = new XTable.DefaultNoDataRenderer();

    protected GridModel model;
    protected boolean rowSelectionAllowed;
    protected boolean columnSelectionAllowed;
    protected CellIndex hoveredCell;
    protected CellIndex selectedCell;

    protected boolean dragEnabled;
    protected Color gridColor;
    protected XTable.NoDataRenderer noDataRenderer;
    protected ListSelectionModel rowSelectionModel;
    protected ListSelectionModel columnSelectionModel;

    public AGrid() {
        this.model = DEFAULT_MODEL;
        this.rowSelectionAllowed = DEFAULT_ROW_SELECTION_ALLOWED;
        this.columnSelectionAllowed = DEFAULT_COLUMN_SELECTION_ALLOWED;
        this.hoveredCell = DEFAULT_HOVERED_CELL;
        this.selectedCell = DEFAULT_SELECTED_CELL;

        this.dragEnabled = DEFAULT_DRAG_ENABLED;
        this.gridColor = StandardSwingColor.CONTROL.or(Color.LIGHT_GRAY);
        this.noDataRenderer = DEFAULT_NO_DATA_RENDERER;
        this.rowSelectionModel = new DefaultListSelectionModel();
        this.columnSelectionModel = new DefaultListSelectionModel();
    }

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    @Nonnull
    public GridModel getModel() {
        return model;
    }

    public void setModel(@Nullable GridModel model) {
        GridModel old = this.model;
        this.model = model != null ? model : DEFAULT_MODEL;
        firePropertyChange(MODEL_PROPERTY, old, this.model);
    }

    public boolean isRowSelectionAllowed() {
        return rowSelectionAllowed;
    }

    public void setRowSelectionAllowed(boolean rowSelectionAllowed) {
        boolean old = this.rowSelectionAllowed;
        this.rowSelectionAllowed = rowSelectionAllowed;
        firePropertyChange(ROW_SELECTION_ALLOWED_PROPERTY, old, this.rowSelectionAllowed);
    }

    public boolean isColumnSelectionAllowed() {
        return columnSelectionAllowed;
    }

    public void setColumnSelectionAllowed(boolean columnSelectionAllowed) {
        boolean old = this.columnSelectionAllowed;
        this.columnSelectionAllowed = columnSelectionAllowed;
        firePropertyChange(COLUMN_SELECTION_ALLOWED_PROPERTY, old, this.columnSelectionAllowed);
    }

    @Nonnull
    public CellIndex getHoveredCell() {
        return hoveredCell;
    }

    public void setHoveredCell(@Nullable CellIndex hoveredCell) {
        CellIndex old = this.hoveredCell;
        this.hoveredCell = hoveredCell != null ? hoveredCell : DEFAULT_HOVERED_CELL;
        firePropertyChange(HOVERED_CELL_PROPERTY, old, this.hoveredCell);
    }

    @Nonnull
    public CellIndex getSelectedCell() {
        return selectedCell;
    }

    public void setSelectedCell(@Nullable CellIndex selectedCell) {
        CellIndex old = this.selectedCell;
        this.selectedCell = selectedCell != null ? selectedCell : DEFAULT_SELECTED_CELL;
        firePropertyChange(SELECTED_CELL_PROPERTY, old, this.selectedCell);
    }

    public boolean isDragEnabled() {
        return dragEnabled;
    }

    public void setDragEnabled(boolean dragEnabled) {
        boolean old = this.dragEnabled;
        this.dragEnabled = dragEnabled;
        firePropertyChange(DRAG_ENABLED_PROPERTY, old, this.dragEnabled);
    }

    @Nonnull
    public Color getGridColor() {
        return gridColor;
    }

    public void setGridColor(@Nullable Color gridColor) {
        Color old = this.gridColor;
        this.gridColor = gridColor != null ? gridColor : StandardSwingColor.CONTROL.or(Color.LIGHT_GRAY);
        firePropertyChange(GRID_COLOR_PROPERTY, old, this.gridColor);
    }

    @Nonnull
    public XTable.NoDataRenderer getNoDataRenderer() {
        return noDataRenderer;
    }

    public void setNoDataRenderer(@Nullable XTable.NoDataRenderer renderer) {
        XTable.NoDataRenderer old = this.noDataRenderer;
        this.noDataRenderer = renderer != null ? renderer : DEFAULT_NO_DATA_RENDERER;
        firePropertyChange(NO_DATA_RENDERER_PROPERTY, old, this.noDataRenderer);
    }

    @Nonnull
    public ListSelectionModel getRowSelectionModel() {
        return rowSelectionModel;
    }

    public void setRowSelectionModel(@Nullable ListSelectionModel rowSelectionModel) {
        ListSelectionModel old = this.rowSelectionModel;
        this.rowSelectionModel = rowSelectionModel != null ? rowSelectionModel : new DefaultListSelectionModel();
        firePropertyChange(ROW_SELECTION_MODEL_PROPERTY, old, this.rowSelectionModel);
    }

    @Nonnull
    public ListSelectionModel getColumnSelectionModel() {
        return columnSelectionModel;
    }

    public void setColumnSelectionModel(@Nullable ListSelectionModel columnSelectionModel) {
        ListSelectionModel old = this.columnSelectionModel;
        this.columnSelectionModel = columnSelectionModel != null ? columnSelectionModel : new DefaultListSelectionModel();
        firePropertyChange(COLUMN_SELECTION_MODEL_PROPERTY, old, this.columnSelectionModel);
    }
    //</editor-fold>
}

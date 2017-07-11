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
package ec.util.grid.swing;

import static ec.util.chart.swing.SwingColorSchemeSupport.withAlpha;
import static ec.util.various.swing.ModernUI.createDropBorder;
import ec.util.various.swing.StandardSwingColor;
import java.awt.*;
import java.awt.event.MouseEvent;
import static java.util.Optional.ofNullable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Improved JTable that adds these functionalities: <li>alternate background for
 * rows <li>cell padding <li>"no data" message <li>using cell renderers as
 * tooltip factory
 *
 * @author Philippe Charles
 */
public class XTable extends JTable {

    //<editor-fold defaultstate="collapsed" desc="Properties">
    public static final String CELL_PADDING_PROPERTY = "Table.cellPadding";
    public static final String ODD_BACKGROUND_PROPERTY = "Table.oddBackground";
    public static final String NO_DATA_RENDERER_PROPERTY = "noDataRenderer";

    protected static final Dimension DEFAULT_CELL_PADDING = new Dimension(4, 2);
    protected static final Color DEFAULT_ODD_BACKGROUND = new Color(250, 250, 250);
    protected static final NoDataRenderer DEFAULT_NO_DATA_RENDERER = new DefaultNoDataRenderer();

    protected Dimension cellPadding;
    protected Color oddBackground;
    protected NoDataRenderer noDataRenderer;
    //</editor-fold>

    // OTHER
    private final CellRendererPane cellRendererPane;
    private final PaddingBorder cellBorder;
    private boolean hasDropLocation;
    private JComponent toolTipFactory;

    public XTable() {
        this.cellRendererPane = new CellRendererPane();
        this.cellPadding = ofNullable(UIManager.getDimension(CELL_PADDING_PROPERTY)).orElse(DEFAULT_CELL_PADDING);
        this.oddBackground = ofNullable(UIManager.getColor(ODD_BACKGROUND_PROPERTY)).orElse(DEFAULT_ODD_BACKGROUND);
        this.noDataRenderer = DEFAULT_NO_DATA_RENDERER;
        this.cellBorder = new PaddingBorder();
        this.hasDropLocation = false;
        this.toolTipFactory = null;

        initTable();
        enableProperties();
    }

    private void initTable() {
        ofNullable(StandardSwingColor.CONTROL.value()).ifPresent(this::setGridColor);
        onCellPaddingChange();
    }

    //<editor-fold defaultstate="collapsed" desc="Interactive stuff">
    private void enableProperties() {
        addPropertyChangeListener(evt -> {
            switch (evt.getPropertyName()) {
                case "dropLocation":
                    hasDropLocation = evt.getNewValue() != null;
                    onNoDataRendererChange();
                    break;
                case CELL_PADDING_PROPERTY:
                    onCellPaddingChange();
                    break;
                case ODD_BACKGROUND_PROPERTY:
                    onOddBackgroundChange();
                    break;
                case NO_DATA_RENDERER_PROPERTY:
                    onNoDataRendererChange();
                    break;
            }
        });
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Event handlers">
    protected void onCellPaddingChange() {
        cellBorder.setPadding(cellPadding);
        setRowHeight(getFontMetrics(getFont()).getHeight() + cellPadding.height * 2 + 1);
    }

    protected void onOddBackgroundChange() {
        repaint();
    }

    protected void onNoDataRendererChange() {
        if (getRowCount() == 0) {
            repaint();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Getters/Setters">
    public void setCellPadding(@Nullable Dimension cellPadding) {
        Dimension old = this.cellPadding;
        this.cellPadding = cellPadding != null ? cellPadding : DEFAULT_CELL_PADDING;
        firePropertyChange(CELL_PADDING_PROPERTY, old, this.cellPadding);
    }

    public void setOddBackground(@Nullable Color oddBackground) {
        Color old = this.oddBackground;
//        this.oddBackground = oddBackground != null ? oddBackground : DEFAULT_ODD_BACKGROUND;
        this.oddBackground = oddBackground;
        firePropertyChange(ODD_BACKGROUND_PROPERTY, old, this.oddBackground);
    }

    @Nonnull
    public NoDataRenderer getNoDataRenderer() {
        return noDataRenderer;
    }

    public void setNoDataRenderer(@Nullable NoDataRenderer noDataRenderer) {
        NoDataRenderer old = this.noDataRenderer;
        this.noDataRenderer = noDataRenderer != null ? noDataRenderer : DEFAULT_NO_DATA_RENDERER;
        firePropertyChange(NO_DATA_RENDERER_PROPERTY, old, this.noDataRenderer);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Tooltip hack">
    @Override
    public JToolTip createToolTip() {
        return toolTipFactory != null ? toolTipFactory.createToolTip() : super.createToolTip();
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        String tip = null;
        Point p = event.getPoint();

        // Locate the renderer under the event location
        int hitColumnIndex = columnAtPoint(p);
        int hitRowIndex = rowAtPoint(p);

        if ((hitColumnIndex != -1) && (hitRowIndex != -1)) {
            TableCellRenderer renderer = getCellRenderer(hitRowIndex, hitColumnIndex);
            Component component = prepareRenderer(renderer, hitRowIndex, hitColumnIndex);

            // Now have to see if the component is a JComponent before
            // getting the tip
            if (component instanceof JComponent) {
                // Convert the event to the renderer's coordinate system
                Rectangle cellRect = getCellRect(hitRowIndex, hitColumnIndex, false);
                p.translate(-cellRect.x, -cellRect.y);
                MouseEvent newEvent = new MouseEvent(component, event.getID(),
                        event.getWhen(), event.getModifiers(),
                        p.x, p.y,
                        event.getXOnScreen(),
                        event.getYOnScreen(),
                        event.getClickCount(),
                        event.isPopupTrigger(),
                        MouseEvent.NOBUTTON);

                tip = ((JComponent) component).getToolTipText(newEvent);
                toolTipFactory = (JComponent) component;
            }
        }

        // No tip from the renderer get our own tip
        if (tip == null) {
            tip = getToolTipText();
            toolTipFactory = null;
        }

        return tip;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Alternate row background hack">
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component result = super.prepareRenderer(renderer, row, column);
        if (oddBackground != null && !isPaintingForPrint() && !isCellSelected(row, column)) {
            result.setBackground(row % 2 == 0 ? getBackground() : oddBackground);
        }
        cellBorder.apply(result);
        return result;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Popup when no rows hack">
    @Override
    public boolean getScrollableTracksViewportHeight() {
        if (getParent() instanceof JViewport) {
            return (((JViewport) getParent()).getHeight() > getPreferredSize().height);
        }
        return super.getScrollableTracksViewportHeight();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="No-data hack">
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (getRowCount() == 0) {
            paintNoData(g);
        }
    }

    protected void paintNoData(Graphics g) {
        cellRendererPane.paintComponent(g, noDataRenderer.getNoDataRendererComponent(this, hasDropLocation), this, 0, 0, getWidth(), getHeight());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="No-data renderer">
    @FunctionalInterface
    public interface NoDataRenderer {

        Component getNoDataRendererComponent(JTable table, boolean hasDropLocation);
    }

    /**
     * Default implementation of {@link NoDataRenderer}.<br>This implementation
     * supports basic html.
     */
    public static class DefaultNoDataRenderer implements NoDataRenderer {

        private final JLabel label;
        private final String message;
        private final String onDropMessage;

        public DefaultNoDataRenderer() {
            this("No data", "Drop data");
        }

        public DefaultNoDataRenderer(@Nonnull String message) {
            this(message, message);
        }

        public DefaultNoDataRenderer(@Nonnull String message, @Nonnull String onDropMessage) {
            this.label = new JLabel();
            this.message = message;
            this.onDropMessage = onDropMessage;
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getNoDataRendererComponent(JTable table, boolean hasDropLocation) {
            if (hasDropLocation) {
                label.setText(onDropMessage);
                label.setForeground(table.getSelectionForeground());
                label.setBackground(withAlpha(table.getSelectionBackground(), 200));
                label.setBorder(createDropBorder(label.getForeground()));
            } else {
                label.setText(message);
                label.setForeground(withAlpha(table.getForeground(), 100));
                label.setBackground(table.getBackground());
                label.setBorder(null);
            }
            if (label.getText().startsWith("<html>")) {
                label.setFont(table.getFont());
            } else {
                label.setFont(table.getFont().deriveFont(table.getFont().getSize2D() * 2));
            }
            return label;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Padding hack">
    private static final class PaddingBorder extends CompoundBorder {

        public void setPadding(Dimension cellPadding) {
            insideBorder = BorderFactory.createEmptyBorder(cellPadding.height, cellPadding.width, cellPadding.height, cellPadding.width);
        }

        public void apply(Component c) {
            if (c instanceof JComponent) {
                apply((JComponent) c);
            }
        }

        public void apply(JComponent c) {
            if (c.getBorder() != this) {
                this.outsideBorder = c.getBorder();
                c.setBorder(this);
            }
        }
    }
    //</editor-fold>

    /**
     * Set the width of the columns as percentages.
     *
     * @param table the {@link JTable} whose columns will be set
     * @param percentages the widths of the columns as percentages; note: this
     * method does NOT verify that all percentages add up to 100% and for the
     * columns to appear properly, it is recommended that the widths for ALL
     * columns be specified
     *
     * @see
     * http://kahdev.wordpress.com/2011/10/30/java-specifying-the-column-widths-of-a-jtable-as-percentages/
     */
    public static void setWidthAsPercentages(JTable table, double... percentages) {
        final double factor = 10000;
        TableColumnModel model = table.getColumnModel();
        for (int columnIndex = 0; columnIndex < percentages.length; columnIndex++) {
            TableColumn column = model.getColumn(columnIndex);
            column.setPreferredWidth((int) (percentages[columnIndex] * factor));
        }
    }
}

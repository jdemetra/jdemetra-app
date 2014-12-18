/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.util.chart.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.LegendItemEntity;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.data.general.SeriesDataset;

/**
 *
 * @author Philippe Charles
 */
public class SelectionMouseListener extends MouseAdapter {

    protected final ListSelectionModel listSelectionModel;
    protected final boolean dragEnabled;

    public SelectionMouseListener(@Nonnull ListSelectionModel listSelectionModel, boolean dragEnabled) {
        this.listSelectionModel = listSelectionModel;
        this.dragEnabled = dragEnabled;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (Charts.isPopup(e) || Charts.isDoubleClick(e)) {
            return;
        }
        int index;
        if (-1 != (index = getSelectionIndex(e))) {
            if (e.isControlDown()) {
                // multi selection
                if (listSelectionModel.isSelectedIndex(index)) {
                    listSelectionModel.removeSelectionInterval(index, index);
                } else {
                    listSelectionModel.addSelectionInterval(index, index);
                }
            } else {
                // single selection
                listSelectionModel.setSelectionInterval(index, index);
            }
            if (dragEnabled && e.getSource() instanceof ChartPanel) {
                ChartPanel chartPanel = (ChartPanel) e.getSource();
                TransferHandler transferHandler = chartPanel.getTransferHandler();
                if (transferHandler != null) {
                    transferHandler.exportAsDrag(chartPanel, e, TransferHandler.COPY);
                }
            }
        } else {
            // no selection
            listSelectionModel.clearSelection();
        }
    }

    /**
     * Retrieves the index of the series that was selected
     *
     * @param e Event triggered by the mouse (usually a mouse pressed)
     * @return Index of the series in the collection; -1 if there was no valid
     * selection
     */
    protected int getSelectionIndex(@Nonnull MouseEvent e) {
        return getSelectionIndex(getSelection(e));
    }

    protected int getSelectionIndex(@Nullable LegendItemEntity entity) {
        return entity != null ? ((SeriesDataset) entity.getDataset()).indexOf(entity.getSeriesKey()) : -1;
    }

    @Nullable
    private static LegendItemEntity getSelection(@Nonnull MouseEvent e) {
        ChartPanel chartPanel = (ChartPanel) e.getSource();
        ChartEntity entity = chartPanel.getEntityForPoint(e.getX(), e.getY());
        if (entity instanceof LegendItemEntity) {
            // Clicking on the legend
            return (LegendItemEntity) entity;
        }
        if (entity instanceof XYItemEntity) {
            XYItemEntity tmp = ((XYItemEntity) entity);
            return Charts.createFakeLegendItemEntity(tmp.getDataset(), tmp.getDataset().getSeriesKey(tmp.getSeriesIndex()));
        }
        if (entity instanceof PlotEntity) {
            // Clicking on the chart -> is a curve nearby?
            return Charts.getSeriesForPoint(e.getPoint(), chartPanel);
        }
        return null;
    }
}

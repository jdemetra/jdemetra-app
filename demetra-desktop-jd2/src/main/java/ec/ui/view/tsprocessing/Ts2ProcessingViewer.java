/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import demetra.bridge.TsConverter;
import demetra.desktop.TsManager;
import demetra.desktop.datatransfer.DataTransfer;
import demetra.desktop.tsproviders.DataSourceManager;
import demetra.timeseries.TsInformationType;
import ec.tss.Ts;
import ec.tss.documents.MultiTsDocument;
import ec.tstoolkit.algorithm.IProcSpecification;

import javax.swing.*;
import java.awt.*;
import java.beans.BeanInfo;
import java.util.Optional;

/**
 * @author Jean Palate
 */
public class Ts2ProcessingViewer extends DefaultProcessingViewer<MultiTsDocument> {

    // FACTORY METHODS >
    public static Ts2ProcessingViewer create(MultiTsDocument doc, String y, String z) {
        Ts2ProcessingViewer viewer = new Ts2ProcessingViewer(Type.APPLY, y, z);
        if (doc != null) {
            viewer.setDocument(doc);
        }
        return viewer;
    }

    // < FACTORY METHODS
    // CONSTANTS
    private static final Font DROP_DATA_FONT = new JLabel().getFont().deriveFont(Font.ITALIC);
    // visual components
    private final JLabel dropDataLabely, dropDataLabelz;
    private final JLabel tsLabely, tsLabelz;
    private final JLabel specLabel;

    public Ts2ProcessingViewer(Type type, String y, String z) {
        super(type);
        this.dropDataLabely = new JLabel("Drop " + y + " here");
        this.dropDataLabelz = new JLabel("Drop " + z + " here");
        dropDataLabely.setFont(DROP_DATA_FONT);
        dropDataLabelz.setFont(DROP_DATA_FONT);
        this.tsLabely = new JLabel(y);
        tsLabely.setVisible(false);
        this.tsLabelz = new JLabel(z);
        tsLabelz.setVisible(false);
        this.specLabel = new JLabel("Spec: ");
        specLabel.setVisible(false);

        toolBar.add(Box.createHorizontalStrut(3), 0);
        toolBar.add(dropDataLabely, 1);
        toolBar.add(tsLabely, 2);
        toolBar.add(new JToolBar.Separator(), 3);
        toolBar.add(dropDataLabelz, 4);
        toolBar.add(tsLabelz, 5);
        toolBar.add(new JToolBar.Separator(), 6);
        toolBar.add(specLabel, 7);

        TsHandler hy = new TsHandler(0);
        TsHandler hz = new TsHandler(1);
        tsLabely.setTransferHandler(hy);
        dropDataLabely.setTransferHandler(hy);
        tsLabelz.setTransferHandler(hz);
        dropDataLabelz.setTransferHandler(hz);
    }

    @Override
    public void refreshHeader() {
        MultiTsDocument doc = getDocument();
        if (doc == null) {
            return;
        }
        Ts[] input = (Ts[]) doc.getInput();
        if (input == null || input[0] == null) {
            dropDataLabely.setVisible(true);
            tsLabely.setVisible(false);
        } else {
            dropDataLabely.setVisible(false);
            demetra.timeseries.TsMoniker monikery = TsConverter.toTsMoniker(input[0].getMoniker());
            tsLabely.setIcon(DataSourceManager.get().getIcon(monikery, BeanInfo.ICON_COLOR_16x16, false));
            tsLabely.setToolTipText(tsLabely.getText() + (monikery.getSource() != null ? (" (" + monikery.getSource() + ")") : ""));
            tsLabely.setVisible(true);
        }
        if (input == null || input[1] == null) {
            dropDataLabelz.setVisible(true);
            tsLabelz.setVisible(false);
        } else {
            dropDataLabelz.setVisible(false);
            demetra.timeseries.TsMoniker monikerz = TsConverter.toTsMoniker(input[1].getMoniker());
            tsLabelz.setIcon(DataSourceManager.get().getIcon(monikerz, BeanInfo.ICON_COLOR_16x16, false));
            tsLabelz.setToolTipText(tsLabelz.getText() + (monikerz.getSource() != null ? (" (" + monikerz.getSource() + ")") : ""));
            tsLabelz.setVisible(true);
        }
        if (input != null) {
            IProcSpecification spec = doc.getSpecification();
            specLabel.setText("Spec: " + (spec != null ? spec.toString() : ""));
            specLabel.setVisible(true);
        } else {
            specLabel.setVisible(false);
        }
        this.toolBar.doLayout();

    }

    class TsHandler extends TransferHandler {

        private final int pos;

        TsHandler(int pos) {
            this.pos = pos;
        }

        @Override
        public boolean canImport(TransferSupport support) {
            return DataTransfer.getDefault().canImport(support.getDataFlavors());
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            Optional<demetra.timeseries.Ts> ts = DataTransfer.getDefault().toTs(support.getTransferable());
            if (ts.isPresent()) {
                Ts[] input = (Ts[]) getDocument().getInput();
                if (input == null) {
                    input = new Ts[2];
                } else
                    input = input.clone();
                input[pos] = TsConverter.fromTs(ts.get().load(TsInformationType.Data, TsManager.get()));
                getDocument().setInput(input);
                refreshAll();
                return true;
            }
            return false;
        }
    }
}

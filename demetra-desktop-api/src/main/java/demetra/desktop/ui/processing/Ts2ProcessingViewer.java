/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import demetra.desktop.TsDynamicProvider;
import demetra.desktop.datatransfer.DataTransferManager;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.processing.ProcSpecification;
import demetra.timeseries.MultiTsDocument;
import demetra.timeseries.Ts;
import demetra.timeseries.TsMoniker;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.TransferHandler;

/**
 *
 * @author Jean Palate
 * @param <S>
 * @param <D>
 */
public class Ts2ProcessingViewer<S extends ProcSpecification, D extends MultiTsDocument<S, ?>> extends DefaultProcessingViewer<S, D> {

    // FACTORY METHODS >
    public static <S extends ProcSpecification, D extends MultiTsDocument<S, ?>> Ts2ProcessingViewer create(MultiTsDocument doc, DocumentUIServices<S, D> uifac, String y, String z) {
        Ts2ProcessingViewer viewer = new Ts2ProcessingViewer(Type.APPLY, uifac, y, z);
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

    public Ts2ProcessingViewer(Type type, DocumentUIServices<S, D> uifac, String y, String z) {
        super(uifac, type);
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
    public void onDocumentChanged() {
        super.onDocumentChanged();
        TsDynamicProvider.onDocumentChanged(getDocument());
    }

    @Override
    public void refreshHeader() {
        MultiTsDocument doc = getDocument();
        if (doc == null) {
            return;
        }
        List<Ts> input = doc.getInput();
        if (input.isEmpty()) {
            dropDataLabely.setVisible(true);
            tsLabely.setVisible(false);
            specLabel.setVisible(false);
        } else {
            Ts low = input.get(0);
            Ts high = input.size() > 1 ? input.get(1) : null;
            dropDataLabely.setVisible(false);
            TsMoniker monikery = low.getMoniker();

//            tsLabely.setIcon(MonikerUI.getDefault().getIcon(monikery));
            tsLabely.setToolTipText(tsLabely.getText() + (" (" + monikery.getSource() + ")"));
            tsLabely.setVisible(true);
            if (high == null) {
                dropDataLabelz.setVisible(true);
                tsLabelz.setVisible(false);
            } else {
                dropDataLabelz.setVisible(false);
                TsMoniker monikerz = high.getMoniker();
//                tsLabelz.setIcon(MonikerUI.getDefault().getIcon(monikerz));
                tsLabelz.setToolTipText(tsLabelz.getText() + (" (" + monikerz.getSource() + ")"));
                tsLabelz.setVisible(true);
            }
            ProcSpecification spec = doc.getSpecification();
            specLabel.setText("Spec: " + (spec != null ? spec.toString() : ""));
            specLabel.setVisible(true);
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
             return DataTransferManager.get().canImport(support.getDataFlavors());
        }
        
        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
           Optional<Ts> ts = DataTransferManager.get().toTs(support.getTransferable());
            if (ts.isPresent()) {
               List<Ts> input = getDocument().getInput();
               List<Ts> nts=new ArrayList<>(input);
               if (input.isEmpty() && pos == 1)
                   // TODO: Message dialog
                   return false;
                if (pos < nts.size()){
                    nts.set(pos, ts.get());
                } else {
                    nts.add(ts.get());
                }
                getDocument().set(nts);
                refreshAll();
                return true;
            }
            return false;
        }
    }
}

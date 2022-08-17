/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import com.google.common.base.Strings;
import demetra.bridge.TsConverter;
import demetra.desktop.TsManager;
import demetra.desktop.datatransfer.DataTransfer;
import demetra.desktop.tsproviders.DataSourceManager;
import demetra.timeseries.TsInformationType;
import ec.tss.Ts;
import ec.tss.TsMoniker;
import ec.tss.documents.TsDocument;
import ec.tss.tsproviders.utils.MultiLineNameUtil;
import ec.tstoolkit.algorithm.IProcSpecification;

import javax.swing.*;
import java.awt.*;
import java.beans.BeanInfo;
import java.util.Optional;

/**
 * @author Jean Palate
 */
public class TsProcessingViewer extends DefaultProcessingViewer<TsDocument> {

    // FACTORY METHODS >
    public static TsProcessingViewer create(TsDocument doc) {
        TsProcessingViewer viewer = new TsProcessingViewer(Type.APPLY);
        if (doc != null) {
            viewer.setDocument(doc);
        }
        return viewer;
    }

    // < FACTORY METHODS
    // CONSTANTS
    private static final Font DROP_DATA_FONT = new JLabel().getFont().deriveFont(Font.ITALIC);
    // visual components
    private final JLabel dropDataLabel;
    private final JLabel tsLabel;
    private final JLabel specLabel;

    public TsProcessingViewer(Type type) {
        super(type);
        this.dropDataLabel = new JLabel("Drop data here");
        dropDataLabel.setFont(DROP_DATA_FONT);
        this.tsLabel = new JLabel();
        tsLabel.setVisible(false);
        this.specLabel = new JLabel("Spec: ");
        specLabel.setVisible(false);

        toolBar.add(Box.createHorizontalStrut(3), 0);
        toolBar.add(dropDataLabel, 1);
        toolBar.add(tsLabel, 2);
        toolBar.add(new JToolBar.Separator(), 3);
        toolBar.add(specLabel, 4);

        setTransferHandler(new TsHandler());
    }

    @Override
    public void refreshHeader() {
        TsDocument doc = getDocument();
        if (doc == null || doc.getInput() == null) {
            dropDataLabel.setVisible(true);
            tsLabel.setVisible(false);
            specLabel.setVisible(false);
        } else {
            dropDataLabel.setVisible(false);
            String displayName = ((Ts) doc.getInput()).getName();
            tsLabel.setText(MultiLineNameUtil.lastWithMax(displayName, 70));
            tsLabel.setToolTipText(!Strings.isNullOrEmpty(displayName) ? MultiLineNameUtil.toHtml(displayName) : null);
            TsMoniker moniker = doc.getMoniker();
            tsLabel.setIcon(DataSourceManager.get().getIcon(TsConverter.toTsMoniker(moniker), BeanInfo.ICON_COLOR_16x16, false));
            tsLabel.setVisible(true);
            IProcSpecification spec = doc.getSpecification();
            specLabel.setText("Spec: " + (spec != null ? spec.toString() : ""));
            specLabel.setVisible(true);
        }
    }

    class TsHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferSupport support) {
            return DataTransfer.getDefault().canImport(support.getDataFlavors());
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            Optional<demetra.timeseries.Ts> ts = DataTransfer.getDefault().toTs(support.getTransferable());
            if (ts.isPresent()) {
                // data needs to be loaded and frozen because JD2 underlying document tries (and fails) to refresh it
                demetra.timeseries.Ts input = ts.get().load(TsInformationType.All, TsManager.get()).freeze();
                getDocument().setInput(TsConverter.fromTs(input));
                refreshAll();
                return true;
            }
            return false;
        }
    }
}
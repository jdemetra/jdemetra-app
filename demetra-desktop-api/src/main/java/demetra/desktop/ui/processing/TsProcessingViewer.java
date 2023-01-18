/*
 * Copyright 2022 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.ui.processing;

import demetra.desktop.datatransfer.DataTransferManager;
import demetra.desktop.tsproviders.DataSourceManager;
import demetra.desktop.workspace.DocumentUIServices;
import demetra.processing.ProcSpecification;
import demetra.timeseries.Ts;
import demetra.timeseries.TsDocument;
import demetra.timeseries.TsMoniker;
import demetra.util.MultiLineNameUtil;

import javax.swing.*;
import java.awt.*;
import java.beans.BeanInfo;
import java.util.Optional;

/**
 * @author Jean Palate
 * @param <S>
 * @param <D>
 */
public class TsProcessingViewer<S extends ProcSpecification, D extends TsDocument<S, ?>> extends DefaultProcessingViewer<S, D> {
    

    // FACTORY METHODS >
    public static <S extends ProcSpecification, D extends TsDocument<S, ?>> TsProcessingViewer create(D doc, DocumentUIServices<S, D> uifac) {
        TsProcessingViewer viewer = new TsProcessingViewer(uifac, Type.APPLY);
        if (doc != null) {
            viewer.setDocument(doc);
        }
        return viewer;
    }

    // CONSTANTS
    private static final Font DROP_DATA_FONT = new JLabel().getFont().deriveFont(Font.ITALIC);
    // visual components
    private final JLabel dropDataLabel;
    private final JLabel tsLabel;
    private final JLabel specLabel;

    public TsProcessingViewer(DocumentUIServices<S, D> uifac, Type type) {
        super(uifac, type);
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
            Ts input = doc.getInput();
            String displayName = (input).getName();
            tsLabel.setText(MultiLineNameUtil.lastWithMax(displayName, 70));
            tsLabel.setToolTipText(!displayName.isEmpty() ? MultiLineNameUtil.toHtml(displayName) : null);
            TsMoniker moniker = input.getMoniker();
            tsLabel.setIcon(DataSourceManager.get().getIcon(moniker, BeanInfo.ICON_COLOR_16x16, false));
            tsLabel.setVisible(true);
            ProcSpecification spec = doc.getSpecification();
            specLabel.setText("Spec: " + spec.display());
            specLabel.setVisible(true);

        }
    }

    class TsHandler extends TransferHandler {

        @Override
        public boolean canImport(TransferSupport support) {
            return DataTransferManager.get().canImport(support.getDataFlavors());
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            Optional<Ts> ts = DataTransferManager.get().toTs(support.getTransferable());
            if (ts.isPresent()) {
                Ts input = ts.get();
                Ts old=getDocument().getInput();
                TsProcessingViewer.this.firePropertyChange(INPUT_CHANGED, old, input);
                return true;
            }
            return false;
        }
    }
}

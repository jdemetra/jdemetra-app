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
package ec.nbdemetra.chainlinking.outlineview;

import ec.tss.Ts;
import ec.tss.datatransfer.TssTransferSupport;
import java.awt.Point;
import javax.swing.JComponent;
import javax.swing.TransferHandler;

/**
 * Transfer handler allowing import of time series data as input of the chain
 * linking
 *
 * @author Mats Maggi
 */
public class TsTransferHandler extends TransferHandler {

    public TsTransferHandler() {
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }

    @Override
    public boolean canImport(TransferSupport support) {
        AddProductTable jt = (AddProductTable) support.getComponent();
        if (jt.getProducts() == null || jt.getProducts().isEmpty()) {
            return false;
        }

        Point p = support.getDropLocation().getDropPoint();
        if (jt.columnAtPoint(p) > 0 && jt.rowAtPoint(p) != -1) {
            return TssTransferSupport.getDefault().canImport(support.getDataFlavors());
        } else {
            return false;
        }
    }

    @Override
    public boolean importData(TransferSupport support) {
        AddProductTable jt = (AddProductTable) support.getComponent();

        Ts ts = TssTransferSupport.getDefault().toTs(support.getTransferable());
        if (ts != null && ts.getTsData() != null) {
            jt.setValueAt(ts.getTsData(), jt.getSelectedRow(), jt.getSelectedColumn());
        }

        return super.importData(support);
    }
}

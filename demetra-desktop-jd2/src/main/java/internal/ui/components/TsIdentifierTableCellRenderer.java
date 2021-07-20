/*
 * Copyright 2018 National Bank of Belgium
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
package internal.ui.components;

import demetra.bridge.TsConverter;
import ec.nbdemetra.ui.MonikerUI;
import ec.tss.TsIdentifier;
import ec.tss.tsproviders.utils.MultiLineNameUtil;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Philippe Charles
 */
public final class TsIdentifierTableCellRenderer extends DefaultTableCellRenderer {

    private final MonikerUI monikerUI = MonikerUI.getDefault();

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        JLabel result = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value != null) {
            TsIdentifier id = (TsIdentifier) value;
            String text = id.getName();
            if (text.isEmpty()) {
                result.setText(" ");
                result.setToolTipText(null);
            } else if (text.startsWith("<html>")) {
                result.setText(text);
                result.setToolTipText(text);
            } else {
                result.setText(MultiLineNameUtil.join(text));
                result.setToolTipText(MultiLineNameUtil.toHtml(text));
            }
            result.setIcon(monikerUI.getIcon(TsConverter.toTsMoniker(id.getMoniker())));
        }
        return result;
    }
}

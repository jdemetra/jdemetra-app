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
package ec.nbdemetra.chainlinking.outlineview.nodes;

import ec.nbdemetra.chainlinking.outlineview.nodes.ChainLinkingTreeModel.QuarterlyNode;
import ec.nbdemetra.chainlinking.outlineview.nodes.ChainLinkingTreeModel.YearlyNode;
import java.text.DecimalFormat;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.RowModel;

/**
 * Model for the "Table" part of the tree table ({@link Outline}) displaying
 * input data.
 *
 * @author Mats Maggi
 */
public class ChainLinkingRowModel implements RowModel {

    protected static String[] cNames = {"Quantity", "Price", "Value"};
    protected static Class[] cTypes = {String.class, String.class, String.class};

    private final DecimalFormat df2 = new DecimalFormat();

    public ChainLinkingRowModel() {
        df2.setMaximumFractionDigits(2);
        df2.setMinimumFractionDigits(2);
    }

    @Override
    public int getColumnCount() {
        return cNames.length;
    }

    @Override
    public Object getValueFor(Object o, int i) {
         if (o instanceof YearlyNode) {
            YearlyNode n = (YearlyNode) o;
            switch (i) {
                case 0:
                    return n.getQ() == null ? "" : df2.format(n.getQ());
                case 1:
                    return n.getP() == null ? "" : df2.format(n.getP());
                case 2:
                    return n.getQ() == null || n.getP() == null ? "" : df2.format(n.getP() * n.getQ());
            }
        } else if (o instanceof QuarterlyNode) {
            QuarterlyNode n = (QuarterlyNode) o;
            switch (i) {
                case 0:
                    return n.getQ() == null ? "" : df2.format(n.getQ());
                case 1:
                    return n.getP() == null ? "" : df2.format(n.getP());
                case 2:
                    return n.getQ() == null || n.getP() == null ? "" : df2.format(n.getP() * n.getQ());
            }
        }
        return null;
    }

    @Override
    public Class getColumnClass(int i) {
        return cTypes[i];
    }

    @Override
    public boolean isCellEditable(Object o, int i) {
        return false;
    }

    @Override
    public void setValueFor(Object o, int i, Object o1) {

    }

    @Override
    public String getColumnName(int i) {
        return cNames[i];
    }

}

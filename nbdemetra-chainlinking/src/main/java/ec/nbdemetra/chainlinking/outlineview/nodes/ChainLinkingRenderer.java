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

import ec.nbdemetra.chainlinking.outlineview.nodes.ChainLinkingTreeModel.ProductNode;
import ec.nbdemetra.chainlinking.outlineview.nodes.ChainLinkingTreeModel.YearlyNode;
import ec.nbdemetra.ui.DemetraUiIcon;
import javax.swing.UIManager;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.RenderDataProvider;

/**
 * Renderer of the "Tree" part of the tree table ({@link Outline}) displaying
 * input data.
 *
 * @author Mats Maggi
 */
public class ChainLinkingRenderer implements RenderDataProvider {

    @Override
    public java.awt.Color getBackground(Object o) {
        return null;
    }

    @Override
    public String getDisplayName(Object o) {
        if (o instanceof YearlyNode
                || o instanceof ProductNode) {
            return "<b>" + String.valueOf(o) + "</b>";
        }
        return String.valueOf(o);
    }

    @Override
    public java.awt.Color getForeground(Object o) {
        return UIManager.getColor("foreground");
    }

    @Override
    public javax.swing.Icon getIcon(Object o) {
        if (o instanceof YearlyNode) {
            return DemetraUiIcon.CALENDAR_16;
        } else if (o instanceof ProductNode) {
            return DemetraUiIcon.PUZZLE_16;
        }
        return DemetraUiIcon.DOCUMENT_16;
    }

    @Override
    public boolean isHtmlDisplayName(Object o) {
        return true;
    }

    @Override
    public String getTooltipText(Object o) {
        return " ";
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.processing;

import demetra.html.HtmlElement;
import javax.swing.JComponent;

/**
 *
 * @author Philippe Charles
  */
public class HtmlItemUI implements ItemUI<HtmlElement> {
    
    @Override
    public JComponent getView(HtmlElement information) {
        return TsViewToolkit.getHtmlViewer(information);
    }
}

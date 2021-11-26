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
 * @param <H>
 */
public abstract class HtmlItemUI<H extends IProcDocumentView<?>, I> extends DefaultItemUI<H, I> {

    @Override
    public JComponent getView(H host, I information) {
        return TsViewToolkit.getHtmlViewer(getHtmlElement(host, information));
    }

    abstract protected HtmlElement getHtmlElement(H host, I information);
}

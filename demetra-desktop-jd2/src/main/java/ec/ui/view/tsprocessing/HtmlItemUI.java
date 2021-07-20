/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.html.IHtmlElement;
import javax.swing.JComponent;

/**
 *
 * @author Philippe Charles
 */
public abstract class HtmlItemUI<H extends IProcDocumentView<?>, I> extends DefaultItemUI<H, I> {

    @Override
    public JComponent getView(H host, I information) {
        return host.getToolkit().getHtmlViewer(getHtmlElement(host, information));
    }

    abstract protected IHtmlElement getHtmlElement(H host, I information);
}

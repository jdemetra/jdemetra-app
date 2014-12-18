/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlNiidTest;
import ec.tstoolkit.stats.NiidTests;

/**
 *
 * @author pcuser
 */
public class ResidualsStatsUI<V extends IProcDocumentView<?>> extends HtmlItemUI<V, NiidTests> {

    @Override
    protected IHtmlElement getHtmlElement(V host, NiidTests information) {
        return new HtmlNiidTest(information);
    }
}

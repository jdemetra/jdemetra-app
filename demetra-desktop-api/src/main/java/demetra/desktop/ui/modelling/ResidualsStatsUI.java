/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.modelling;

import demetra.desktop.ui.processing.HtmlItemUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.html.HtmlElement;
import demetra.html.stat.HtmlNiidTest;
import jdplus.stats.tests.NiidTests;


/**
 *
 * @author Jean Palate
 */
public class ResidualsStatsUI<V extends IProcDocumentView<?>> extends HtmlItemUI<V, NiidTests> {

    @Override
    protected HtmlElement getHtmlElement(V host, NiidTests information) {
        return new HtmlNiidTest(information);
    }
}

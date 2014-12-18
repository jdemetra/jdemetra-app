/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing.sa;

import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlSeasonalityTest;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.view.tsprocessing.HtmlItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;

/**
 *
 * @author Jean Palate
 */
public class SeasonalityTestUI<V extends IProcDocumentView<?>> extends HtmlItemUI<V, SeasonalityTestUI.Information> {

    @Override
    protected IHtmlElement getHtmlElement(V host, Information information) {
        return new HtmlSeasonalityTest(information.si, information.mul);
    }

    public static class Information {

        public TsData si;
        public boolean mul;
    }
}

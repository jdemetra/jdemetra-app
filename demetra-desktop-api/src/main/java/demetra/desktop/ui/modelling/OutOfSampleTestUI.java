/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.modelling;

import demetra.desktop.ui.processing.HtmlItemUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.html.HtmlElement;
import demetra.html.modelling.HtmlOneStepAheadForecastingTest;
import jdplus.regarima.tests.OneStepAheadForecastingTest;


/**
 *
 * @author Jean Palate
 */
public class OutOfSampleTestUI<V extends IProcDocumentView<?>> extends HtmlItemUI<V, OneStepAheadForecastingTest> {

    @Override
    protected HtmlElement getHtmlElement(V host, OneStepAheadForecastingTest information) {
        return new HtmlOneStepAheadForecastingTest(information);
    }
}

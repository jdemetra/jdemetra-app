/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlOneStepAheadForecastingTest;
import ec.tstoolkit.modelling.arima.diagnostics.IOneStepAheadForecastingTest;

/**
 *
 * @author Jean Palate
 */
public class OutOfSampleTestUI<V extends IProcDocumentView<?>> extends HtmlItemUI<V, IOneStepAheadForecastingTest> {

    @Override
    protected IHtmlElement getHtmlElement(V host, IOneStepAheadForecastingTest information) {
        return new HtmlOneStepAheadForecastingTest(information);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlRegArima;
import ec.tstoolkit.modelling.arima.PreprocessingModel;

/**
 *
 * @author Jean Palate
 */
public class PreprocessingUI<V extends IProcDocumentView<?>> extends HtmlItemUI<V, PreprocessingModel> {

    @Override
    protected IHtmlElement getHtmlElement(V host, PreprocessingModel information) {
        return new HtmlRegArima(information, false);
    }
}

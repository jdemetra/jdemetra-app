/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlUcarima;
import ec.tstoolkit.arima.ArimaModel;
import ec.tstoolkit.arima.IArimaModel;

/**
 *
 * @author Jean Palate
 */
public class UcarimaUI< V extends IProcDocumentView<?>> extends HtmlItemUI<V, UcarimaUI.Information> {

    @Override
    protected IHtmlElement getHtmlElement(V host, Information information) {
        return new HtmlUcarima(information.model, information.cmps, information.names);
    }

    public static class Information {

        public IArimaModel model;
        public ArimaModel[] cmps;
        public String[] names;
    }
}

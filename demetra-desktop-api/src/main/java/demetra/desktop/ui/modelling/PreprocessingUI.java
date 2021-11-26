/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package demetra.desktop.ui.modelling;

import demetra.desktop.ui.processing.HtmlItemUI;
import demetra.desktop.ui.processing.IProcDocumentView;
import demetra.html.HtmlElement;
import demetra.html.modelling.HtmlRegArima;
import jdplus.regsarima.regular.RegSarimaModel;


/**
 *
 * @author Jean Palate
 * @param <V>
 */
public class PreprocessingUI<V extends IProcDocumentView<?>> extends HtmlItemUI<V, RegSarimaModel> {

    @Override
    protected HtmlElement getHtmlElement(V host, RegSarimaModel information) {
        return new HtmlRegArima(information, false);
    }
}

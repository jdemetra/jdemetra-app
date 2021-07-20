/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlProcessingInformation;
import ec.tstoolkit.algorithm.IProcResults;

/**
 *
 * @author Jean Palate
 * @param <V>
 */
public class ProcessingLogUI<V extends IProcDocumentView<?>> extends HtmlItemUI<V, IProcResults> {

    @Override
    protected IHtmlElement getHtmlElement(V host, IProcResults information) {
        HtmlProcessingInformation log=new HtmlProcessingInformation(information.getProcessingInformation());
        log.displayErrors(false);
        log.displayWarnings(false);
        log.displayInfos(true);
        return log;
    }
}

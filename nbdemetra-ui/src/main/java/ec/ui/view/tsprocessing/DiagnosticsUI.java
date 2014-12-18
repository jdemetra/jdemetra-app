/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlDiagnosticSummary;
import ec.tss.sa.SaManager;
import ec.tstoolkit.algorithm.CompositeResults;
import ec.tstoolkit.information.InformationSet;

/**
 *
 * @author pcuser
 */
public class DiagnosticsUI<V extends IProcDocumentView<?>> extends HtmlItemUI<V, CompositeResults> {

    @Override
    protected IHtmlElement getHtmlElement(V host, CompositeResults information) {
        InformationSet diags = SaManager.instance.diagnostic(information);
        return new HtmlDiagnosticSummary(diags);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing;

import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlSlidingSpanSummary;
import ec.tstoolkit.timeseries.analysis.SlidingSpans;

/**
 *
 * @author Jean Palate
 */
public class SlidingSpansUI<V extends IProcDocumentView<?>> extends HtmlItemUI<V, SlidingSpans> {

    private final String s_, si_;

    public SlidingSpansUI(String s, String si) {
        s_ = s;
        si_ = si;
    }

    @Override
    protected IHtmlElement getHtmlElement(V host, SlidingSpans information) {
        return new HtmlSlidingSpanSummary(information, s_, si_);
    }
}

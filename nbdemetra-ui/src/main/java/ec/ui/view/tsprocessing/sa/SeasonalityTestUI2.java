/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.ui.view.tsprocessing.sa;

import ec.tss.html.HtmlElements;
import ec.tss.html.HtmlHeader;
import ec.tss.html.IHtmlElement;
import ec.tss.html.implementation.HtmlSeasonalityDiagnostics;
import ec.tstoolkit.modelling.arima.tramo.SeasonalityTests;
import ec.tstoolkit.timeseries.simplets.TsData;
import ec.ui.view.tsprocessing.HtmlItemUI;
import ec.ui.view.tsprocessing.IProcDocumentView;

/**
 *
 * @author Jean Palate
 */
public class SeasonalityTestUI2<V extends IProcDocumentView<?>> extends HtmlItemUI<V, SeasonalityTestUI2.Information> {

    private final String header;
    private final boolean seasControl;

    public SeasonalityTestUI2(final String header, final boolean seasControl) {
        this.header = header;
        this.seasControl = seasControl;
    }

    @Override
    protected IHtmlElement getHtmlElement(V host, Information information) {
        TsData s = information.s;
        if (information.mul) {
            s = s.log();
        }
        if (header == null) {
            return new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s, information.del, information.mean, true));
        } else {
            return new HtmlElements(new HtmlHeader(1, header, true),
                    new HtmlSeasonalityDiagnostics(SeasonalityTests.seasonalityTest(s, information.del, information.mean, true), seasControl));
        }
    }

    public static class Information {
        
        public TsData s;
        public int del;
        public boolean mean=true;
        public boolean mul;
    }
}

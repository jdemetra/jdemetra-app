/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.modelling;

import demetra.desktop.ui.processing.HtmlItemUI;
import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.html.HtmlElement;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import jdplus.regsarima.regular.RegSarimaModel;
import jdplus.stats.tests.NiidTests;

/**
 *
 * @author PALATEJ
 * @param <D>
 */
public abstract class NiidTestsFactory<D extends TsDocument<?, RegSarimaModel>>
        extends ProcDocumentItemFactory<D, HtmlElement> {

    protected NiidTestsFactory(Class<D> documentType, Id id) {
        super(documentType, id, (D source) -> {
            RegSarimaModel rslt = source.getResult();
            TsData res = rslt.fullResiduals();
            NiidTests niid = NiidTests.builder()
                    .data(res.getValues())
                    .hyperParametersCount(rslt.freeArimaParametersCount())
                    .period(res.getAnnualFrequency())
                    .defaultTestsLength()
                    .build();
            return new demetra.html.stat.HtmlNiidTest(niid);
        }, new HtmlItemUI());
    }
}

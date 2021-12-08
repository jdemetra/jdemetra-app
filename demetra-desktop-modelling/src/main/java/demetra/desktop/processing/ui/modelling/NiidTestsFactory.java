/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.processing.ui.modelling;

import demetra.desktop.ui.processing.HtmlItemUI;
import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.html.HtmlElement;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import java.util.function.Function;
import jdplus.regsarima.regular.RegSarimaModel;
import jdplus.stats.tests.NiidTests;

/**
 *
 * @author PALATEJ
 * @param <D>
 */
public abstract class NiidTestsFactory<D extends TsDocument<?, ?>>
        extends ProcDocumentItemFactory<D, HtmlElement> {

    protected NiidTestsFactory(Class<D> documentType, Id id, Function<D, RegSarimaModel> extractor) {
        super(documentType, id, extractor.andThen( source -> {
            TsData res = source.fullResiduals();
            NiidTests niid = NiidTests.builder()
                    .data(res.getValues())
                    .hyperParametersCount(source.freeArimaParametersCount())
                    .period(res.getAnnualFrequency())
                    .defaultTestsLength()
                    .build();
            return new demetra.html.stat.HtmlNiidTest(niid);
        }), new HtmlItemUI());
    }
}

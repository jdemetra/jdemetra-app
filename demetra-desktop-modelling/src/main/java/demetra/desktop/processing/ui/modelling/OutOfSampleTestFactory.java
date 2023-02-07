/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.processing.ui.modelling;

import demetra.desktop.ui.processing.HtmlItemUI;
import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.html.HtmlElement;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import java.util.function.Function;
import jdplus.regarima.tests.OneStepAheadForecastingTest;
import jdplus.regsarima.RegSarimaComputer;
import jdplus.regsarima.regular.RegSarimaModel;

/**
 *
 * @author PALATEJ
 * @param <D>
 */
public abstract class OutOfSampleTestFactory<D extends TsDocument<?, ?>>
        extends ProcDocumentItemFactory<D, HtmlElement> {

    protected OutOfSampleTestFactory(Class<D> documentType, Id id, Function<D, RegSarimaModel> extractor) {
        super(documentType, id, extractor.andThen(source -> {
            if (source == null) {
                return null;
            }
            int lback;
            int freq = source.getDescription().getSeries().getAnnualFrequency();
            lback = switch (freq) {
                case 12 -> 18;
                case 6 -> 9;
                case 4 -> 6;
                default -> 5;
            };
            RegSarimaComputer processor = RegSarimaComputer.builder().build();
            OneStepAheadForecastingTest test = OneStepAheadForecastingTest.of(source.regarima(), processor, lback);
            return new demetra.html.modelling.HtmlOneStepAheadForecastingTest(test);
        }), new HtmlItemUI());
    }
}

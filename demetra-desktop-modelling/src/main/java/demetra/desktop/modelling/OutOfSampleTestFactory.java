/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.modelling;

import demetra.desktop.ui.processing.HtmlItemUI;
import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.html.HtmlElement;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import jdplus.regarima.tests.OneStepAheadForecastingTest;
import jdplus.regsarima.RegSarimaComputer;
import jdplus.regsarima.regular.RegSarimaModel;

/**
 *
 * @author PALATEJ
 * @param <D>
 */
public abstract class OutOfSampleTestFactory <D extends TsDocument<?, RegSarimaModel>>
            extends ProcDocumentItemFactory<D, HtmlElement> {

        protected OutOfSampleTestFactory(Class<D> documentType, Id id) {
            super(documentType, id, (D source)->{
                     RegSarimaModel model = source.getResult();
                    int lback;
                    int freq = model.getDescription().getSeries().getAnnualFrequency();
                    switch (freq) {
                        case 12:
                            lback = 18;
                            break;
                        case 6:
                            lback = 9;
                            break;
                        case 4:
                            lback = 6;
                            break;
                        default:
                            lback = 5;
                            break;
                    }
                    RegSarimaComputer processor = RegSarimaComputer.builder().build();
                    OneStepAheadForecastingTest test =  OneStepAheadForecastingTest.of(model.regarima(), processor, lback);
                    return new demetra.html.modelling.HtmlOneStepAheadForecastingTest(test);
            }, new HtmlItemUI());
        }
    }

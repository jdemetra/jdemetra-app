/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.processing.ui.modelling;

import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.desktop.ui.processing.stats.RegressorsUI;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import java.util.function.Function;
import jdplus.regsarima.regular.RegSarimaModel;

/**
 *
 * @author PALATEJ
 * @param <D>
 */
public abstract class ModelRegressorsFactory<D extends TsDocument<?, ?>>
            extends ProcDocumentItemFactory<D, RegSarimaModel> {

        protected ModelRegressorsFactory(Class<D> documentType, Id id, Function<D, RegSarimaModel> extractor) {
            super(documentType, id, extractor, new RegressorsUI());
        }
    }

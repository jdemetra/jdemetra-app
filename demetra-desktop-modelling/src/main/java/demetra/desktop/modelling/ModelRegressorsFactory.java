/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.modelling;

import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.desktop.ui.processing.stats.RegressorsUI;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import jdplus.regsarima.regular.RegSarimaModel;

/**
 *
 * @author PALATEJ
 * @param <D>
 */
public abstract class ModelRegressorsFactory<D extends TsDocument<?, RegSarimaModel>>
            extends ProcDocumentItemFactory<D, RegSarimaModel> {

        protected ModelRegressorsFactory(Class<D> documentType, Id id) {
            super(documentType, id, source->source.getResult(), new RegressorsUI());
        }
    }

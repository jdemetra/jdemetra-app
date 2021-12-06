/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.modelling;

import demetra.desktop.ui.processing.GenericTableUI;
import demetra.desktop.ui.processing.ProcDocumentItemFactory;
import demetra.modelling.ModellingDictionary;
import demetra.modelling.SeriesInfo;
import demetra.timeseries.TsDocument;
import demetra.util.Id;
import jdplus.regsarima.regular.RegSarimaModel;

/**
 *
 * @author PALATEJ
 * @param <D>
 */
public abstract class ForecastsTableFactory<D extends TsDocument<?, RegSarimaModel>>
            extends ProcDocumentItemFactory<D, D> {

        private static String[] generateItems() {
            return new String[]{ModellingDictionary.Y + SeriesInfo.F_SUFFIX, ModellingDictionary.Y + SeriesInfo.EF_SUFFIX};
        }

        protected ForecastsTableFactory(Class documentType, Id id) {
            super(documentType, id, null, 
                    new GenericTableUI(false, generateItems()));
        }
    }


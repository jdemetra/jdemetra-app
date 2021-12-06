/*
 * Copyright 2020 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.tramoseats.ui;

import demetra.processing.ProcessingLog;
import demetra.timeseries.TsData;
import demetra.timeseries.TsDocument;
import demetra.timeseries.regression.ModellingContext;
import demetra.tramoseats.TramoSeatsSpec;
import jdplus.tramoseats.TramoSeatsKernel;
import jdplus.tramoseats.TramoSeatsResults;

/**
 *
 * @author PALATEJ
 */
public class TramoSeatsDocument extends TsDocument<TramoSeatsSpec, TramoSeatsResults> {

    private final ModellingContext context;

    public TramoSeatsDocument() {
        super(TramoSeatsSpec.RSAfull);
        context = ModellingContext.getActiveContext();
    }

    public TramoSeatsDocument(ModellingContext context) {
        super(TramoSeatsSpec.RSAfull);
        this.context = context;
    }

    @Override
    protected TramoSeatsResults internalProcess(TramoSeatsSpec spec, TsData data) {
        return TramoSeatsKernel.of(spec, context).process(data, ProcessingLog.dummy());
    }

}

/*
 * Copyright 2022 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved 
 * by the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * https://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence.
 */
package demetra.desktop.x13.ui.actions;

import demetra.desktop.sa.util.ActionsHelper;
import demetra.desktop.x13.ui.X13UI;
import demetra.sa.SaProcessingFactory;
import demetra.sa.SaSpecification;
import demetra.x13.X13Spec;
import java.util.List;
import jdplus.x13.X13Factory;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author palatej
 */
@ServiceProvider(service = ActionsHelper.class, position=100)
public class X13ActionsHelper implements ActionsHelper {

    @Override
    public List<String> selectedSeries() {
        return X13UI.get().getSelectedComponents();
    }

    @Override
    public List<String> selectedMatrixItems() {
        return X13UI.get().getSelectedDiagnostics();
    }

    @Override
    public List<String> allSeries() {
        return X13UI.get().allComponents();
    }

    @Override
    public List<String> allMatrixItems() {
        return X13UI.get().allDiagnostics();
    }
    
    @Override
    public boolean match(SaSpecification spec) {
        return spec instanceof X13Spec;
    }

    @Override
    public boolean match(SaProcessingFactory fac) {
        return fac instanceof X13Factory;
    }

    @Override
    public int defaultSeriesParameter() {
        return X13UI.get().getDefaultSeriesParameter();
    }
    
}

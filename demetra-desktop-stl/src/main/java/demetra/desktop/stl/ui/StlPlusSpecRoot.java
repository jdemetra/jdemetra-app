/*
 * Copyright 2023 National Bank of Belgium
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
package demetra.desktop.stl.ui;

import demetra.desktop.sa.descriptors.regular.RegularSpecUI;
import demetra.modelling.regular.ModellingSpec;
import demetra.sa.benchmarking.SaBenchmarkingSpec;
import demetra.stl.StlPlusSpec;
import demetra.stl.StlSpec;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Jean Palate
 */
@lombok.Getter
public class StlPlusSpecRoot implements RegularSpecUI {

    @NonNull
    ModellingSpec preprocessing;
    StlSpec stl;
    @NonNull
    SaBenchmarkingSpec benchmarking;
    boolean ro;

    public StlPlusSpecRoot(StlPlusSpec spec, boolean ro) {
        this.preprocessing = spec.getPreprocessing();
        this.stl = spec.getStl();
        this.benchmarking = spec.getBenchmarking();
        this.ro = ro;
    }

    public StlPlusSpec getCore() {
        return StlPlusSpec.builder()
                .preprocessing(preprocessing)
                .stl(stl)
                .benchmarking(benchmarking)
                .build();
    }

    @Override
    public void update(ModellingSpec spec) {
        preprocessing=spec;
    }

    public void update(StlSpec spec) {
        stl = spec;
    }

    @Override
    public ModellingSpec preprocessing() {
        return preprocessing;
    }

}

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
package demetra.desktop.tramoseats.diagnostics.impl;

import demetra.desktop.sa.diagnostics.SpectralDiagnosticsBuddy;
import demetra.desktop.tramoseats.diagnostics.TramoSeatsDiagnosticsFactoryBuddy;
import demetra.sa.SaDiagnosticsFactory;
import jdplus.sa.diagnostics.SpectralDiagnosticsConfiguration;
import jdplus.sa.diagnostics.SpectralDiagnosticsFactory;
import jdplus.tramoseats.TramoSeatsResults;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author palatej
 */
@ServiceProvider(service = TramoSeatsDiagnosticsFactoryBuddy.class, position = 1210)
public final class TramoSeatsSpectralDiagnosticsBuddy extends SpectralDiagnosticsBuddy implements TramoSeatsDiagnosticsFactoryBuddy<SpectralDiagnosticsConfiguration> {

    public TramoSeatsSpectralDiagnosticsBuddy(){
        this.setActiveDiagnosticsConfiguration(SpectralDiagnosticsConfiguration.getDefault());
    }
    
    @Override
    public SpectralDiagnosticsFactory<TramoSeatsResults> createFactory() {
        return new SpectralDiagnosticsFactory<>(this.getActiveDiagnosticsConfiguration());
    }

}

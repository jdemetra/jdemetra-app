/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package demetra.desktop.tramoseats.diagnostics.impl;

import demetra.desktop.sa.diagnostics.SaOutOfSampleDiagnosticsBuddy;
import demetra.desktop.tramoseats.diagnostics.TramoSeatsDiagnosticsFactoryBuddy;
import demetra.sa.SaDiagnosticsFactory;
import jdplus.sa.diagnostics.SaOutOfSampleDiagnosticsFactory;
import jdplus.tramoseats.TramoSeatsResults;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author palatej
 */
@ServiceProvider(service = TramoSeatsDiagnosticsFactoryBuddy.class, position = 1100)
public class TramoSeatsOutOfSampleDiagnosticsBuddy extends SaOutOfSampleDiagnosticsBuddy implements TramoSeatsDiagnosticsFactoryBuddy {

    @Override
    public SaDiagnosticsFactory createFactory() {
        return new SaOutOfSampleDiagnosticsFactory<>(config,
                (TramoSeatsResults r) -> r.getDiagnostics().getGenericDiagnostics().forecastingTest());
    }

}

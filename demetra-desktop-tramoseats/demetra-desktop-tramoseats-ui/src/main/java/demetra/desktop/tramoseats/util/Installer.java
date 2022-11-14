/*
 * Copyright 2013 National Bank of Belgium
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
package demetra.desktop.tramoseats.util;

import demetra.desktop.Config;
import demetra.desktop.tramoseats.diagnostics.TramoSeatsDiagnosticsFactoryBuddies;
import demetra.desktop.util.InstallerStep;
import java.util.prefs.Preferences;
import org.openide.modules.ModuleInstall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Installer extends ModuleInstall {

    final static Logger LOGGER = LoggerFactory.getLogger(Installer.class);

    public static final InstallerStep STEP = InstallerStep.all(
            new DemetraTramoSeatsDiagnosticsStep()
    );

    @Override
    public void restored() {
        super.restored();
        STEP.restore();
    }

    @Override
    public void close() {
        STEP.close();
        super.close();
    }

    private static final class DemetraTramoSeatsDiagnosticsStep extends InstallerStep {

        final Preferences prefs = prefs().node("diagnostics");

        @Override
        public void restore() {
            TramoSeatsDiagnosticsFactoryBuddies.getInstance().getFactories().forEach(buddy->{
                    Preferences nprefs = prefs.node(buddy.getDisplayName());
                    tryGet(nprefs).ifPresent(buddy::setConfig);
            });
        }

        @Override
        public void close() {
            TramoSeatsDiagnosticsFactoryBuddies.getInstance().getFactories().forEach(buddy->{
                Config config = buddy.getConfig();
                if (config != null){
                    Preferences nprefs = prefs.node(buddy.getDisplayName());
                    put(nprefs, config);
                }
            });
        }
    }
}

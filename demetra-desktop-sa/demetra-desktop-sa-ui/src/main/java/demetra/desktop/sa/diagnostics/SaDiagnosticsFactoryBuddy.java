/*
 * Copyright 2015 National Bank of Belgium
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
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
package demetra.desktop.sa.diagnostics;

import demetra.desktop.NamedService;
import demetra.desktop.DemetraIcons;
import demetra.desktop.Persistable;
import demetra.desktop.actions.Resetable;
import demetra.processing.DiagnosticsConfiguration;
import java.awt.Image;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

/**
 *
 * Link between the GUI and the actual SaDiagnosticsFactory
 * @param <C> Actual converter
  */
public interface SaDiagnosticsFactoryBuddy<C extends DiagnosticsConfiguration>  extends NamedService, Persistable, Resetable{
    
    /**
     * Configuration after edition. Not necessary applied
     * @return 
     */
   C getCurrentDiagnosticsConfiguration();

    /**
     * Active (used in the algorithms) configuration
     * @return 
     */
    C getActiveDiagnosticsConfiguration();
    
    void setActiveDiagnosticsConfiguration(C config);

    boolean editConfiguration();
    
    void commit();
    
    void restore();
    
    boolean valid();

    @Override
    default String getDisplayName() {
        return getName();
    }

    @Override
    default Image getIcon(int type, boolean opened) {
        return ImageUtilities.icon2Image(DemetraIcons.PUZZLE_16);
    }

    @Override
    default Sheet createSheet() {
        return new Sheet();
    }
}

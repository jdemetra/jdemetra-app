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
package ec.nbdemetra.ui.sa;

import demetra.desktop.NamedService;
import demetra.desktop.util.NetBeansServiceBackend;
import demetra.desktop.DemetraIcons;
import java.awt.Image;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Philippe Charles
 * @since 2.1.0
 */
@ServiceDefinition(
        quantifier = Quantifier.MULTIPLE,
        backend = NetBeansServiceBackend.class,
        singleton = true
)
public abstract class SaDiagnosticsFactoryBuddy implements NamedService {

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.icon2Image(DemetraIcons.PUZZLE_16);
    }
}

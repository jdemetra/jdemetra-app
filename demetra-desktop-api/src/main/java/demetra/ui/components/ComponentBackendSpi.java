/*
 * Copyright 2018 National Bank of Belgium
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
package demetra.ui.components;

import demetra.ui.util.NetBeansServiceBackend;
import javax.swing.JComponent;
import nbbrd.service.Quantifier;
import nbbrd.service.ServiceDefinition;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Philippe Charles
 */
@ServiceDefinition(
        quantifier = Quantifier.MULTIPLE,
        backend = NetBeansServiceBackend.class,
        singleton = true
)
public interface ComponentBackendSpi {

    boolean handles(@NonNull Class<? extends JComponent> type);

    void install(@NonNull JComponent component);
}

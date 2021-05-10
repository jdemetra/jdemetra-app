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
package internal.ui.components.parts;

import demetra.ui.beans.PropertyChangeBroadcaster;
import demetra.ui.components.parts.HasColorScheme;
import ec.util.chart.ColorScheme;

/**
 *
 * @author Philippe Charles
 */
@lombok.RequiredArgsConstructor
public final class HasColorSchemeImpl implements HasColorScheme {

    @lombok.NonNull
    private final PropertyChangeBroadcaster broadcaster;
    private ColorScheme colorScheme = null;

    @Override
    public ColorScheme getColorScheme() {
        return colorScheme;
    }

    @Override
    public void setColorScheme(ColorScheme colorScheme) {
        ColorScheme old = this.colorScheme;
        this.colorScheme = colorScheme;
        broadcaster.firePropertyChange(COLOR_SCHEME_PROPERTY, old, this.colorScheme);
    }
}

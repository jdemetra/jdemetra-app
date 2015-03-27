/*
 * Copyright 2013 National Bank of Belgium
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
package ec.ui.grid;

import java.awt.Color;
import javax.annotation.Nonnull;
import javax.swing.border.Border;

/**
 *
 * @author Philippe Charles
 */
abstract class CellUIResource {

    @Nonnull
    abstract public Color getBackground();

    @Nonnull
    abstract public Color getForeground();

    @Nonnull
    abstract public Border getBorder();

    @Nonnull
    public static CellUIResource of(final Color background, final Color foreground, final Border border) {
        return new CellUIResource() {
            @Override
            public Color getBackground() {
                return background;
            }

            @Override
            public Color getForeground() {
                return foreground;
            }

            @Override
            public Border getBorder() {
                return border;
            }
        };
    }
}

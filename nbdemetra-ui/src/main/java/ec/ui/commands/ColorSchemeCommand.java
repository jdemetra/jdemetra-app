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
package ec.ui.commands;

import ec.tstoolkit.design.UtilityClass;
import ec.ui.interfaces.IColorSchemeAble;
import static ec.ui.interfaces.IColorSchemeAble.COLOR_SCHEME_PROPERTY;
import ec.util.chart.ColorScheme;
import ec.util.various.swing.JCommand;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author Philippe Charles
 */
@UtilityClass(IColorSchemeAble.class)
public final class ColorSchemeCommand {

    private ColorSchemeCommand() {
        // static class
    }

    @Nonnull
    public static JCommand<IColorSchemeAble> applyColorScheme(@Nullable ColorScheme colorScheme) {
        return new ApplyColorSchemeCommand(colorScheme);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static final class ApplyColorSchemeCommand extends ComponentCommand<IColorSchemeAble> {

        private final ColorScheme colorScheme;

        public ApplyColorSchemeCommand(@Nullable ColorScheme colorScheme) {
            super(COLOR_SCHEME_PROPERTY);
            this.colorScheme = colorScheme;
        }

        @Override
        public boolean isSelected(IColorSchemeAble component) {
            return Objects.equals(colorScheme, component.getColorScheme());
        }

        @Override
        public void execute(IColorSchemeAble component) {
            component.setColorScheme(colorScheme);
        }
    }
    //</editor-fold>
}

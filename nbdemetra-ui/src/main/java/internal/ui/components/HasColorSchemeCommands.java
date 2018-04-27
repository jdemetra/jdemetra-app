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
package internal.ui.components;

import demetra.ui.components.HasColorScheme;
import ec.ui.commands.ComponentCommand;
import ec.util.chart.ColorScheme;
import ec.util.chart.swing.ColorSchemeIcon;
import ec.util.various.swing.JCommand;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class HasColorSchemeCommands {

    public static final String DEFAULT_COLOR_SCHEME_ACTION = "defaultColorScheme";

    @Nonnull
    public static JCommand<HasColorScheme> commandOf(@Nullable ColorScheme colorScheme) {
        return new ApplyColorSchemeCommand(colorScheme);
    }

    @Nonnull
    public static Icon iconOf(@Nonnull ColorScheme colorScheme) {
        return new ColorSchemeIcon(colorScheme);
    }

    @Nonnull
    public static JMenu menuOf(@Nonnull HasColorScheme component, @Nonnull Iterable<? extends ColorScheme> colorSchemes) {
        JMenu result = new JMenu("Color scheme");
        result.add(menuItemOf(component, null));
        result.addSeparator();
        colorSchemes.forEach(o -> result.add(menuItemOf(component, o)));
        result.add(result);
        return result;
    }

    @Nonnull
    public static JMenuItem menuItemOf(@Nonnull HasColorScheme component, @Nullable ColorScheme colorScheme) {
        JMenuItem result = new JCheckBoxMenuItem(commandOf(colorScheme).toAction(component));
        if (colorScheme != null) {
            result.setText(colorScheme.getDisplayName());
            result.setIcon(iconOf(colorScheme));
        } else {
            result.setText("Default");
        }
        return result;
    }
    
    private static final class ApplyColorSchemeCommand extends ComponentCommand<HasColorScheme> {

        private final ColorScheme colorScheme;

        public ApplyColorSchemeCommand(@Nullable ColorScheme colorScheme) {
            super(HasColorScheme.COLOR_SCHEME_PROPERTY);
            this.colorScheme = colorScheme;
        }

        @Override
        public boolean isSelected(HasColorScheme component) {
            return Objects.equals(colorScheme, component.getColorScheme());
        }

        @Override
        public void execute(HasColorScheme component) {
            component.setColorScheme(colorScheme);
        }
    }
}

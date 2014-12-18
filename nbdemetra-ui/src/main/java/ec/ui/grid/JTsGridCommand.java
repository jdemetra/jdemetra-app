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

import ec.tstoolkit.design.UtilityClass;
import static ec.ui.grid.JTsGrid.SHOW_BARS_PROPERTY;
import static ec.ui.grid.JTsGrid.USE_COLOR_SCHEME_PROPERTY;
import ec.util.various.swing.JCommand;
import java.awt.Component;
import javax.annotation.Nonnull;

/**
 *
 * @author Philippe Charles
 */
@UtilityClass(JTsGrid.class)
public final class JTsGridCommand {

    private JTsGridCommand() {
        // static 
    }

    @Nonnull
    public static JCommand<JTsGrid> toggleUseColorScheme() {
        return ToggleUseColorSchemeCommand.INSTANCE;
    }

    @Nonnull
    public static JCommand<JTsGrid> toggleShowBars() {
        return ToggleShowBarsCommand.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static final class ToggleUseColorSchemeCommand extends JCommand<JTsGrid> {

        public static final ToggleUseColorSchemeCommand INSTANCE = new ToggleUseColorSchemeCommand();

        @Override
        public void execute(JTsGrid component) throws Exception {
            component.setUseColorScheme(!component.isUseColorScheme());
        }

        @Override
        public boolean isSelected(JTsGrid component) {
            return component.isUseColorScheme();
        }

        @Override
        public JCommand.ActionAdapter toAction(JTsGrid c) {
            JCommand.ActionAdapter result = super.toAction(c);
            return c instanceof Component ? result.withWeakPropertyChangeListener((Component) c, USE_COLOR_SCHEME_PROPERTY) : result;
        }
    }

    private static final class ToggleShowBarsCommand extends JCommand<JTsGrid> {

        public static final ToggleShowBarsCommand INSTANCE = new ToggleShowBarsCommand();

        @Override
        public void execute(JTsGrid component) throws Exception {
            component.setShowBars(!component.isShowBars());
        }

        @Override
        public boolean isSelected(JTsGrid component) {
            return component.isShowBars();
        }

        @Override
        public JCommand.ActionAdapter toAction(JTsGrid c) {
            JCommand.ActionAdapter result = super.toAction(c);
            return c instanceof Component ? result.withWeakPropertyChangeListener((Component) c, SHOW_BARS_PROPERTY) : result;
        }
    }
    //</editor-fold>
}

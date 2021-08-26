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
package internal.ui.components;

import demetra.ui.components.JTsGrid;
import demetra.ui.components.ComponentCommand;
import static demetra.ui.components.JTsGrid.CHRONOLOGY_PROPERTY;
import static demetra.ui.components.JTsGrid.Chronology.ASCENDING;
import static demetra.ui.components.JTsGrid.Chronology.DESCENDING;
import static demetra.ui.components.JTsGrid.MODE_PROPERTY;
import static demetra.ui.components.JTsGrid.Mode.MULTIPLETS;
import static demetra.ui.components.JTsGrid.Mode.SINGLETS;
import static demetra.ui.components.JTsGrid.ORIENTATION_PROPERTY;
import static demetra.ui.components.JTsGrid.Orientation.NORMAL;
import static demetra.ui.components.JTsGrid.Orientation.REVERSED;
import static demetra.ui.components.JTsGrid.SHOW_BARS_PROPERTY;
import static demetra.ui.components.JTsGrid.USE_COLOR_SCHEME_PROPERTY;
import ec.util.various.swing.JCommand;
import java.awt.Component;
import java.util.EnumMap;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class JTsGridCommands {

    public static final String TRANSPOSE_ACTION = "transpose";
    public static final String REVERSE_ACTION = "reverse";
    public static final String SINGLE_TS_ACTION = "singleTs";
    public static final String MULTI_TS_ACTION = "multiTs";
    public static final String TOGGLE_MODE_ACTION = "toggleMode";

    @NonNull
    public static JCommand<JTsGrid> transpose() {
        return TransposeCommand.INSTANCE;
    }

    @NonNull
    public static JCommand<JTsGrid> reverseChronology() {
        return ReverseChronologyCommand.INSTANCE;
    }

    @NonNull
    public static JCommand<JTsGrid> toggleMode() {
        return ToggleModeCommand.INSTANCE;
    }

    @NonNull
    public static JCommand<JTsGrid> applyMode(JTsGrid.Mode mode) {
        return ApplyModeCommand.VALUES.get(mode);
    }

    @NonNull
    public static JCommand<JTsGrid> toggleUseColorScheme() {
        return ToggleUseColorSchemeCommand.INSTANCE;
    }

    @NonNull
    public static JCommand<JTsGrid> toggleShowBars() {
        return ToggleShowBarsCommand.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static final class TransposeCommand extends ComponentCommand<JTsGrid> {

        public static final TransposeCommand INSTANCE = new TransposeCommand();

        public TransposeCommand() {
            super(ORIENTATION_PROPERTY);
        }

        @Override
        public boolean isSelected(JTsGrid component) {
            return component.getOrientation() == JTsGrid.Orientation.REVERSED;
        }

        @Override
        public void execute(JTsGrid c) throws Exception {
            c.setOrientation(c.getOrientation() == NORMAL ? REVERSED : NORMAL);
        }
    }

    private static final class ReverseChronologyCommand extends ComponentCommand<JTsGrid> {

        public static final ReverseChronologyCommand INSTANCE = new ReverseChronologyCommand();

        public ReverseChronologyCommand() {
            super(CHRONOLOGY_PROPERTY);
        }

        @Override
        public boolean isSelected(JTsGrid component) {
            return component.getChronology() == JTsGrid.Chronology.DESCENDING;
        }

        @Override
        public void execute(JTsGrid c) throws Exception {
            c.setChronology(c.getChronology() == ASCENDING ? DESCENDING : ASCENDING);
        }
    }

    private static final class ToggleModeCommand extends ComponentCommand<JTsGrid> {

        public static final ToggleModeCommand INSTANCE = new ToggleModeCommand();

        public ToggleModeCommand() {
            super(MODE_PROPERTY);
        }

        @Override
        public boolean isSelected(JTsGrid component) {
            return component.getMode() == JTsGrid.Mode.SINGLETS;
        }

        @Override
        public void execute(JTsGrid c) throws Exception {
            c.setMode(c.getMode() == SINGLETS ? MULTIPLETS : SINGLETS);
        }
    }

    private static final class ApplyModeCommand extends ComponentCommand<JTsGrid> {

        public static final EnumMap<JTsGrid.Mode, ApplyModeCommand> VALUES;

        static {
            VALUES = new EnumMap<>(JTsGrid.Mode.class);
            for (JTsGrid.Mode o : JTsGrid.Mode.values()) {
                VALUES.put(o, new ApplyModeCommand(o));
            }
        }
        //
        private final JTsGrid.Mode value;

        public ApplyModeCommand(JTsGrid.Mode value) {
            super(MODE_PROPERTY);
            this.value = value;
        }

        @Override
        public boolean isSelected(JTsGrid component) {
            return component.getMode() == value;
        }

        @Override
        public void execute(JTsGrid component) throws Exception {
            component.setMode(value);
        }
    }

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
            return c instanceof Component ? result.withWeakPropertyChangeListener(c, USE_COLOR_SCHEME_PROPERTY) : result;
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
            return c instanceof Component ? result.withWeakPropertyChangeListener(c, SHOW_BARS_PROPERTY) : result;
        }
    }
    //</editor-fold>
}

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
import ec.ui.interfaces.ITsGrid;
import static ec.ui.interfaces.ITsGrid.CHRONOLOGY_PROPERTY;
import static ec.ui.interfaces.ITsGrid.MODE_PROPERTY;
import static ec.ui.interfaces.ITsGrid.ORIENTATION_PROPERTY;
import static ec.ui.interfaces.ITsGrid.ZOOM_PROPERTY;
import ec.util.various.swing.JCommand;
import java.util.EnumMap;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 *
 * @author Philippe Charles
 */
@UtilityClass(ITsGrid.class)
public final class TsGridCommand {

    private TsGridCommand() {
        // static class
    }

    @NonNull
    public static JCommand<ITsGrid> transpose() {
        return TransposeCommand.INSTANCE;
    }

    @NonNull
    public static JCommand<ITsGrid> reverseChronology() {
        return ReverseChronologyCommand.INSTANCE;
    }

    @NonNull
    public static JCommand<ITsGrid> toggleMode() {
        return ToggleModeCommand.INSTANCE;
    }

    @NonNull
    public static JCommand<ITsGrid> applyMode(ITsGrid.Mode mode) {
        return ApplyModeCommand.VALUES.get(mode);
    }

    @NonNull
    public static JCommand<ITsGrid> applyZoomRatio(int zoomRatio) {
        return new ZoomRatioCommand(zoomRatio);
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static final class TransposeCommand extends ComponentCommand<ITsGrid> {

        public static final TransposeCommand INSTANCE = new TransposeCommand();

        public TransposeCommand() {
            super(ORIENTATION_PROPERTY);
        }

        @Override
        public boolean isSelected(ITsGrid component) {
            return component.getOrientation() == ITsGrid.Orientation.REVERSED;
        }

        @Override
        public void execute(ITsGrid component) throws Exception {
            component.setOrientation(component.getOrientation().transpose());
        }
    }

    private static final class ReverseChronologyCommand extends ComponentCommand<ITsGrid> {

        public static final ReverseChronologyCommand INSTANCE = new ReverseChronologyCommand();

        public ReverseChronologyCommand() {
            super(CHRONOLOGY_PROPERTY);
        }

        @Override
        public boolean isSelected(ITsGrid component) {
            return component.getChronology() == ITsGrid.Chronology.DESCENDING;
        }

        @Override
        public void execute(ITsGrid component) throws Exception {
            component.setChronology(component.getChronology().reverse());
        }
    }

    private static final class ToggleModeCommand extends ComponentCommand<ITsGrid> {

        public static final ToggleModeCommand INSTANCE = new ToggleModeCommand();

        public ToggleModeCommand() {
            super(MODE_PROPERTY);
        }

        @Override
        public boolean isSelected(ITsGrid component) {
            return component.getMode() == ITsGrid.Mode.SINGLETS;
        }

        @Override
        public void execute(ITsGrid component) throws Exception {
            component.setMode(component.getMode().toggle());
        }
    }

    private static final class ApplyModeCommand extends ComponentCommand<ITsGrid> {

        public static final EnumMap<ITsGrid.Mode, ApplyModeCommand> VALUES;

        static {
            VALUES = new EnumMap<>(ITsGrid.Mode.class);
            for (ITsGrid.Mode o : ITsGrid.Mode.values()) {
                VALUES.put(o, new ApplyModeCommand(o));
            }
        }
        //
        private final ITsGrid.Mode value;

        public ApplyModeCommand(ITsGrid.Mode value) {
            super(MODE_PROPERTY);
            this.value = value;
        }

        @Override
        public boolean isSelected(ITsGrid component) {
            return component.getMode() == value;
        }

        @Override
        public void execute(ITsGrid component) throws Exception {
            component.setMode(value);
        }
    }

    private static final class ZoomRatioCommand extends ComponentCommand<ITsGrid> {

        private final int zoomRatio;

        public ZoomRatioCommand(int zoomRatio) {
            super(ZOOM_PROPERTY);
            this.zoomRatio = zoomRatio;
        }

        @Override
        public boolean isSelected(ITsGrid component) {
            return zoomRatio == component.getZoomRatio();
        }

        @Override
        public void execute(ITsGrid component) throws Exception {
            component.setZoomRatio(zoomRatio);
        }
    }
    //</editor-fold>
}

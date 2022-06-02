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
package demetra.desktop.core.components;

import com.toedter.components.JSpinField;
import demetra.desktop.components.ComponentCommand;
import demetra.desktop.components.JTsGrowthChart;
import demetra.desktop.components.TsSelectionBridge;
import demetra.desktop.datatransfer.DataTransferManager;
import demetra.timeseries.TsCollection;
import ec.util.various.swing.JCommand;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Transferable;
import java.util.EnumMap;

import static demetra.desktop.components.JTsGrowthChart.GROWTH_KIND_PROPERTY;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class TsGrowthChartCommands {

    @NonNull
    public static JCommand<JTsGrowthChart> copyGrowthData() {
        return CopyGrowthData.INSTANCE;
    }

    @NonNull
    public static JCommand<JTsGrowthChart> applyGrowthKind(JTsGrowthChart.@NonNull GrowthKind growthKind) {
        return ApplyGrowthKind.VALUES.get(growthKind);
    }

    @NonNull
    public static JCommand<JTsGrowthChart> editLastYears() {
        return EditLastYearsCommand.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static final class CopyGrowthData extends ComponentCommand<JTsGrowthChart> {

        public static final CopyGrowthData INSTANCE = new CopyGrowthData();

        public CopyGrowthData() {
            super(TsSelectionBridge.TS_SELECTION_PROPERTY);
        }

        @Override
        public boolean isEnabled(JTsGrowthChart component) {
            return !component.getTsSelectionModel().isSelectionEmpty();
        }

        @Override
        public void execute(JTsGrowthChart c) throws Exception {
            TsCollection col = TsCollection.of(c.computeGrowthData());
            Transferable transferable = DataTransferManager.get().fromTsCollection(col);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
        }
    }

    private static final class ApplyGrowthKind extends ComponentCommand<JTsGrowthChart> {

        public static final EnumMap<JTsGrowthChart.GrowthKind, ApplyGrowthKind> VALUES;

        static {
            VALUES = new EnumMap<>(JTsGrowthChart.GrowthKind.class);
            for (JTsGrowthChart.GrowthKind o : JTsGrowthChart.GrowthKind.values()) {
                VALUES.put(o, new ApplyGrowthKind(o));
            }
        }

        private final JTsGrowthChart.GrowthKind value;

        public ApplyGrowthKind(JTsGrowthChart.GrowthKind value) {
            super(GROWTH_KIND_PROPERTY);
            this.value = value;
        }

        @Override
        public boolean isSelected(JTsGrowthChart component) {
            return component.getGrowthKind() == value;
        }

        @Override
        public void execute(JTsGrowthChart component) throws Exception {
            component.setGrowthKind(value);
        }
    }

    private static final class EditLastYearsCommand extends JCommand<JTsGrowthChart> {

        public static final EditLastYearsCommand INSTANCE = new EditLastYearsCommand();

        @Override
        public void execute(JTsGrowthChart component) throws Exception {
            JSpinField editor = new JSpinField();
            editor.setMinimum(0);
            editor.setValue(component.getLastYears());
            editor.setPreferredSize(new Dimension(100, editor.getPreferredSize().height));
            JPanel p = new JPanel(new FlowLayout());
            p.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
            p.add(editor);
            NotifyDescriptor descriptor = new NotifyDescriptor(p, "Edit last years", NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.INFORMATION_MESSAGE, null, null);
            if (DialogDisplayer.getDefault().notify(descriptor) == NotifyDescriptor.OK_OPTION) {
                component.setLastYears(editor.getValue());
            }
        }
    }
    //</editor-fold>
}

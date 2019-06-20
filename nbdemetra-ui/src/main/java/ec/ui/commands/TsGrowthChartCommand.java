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

import com.toedter.components.JSpinField;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tss.datatransfer.TssTransferSupport;
import ec.tstoolkit.design.UtilityClass;
import static ec.ui.interfaces.ITsCollectionView.SELECTION_PROPERTY;
import ec.ui.interfaces.ITsGrowthChart;
import static ec.ui.interfaces.ITsGrowthChart.GROWTH_KIND_PROPERTY;
import ec.util.various.swing.JCommand;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Transferable;
import java.util.Arrays;
import java.util.EnumMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Philippe Charles
 */
@UtilityClass(ITsGrowthChart.class)
public final class TsGrowthChartCommand {

    private TsGrowthChartCommand() {
        // static class
    }

    @NonNull
    public static JCommand<ITsGrowthChart> copyGrowthData() {
        return CopyGrowthData.INSTANCE;
    }

    @NonNull
    public static JCommand<ITsGrowthChart> applyGrowthKind(ITsGrowthChart.@NonNull GrowthKind growthKind) {
        return ApplyGrowthKind.VALUES.get(growthKind);
    }

    @NonNull
    public static JCommand<ITsGrowthChart> editLastYears() {
        return EditLastYearsCommand.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static final class CopyGrowthData extends ComponentCommand<ITsGrowthChart> {

        public static final CopyGrowthData INSTANCE = new CopyGrowthData();

        public CopyGrowthData() {
            super(SELECTION_PROPERTY);
        }

        @Override
        public boolean isEnabled(ITsGrowthChart component) {
            return component.getSelectionSize() > 0;
        }

        @Override
        public void execute(ITsGrowthChart component) throws Exception {
            TsCollection tmp = TsFactory.instance.createTsCollection();
            tmp.quietAppend(Arrays.asList(component.getGrowthData()));
            Transferable transferable = TssTransferSupport.getDefault().fromTsCollection(tmp);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(transferable, null);
        }
    }

    private static final class ApplyGrowthKind extends ComponentCommand<ITsGrowthChart> {

        public static final EnumMap<ITsGrowthChart.GrowthKind, ApplyGrowthKind> VALUES;

        static {
            VALUES = new EnumMap<>(ITsGrowthChart.GrowthKind.class);
            for (ITsGrowthChart.GrowthKind o : ITsGrowthChart.GrowthKind.values()) {
                VALUES.put(o, new ApplyGrowthKind(o));
            }
        }
        //
        private final ITsGrowthChart.GrowthKind value;

        public ApplyGrowthKind(ITsGrowthChart.GrowthKind value) {
            super(GROWTH_KIND_PROPERTY);
            this.value = value;
        }

        @Override
        public boolean isSelected(ITsGrowthChart component) {
            return component.getGrowthKind() == value;
        }

        @Override
        public void execute(ITsGrowthChart component) throws Exception {
            component.setGrowthKind(value);
        }
    }

    private static final class EditLastYearsCommand extends JCommand<ITsGrowthChart> {

        public static final EditLastYearsCommand INSTANCE = new EditLastYearsCommand();

        @Override
        public void execute(ITsGrowthChart component) throws Exception {
            JSpinField editor = new JSpinField();
            editor.setMinimum(0);
            editor.setValue(component.getLastYears());
            editor.setPreferredSize(new Dimension(100, editor.getPreferredSize().height));
            JPanel p = new JPanel(new FlowLayout());
            p.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
            p.add(editor);
            NotifyDescriptor descriptor = new NotifyDescriptor(p, "Edit last years", NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.INFORMATION_MESSAGE, null, null);;
            if (DialogDisplayer.getDefault().notify(descriptor) == NotifyDescriptor.OK_OPTION) {
                component.setLastYears(editor.getValue());
            }
        }
    }
    //</editor-fold>
}

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

import com.google.common.base.Objects;
import ec.nbdemetra.ui.properties.DataFormatComponent2;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tstoolkit.design.UtilityClass;
import ec.ui.interfaces.ITsControl;
import ec.ui.interfaces.ITsPrinter;
import ec.util.various.swing.JCommand;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Philippe Charles
 */
@UtilityClass(ITsControl.class)
public final class TsControlCommand {

    private TsControlCommand() {
        // static class
    }

    @Nonnull
    public static JCommand<ITsControl> printPreview() {
        return PrintPreviewCommand.INSTANCE;
    }

    @Nonnull
    public static JCommand<ITsControl> applyDataFormat(@Nullable DataFormat dataFormat) {
        return new ApplyDataFormatCommand(dataFormat);
    }

    @Nonnull
    public static JCommand<ITsControl> editDataFormat() {
        return EditDataFormatCommand.INSTANCE;
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation">
    private static final class PrintPreviewCommand extends JCommand<ITsControl> {

        public static final PrintPreviewCommand INSTANCE = new PrintPreviewCommand();

        @Override
        public void execute(ITsControl component) throws Exception {
            ITsPrinter printer = component.getPrinter();
            if (printer != null) {
                printer.printPreview();
            }
        }
    }

    private static final class ApplyDataFormatCommand extends ComponentCommand<ITsControl> {

        private final DataFormat dataFormat;

        public ApplyDataFormatCommand(DataFormat dataFormat) {
            super(ITsControl.DATA_FORMAT_PROPERTY);
            this.dataFormat = dataFormat;
        }

        @Override
        public void execute(ITsControl component) throws Exception {
            component.setDataFormat(dataFormat);
        }

        @Override
        public boolean isSelected(ITsControl component) {
            return Objects.equal(dataFormat, component.getDataFormat());
        }
    }

    private static final class EditDataFormatCommand extends JCommand<ITsControl> {

        public static final EditDataFormatCommand INSTANCE = new EditDataFormatCommand();

        @Override
        public void execute(final ITsControl component) {
            final DataFormatComponent2 editor = new DataFormatComponent2();
            Dimension preferedSize = editor.getPreferredSize();
            editor.setPreferredSize(new Dimension(400, preferedSize.height));
            JPanel p = new JPanel(new FlowLayout());
            p.setBorder(BorderFactory.createEmptyBorder(25, 10, 10, 10));
            p.add(editor);
            NotifyDescriptor descriptor = new NotifyDescriptor(p, "Edit data format", NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.INFORMATION_MESSAGE, null, null);
            descriptor.addPropertyChangeListener(evt -> {
                String p1 = evt.getPropertyName();
                if (p1.equals("value")) {
                    editor.setPreviewVisible(false);
                }
            });
            editor.setDataFormat(component.getDataFormat());
            if (component.getDataFormat() != null) {
                JButton b = new JButton(new ApplyDataFormatCommand(null).toAction(component));
                b.setText("Restore");
                descriptor.setAdditionalOptions(new Object[]{b});
            }
            if (DialogDisplayer.getDefault().notify(descriptor) == NotifyDescriptor.OK_OPTION && !editor.getDataFormat().equals(component.getDataFormat())) {
                component.setDataFormat(editor.getDataFormat());
            }
        }
    }
    //</editor-fold>
}

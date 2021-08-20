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

import demetra.bridge.TsConverter;
import demetra.tsprovider.util.ObsFormat;
import demetra.ui.DemetraOptions;
import demetra.ui.components.parts.HasObsFormat;
import ec.nbdemetra.ui.properties.DataFormatComponent2;
import demetra.ui.components.ComponentCommand;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class HasObsFormatCommands {

    public static final String FORMAT_ACTION = "format";

    @NonNull
    public static JCommand<HasObsFormat> applyDataFormat(@Nullable ObsFormat dataFormat) {
        return new ApplyDataFormatCommand(dataFormat);
    }

    @NonNull
    public static JCommand<HasObsFormat> editDataFormat() {
        return EditDataFormatCommand.INSTANCE;
    }

    public static JMenuItem newEditFormatMenu(ActionMap am, DemetraOptions demetraUI) {
        JMenuItem result = new JMenuItem(am.get(FORMAT_ACTION));
        result.setText("Edit format...");
        result.setIcon(demetraUI.getPopupMenuIcon(FontAwesome.FA_GLOBE));
        return result;
    }

    private static final class ApplyDataFormatCommand extends ComponentCommand<HasObsFormat> {

        private final ObsFormat dataFormat;

        public ApplyDataFormatCommand(ObsFormat dataFormat) {
            super(HasObsFormat.OBS_FORMAT_PROPERTY);
            this.dataFormat = dataFormat;
        }

        @Override
        public void execute(HasObsFormat component) throws Exception {
            component.setObsFormat(dataFormat);
        }

        @Override
        public boolean isSelected(HasObsFormat component) {
            return Objects.equals(dataFormat, component.getObsFormat());
        }
    }

    private static final class EditDataFormatCommand extends JCommand<HasObsFormat> {

        public static final EditDataFormatCommand INSTANCE = new EditDataFormatCommand();

        @Override
        public void execute(final HasObsFormat component) {
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
            editor.setDataFormat(TsConverter.fromObsFormat(component.getObsFormat()));
            if (component.getObsFormat() != null) {
                JButton b = new JButton(new ApplyDataFormatCommand(null).toAction(component));
                b.setText("Restore");
                descriptor.setAdditionalOptions(new Object[]{b});
            }
            if (DialogDisplayer.getDefault().notify(descriptor) == NotifyDescriptor.OK_OPTION && !editor.getDataFormat().equals(component.getObsFormat())) {
                component.setObsFormat(TsConverter.toObsFormat(editor.getDataFormat()));
            }
        }
    }
}

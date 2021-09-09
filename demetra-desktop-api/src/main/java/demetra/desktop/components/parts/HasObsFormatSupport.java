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
package demetra.desktop.components.parts;

import demetra.desktop.DemetraIcons;
import demetra.desktop.beans.PropertyChangeBroadcaster;
import demetra.desktop.components.ComponentCommand;
import demetra.desktop.components.JObsFormatComponent;
import demetra.tsprovider.util.ObsFormat;
import ec.util.various.swing.FontAwesome;
import ec.util.various.swing.JCommand;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 *
 * @author Philippe Charles
 */
@lombok.experimental.UtilityClass
public class HasObsFormatSupport {

    @NonNull
    public static HasObsFormat of(@NonNull PropertyChangeBroadcaster broadcaster) {
        return new HasObsFormatImpl(broadcaster);
    }

    public static void registerActions(HasObsFormat component, ActionMap am) {
        am.put(HasObsFormat.EDIT_FORMAT_ACTION, EditDataFormatCommand.INSTANCE.toAction(component));
    }

    @NonNull
    public static <C extends JComponent & HasObsFormat> JMenuItem newApplyFormatMenu(@NonNull C component, @Nullable ObsFormat format) {
        JCheckBoxMenuItem result = new JCheckBoxMenuItem(new ApplyDataFormatCommand(format).toAction(component));
        result.setText(format != null ? format.toString() : "Default");
        return result;
    }

    public static <C extends JComponent & HasObsFormat> JMenuItem newEditFormatMenu(C component) {
        JMenuItem result = new JMenuItem(component.getActionMap().get(HasObsFormat.EDIT_FORMAT_ACTION));
        result.setText("Edit format...");
        result.setIcon(DemetraIcons.getPopupMenuIcon(FontAwesome.FA_GLOBE));
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
            final JObsFormatComponent editor = new JObsFormatComponent();
            Dimension preferredSize = editor.getPreferredSize();
            editor.setPreferredSize(new Dimension(400, preferredSize.height));
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
            editor.setObsFormat(component.getObsFormat());
            if (component.getObsFormat() != null) {
                JButton b = new JButton(new ApplyDataFormatCommand(null).toAction(component));
                b.setText("Restore");
                descriptor.setAdditionalOptions(new Object[]{b});
            }
            if (DialogDisplayer.getDefault().notify(descriptor) == NotifyDescriptor.OK_OPTION && !editor.getObsFormat().equals(component.getObsFormat())) {
                component.setObsFormat(editor.getObsFormat());
            }
        }
    }

    @lombok.RequiredArgsConstructor
    private static final class HasObsFormatImpl implements HasObsFormat {

        @lombok.NonNull
        private final PropertyChangeBroadcaster broadcaster;
        private ObsFormat obsFormat = null;

        @Override
        public ObsFormat getObsFormat() {
            return obsFormat;
        }

        @Override
        public void setObsFormat(ObsFormat obsFormat) {
            ObsFormat old = this.obsFormat;
            this.obsFormat = obsFormat;
            broadcaster.firePropertyChange(OBS_FORMAT_PROPERTY, old, this.obsFormat);
        }
    }
}

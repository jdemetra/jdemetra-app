/*
 * Copyright 2017 National Bank of Belgium
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they will be approved
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
package demetra.desktop.sa.multiprocessing.actions;

import demetra.desktop.sa.multiprocessing.ui.MultiProcessingManager;
import demetra.desktop.sa.multiprocessing.ui.SaBatchUI;
import demetra.desktop.sa.multiprocessing.ui.SaNode;
import demetra.desktop.ui.ActiveViewAction;
import demetra.sa.SaItem;
import java.awt.Dimension;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(category = "SaProcessing",
        id = "demetra.desktop.sa.multiprocessing.actions.CommentSaItem")
@ActionRegistration(displayName = "#CTL_CommentSaItem", lazy = false)
@ActionReferences({
    @ActionReference(path = MultiProcessingManager.LOCALPATH, position = 2100),
    @ActionReference(path = MultiProcessingManager.CONTEXTPATH, position = 2100)
})
@Messages("CTL_CommentSaItem=Edit comments")
public final class CommentSaItem extends ActiveViewAction<SaBatchUI> {

    public static final String TITLE = "Edit comments";

    public CommentSaItem() {
        super(SaBatchUI.class);
        refreshAction();
        putValue(NAME, Bundle.CTL_CommentSaItem());
    }

    @Override
    protected void process(SaBatchUI cur) {
        SaNode item = cur.getSelection()[0];
        if (item != null) {
            SaItem output = item.getOutput();
            if (output != null) {
                JTextArea area = new JTextArea(output.getComment());
                area.setLineWrap(true);
                area.setWrapStyleWord(true);
                JScrollPane scroll = new JScrollPane(area);
                scroll.setPreferredSize(new Dimension(250, 150));

                NotifyDescriptor nd = new NotifyDescriptor(scroll,
                        TITLE,
                        NotifyDescriptor.OK_CANCEL_OPTION,
                        NotifyDescriptor.PLAIN_MESSAGE,
                        null,
                        NotifyDescriptor.OK_OPTION);

                if (DialogDisplayer.getDefault().notify(nd) != NotifyDescriptor.OK_OPTION) {
                    return;
                }
                item.setOutput(output.withComment(area.getText().trim()));
            }
        }
    }

    @Override
    protected void refreshAction() {
        SaBatchUI ui = context();
        enabled = ui != null && ui.getSelectionCount() == 1;
    }
}

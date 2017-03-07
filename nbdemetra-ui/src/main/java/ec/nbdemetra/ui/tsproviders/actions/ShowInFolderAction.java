/*
 * Copyright 2017 National Bank of Belgium
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
package ec.nbdemetra.ui.tsproviders.actions;

import com.google.common.base.Optional;
import ec.nbdemetra.ui.nodes.SingleNodeAction;
import ec.nbdemetra.ui.tsproviders.DataSourceNode;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.TsProviders;
import ec.util.desktop.Desktop;
import ec.util.desktop.DesktopManager;
import java.io.File;
import java.io.IOException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

/**
 * Launch the default file manager and select the file used as a source by the
 * selected {@link DataSource}
 * .<p>
 * This action is enabled if these two conditions are satisfied: <ul><li>the
 * {@link Desktop.Action.SHOW_IN_FOLDER} action is supported <li>the selected
 * {@link DataSource} uses a file as source
 * </ul>
 *
 * @see Desktop#showInFolder(java.io.File)
 * @see TsProviders#tryGetFile(ec.tss.tsproviders.DataSource)
 * @author Philippe Charles
 */
@ActionID(category = "Edit", id = "ec.nbdemetra.ui.tsproviders.ShowInFolderAction")
@ActionRegistration(displayName = "#CTL_ShowInFolderAction", lazy = false)
@Messages("CTL_ShowInFolderAction=Show in folder")
public final class ShowInFolderAction extends SingleNodeAction<DataSourceNode> {

    public ShowInFolderAction() {
        super(DataSourceNode.class);
    }

    @Override
    protected void performAction(DataSourceNode activatedNode) {
        Optional<File> file = getFile(activatedNode);
        if (file.isPresent()) {
            try {
                getDesktop().showInFolder(file.get());
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    protected boolean enable(DataSourceNode activatedNode) {
        return getDesktop().isSupported(Desktop.Action.SHOW_IN_FOLDER)
                && getFile(activatedNode).isPresent();
    }

    @Override
    public String getName() {
        return Bundle.CTL_ShowInFolderAction();
    }

    private static Desktop getDesktop() {
        return DesktopManager.get();
    }

    private static Optional<File> getFile(DataSourceNode activatedNode) {
        return TsProviders.tryGetFile(activatedNode.getLookup().lookup(DataSource.class));
    }
}

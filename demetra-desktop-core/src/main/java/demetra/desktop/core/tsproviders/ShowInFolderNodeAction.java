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
package demetra.desktop.core.tsproviders;

import demetra.desktop.TsManager;
import demetra.desktop.actions.AbilityNodeAction;
import demetra.desktop.actions.Actions;
import demetra.tsprovider.DataSource;
import ec.util.desktop.Desktop;
import ec.util.desktop.DesktopManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;
import javax.swing.JMenuItem;
import org.openide.util.actions.Presenter;

/**
 * Launch the default file manager and select the file used as a source by the
 * selected {@link DataSource}
 * .<p>
 * This action is enabled if these two conditions are satisfied: <ul><li>the
 * {@link Desktop.Action#SHOW_IN_FOLDER} action is supported <li>the selected
 * {@link DataSource} uses a file as source
 * </ul>
 *
 * @author Philippe Charles
 * @see Desktop#showInFolder(java.io.File)
 */
@ActionID(category = "Edit", id = ShowInFolderNodeAction.ID)
@ActionRegistration(displayName = "#CTL_ShowInFolderAction", lazy = false)
@Messages("CTL_ShowInFolderAction=Show in folder")
public final class ShowInFolderNodeAction extends AbilityNodeAction<DataSource> implements Presenter.Popup {

    public static final String ID = "demetra.desktop.core.tsproviders.ShowInFolderAction";

    public ShowInFolderNodeAction() {
        super(DataSource.class, true);
    }

    @Override
    public JMenuItem getPopupPresenter() {
        return Actions.hideWhenDisabled(new JMenuItem(this));
    }

    @Override
    protected void performAction(Stream<DataSource> items) {
        items.forEach(item -> {
            Optional<File> file = TsManager.getDefault().getFile(item);
            if (file.isPresent()) {
                try {
                    DesktopManager.get().showInFolder(file.get());
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }

    @Override
    protected boolean enable(Stream<DataSource> items) {
        return DesktopManager.get().isSupported(Desktop.Action.SHOW_IN_FOLDER)
                && items.anyMatch(item -> TsManager.getDefault().getFile(item).isPresent());
    }

    @Override
    public String getName() {
        return Bundle.CTL_ShowInFolderAction();
    }
}

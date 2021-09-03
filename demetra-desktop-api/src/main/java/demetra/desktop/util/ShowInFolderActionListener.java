/*
 * Copyright 2016 National Bank of Belgium
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
package demetra.desktop.util;

import ec.util.desktop.Desktop;
import ec.util.desktop.DesktopManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.util.Exceptions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Philippe Charles
 * @since 2.2.0
 */
public final class ShowInFolderActionListener implements ActionListener {

    @NonNull
    public static ShowInFolderActionListener of(@NonNull File file) {
        return new ShowInFolderActionListener(DesktopManager.get(), file);
    }

    private final Desktop desktop;
    private final File file;

    private ShowInFolderActionListener(Desktop desktop, File file) {
        this.desktop = desktop;
        this.file = file;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (desktop.isSupported(Desktop.Action.SHOW_IN_FOLDER)) {
            try {
                desktop.showInFolder(file);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}

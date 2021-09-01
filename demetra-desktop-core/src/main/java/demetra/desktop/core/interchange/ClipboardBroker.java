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
package demetra.desktop.core.interchange;

import demetra.desktop.interchange.Exportable;
import demetra.desktop.interchange.Importable;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import demetra.desktop.interchange.InterchangeSpi;

/**
 *
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider
public final class ClipboardBroker implements InterchangeSpi {

    @Override
    public int getPosition() {
        return 100;
    }

    @Override
    public String getName() {
        return "Clipboard";
    }

    private Clipboard getClipboard() {
        return Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    @Override
    public boolean canImport(List<? extends Importable> importables) {
        try {
            String xml = readString(getClipboard());
            if (xml == null) {
                return false;
            }
            Configs configs = Configs.xmlParser().parseChars(xml);
            return configs != null && configs.canImport(importables);
        } catch (IOException ex) {
            return false;
        }
    }

    @Override
    public void performImport(List<? extends Importable> importables) throws IOException, IllegalArgumentException {
        Configs configs = load(getClipboard());
        configs.performImport(importables);
    }

    @Override
    public boolean canExport(List<? extends Exportable> exportables) {
        return !exportables.isEmpty();
    }

    @Override
    public void performExport(List<? extends Exportable> exportables) throws IOException {
        Configs configs = Configs.fromExportables(exportables);
        store(getClipboard(), configs);
    }

    @NonNull
    private static Configs load(@NonNull Clipboard clipboard) throws IOException {
        String xml = readString(clipboard);
        if (xml == null) {
            throw new IOException("Not string input");
        }
        Configs result = Configs.xmlParser().parseChars(xml);
        if (result == null) {
            throw new IOException("Cannot parse configs");
        }
        return result;
    }

    private static void store(@NonNull Clipboard clipboard, @NonNull Configs configs) throws IOException {
        String xml = Configs.xmlFormatter(true).formatToString(configs);
        if (xml == null) {
            throw new IOException("Cannot format configs");
        }
        clipboard.setContents(new StringSelection(xml), null);
    }

    private static String readString(@NonNull Clipboard clipboard) throws IOException {
        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            try {
                return (String) clipboard.getData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException ex) {
                throw new IOException(ex);
            }
        }
        return null;
    }
}

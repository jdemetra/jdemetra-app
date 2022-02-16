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
import demetra.desktop.interchange.InterchangeSpi;
import nbbrd.design.DirectImpl;
import nbbrd.service.ServiceProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openide.filesystems.FileChooserBuilder;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider
public final class FileBroker implements InterchangeSpi {

    private final JFileChooser fileChooser;

    public FileBroker() {
        this.fileChooser = new FileChooserBuilder(FileBroker.class)
                .setAcceptAllFileFilterUsed(false)
                .setFileFilter(new FileNameExtensionFilter("Configs files", "cfgx"))
                .setFilesOnly(true)
                .createFileChooser();
    }

    @Override
    public int getPosition() {
        return 300;
    }

    @Override
    public String getName() {
        return "File";
    }

    @Override
    public String getDisplayName() {
        return "File...";
    }

    @Override
    public boolean canImport(List<? extends Importable> importables) {
        return true;
    }

    @Override
    public void performImport(List<? extends Importable> importables) throws IOException {
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            Configs configs = load(fileChooser.getSelectedFile().toPath());
            configs.performImport(importables);
        }
    }

    @Override
    public boolean canExport(List<? extends Exportable> exportables) {
        return !exportables.isEmpty();
    }

    @Override
    public void performExport(List<? extends Exportable> exportables) throws IOException {
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            Configs configs = Configs.fromExportables(exportables);
            store(enforceExtension(fileChooser.getSelectedFile()).toPath(), configs);
        }
    }

    private File enforceExtension(File input) {
        int index = input.getPath().toLowerCase(Locale.ROOT).indexOf(".cfgx");
        return index != -1 ? input : new File(input.getPath() + ".cfgx");
    }

    @NonNull
    public static Configs load(@NonNull Path file) throws IOException {
        try (InputStream stream = Files.newInputStream(file, StandardOpenOption.READ)) {
            try (GZIPInputStream gz = new GZIPInputStream(stream)) {
                return Configs.xmlParser().parseStream(gz, StandardCharsets.UTF_8);
            }
        }
    }

    public static void store(@NonNull Path file, @NonNull Configs configs) throws IOException {
        try (OutputStream stream = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
            try (GZIPOutputStream gz = new GZIPOutputStream(stream)) {
                Configs.xmlFormatter(true).formatStream(configs, gz, StandardCharsets.UTF_8);
            }
        }
    }
}

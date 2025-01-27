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
package ec.nbdemetra.ui.interchange.impl;

import ec.nbdemetra.ui.interchange.Exportable;
import ec.nbdemetra.ui.interchange.Importable;
import ec.nbdemetra.ui.interchange.InterchangeBroker;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.checkerframework.checker.nullness.qual.NonNull;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = InterchangeBroker.class, position = 300)
public class FileBroker extends InterchangeBroker {

    private final JFileChooser fileChooser;

    public FileBroker() {
        this.fileChooser = new FileChooserBuilder(FileBroker.class)
                .setAcceptAllFileFilterUsed(false)
                .setFileFilter(new FileNameExtensionFilter("Configs files", "cfgx"))
                .setFilesOnly(true)
                .createFileChooser();
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
        return index != -1 ? input : Paths.get(input.getPath() + ".cfgx").toFile();
    }

    @NonNull
    public static Configs load(@NonNull Path file) throws IOException {
        CharSequence xml = readGZIP(file);
        Configs result = Configs.xmlParser().parse(xml);
        if (result == null) {
            throw new IOException("Cannot parse configs");
        }
        return result;
    }

    public static void store(@NonNull Path file, @NonNull Configs configs) throws IOException {
        CharSequence xml = Configs.xmlFormatter(true).format(configs);
        if (xml == null) {
            throw new IOException("Cannot format configs");
        }
        writeGZIP(file, xml);
    }

    private static CharSequence readGZIP(Path file) throws IOException {
        try (InputStream stream = Files.newInputStream(file, StandardOpenOption.READ)) {
            try (GZIPInputStream gz = new GZIPInputStream(stream)) {
                try (InputStreamReader reader = new InputStreamReader(gz, StandardCharsets.UTF_8)) {
                    StringBuilder result = new StringBuilder();
                    try (BufferedReader buff = new BufferedReader(reader)) {
                        String line;
                        while ((line = buff.readLine()) != null) {
                            result.append(line);
                        }
                    }
                    return result;
                }
            }
        }
    }

    private static void writeGZIP(Path file, CharSequence xml) throws IOException {
        try (OutputStream stream = Files.newOutputStream(file, StandardOpenOption.CREATE)) {
            try (GZIPOutputStream gz = new GZIPOutputStream(stream)) {
                try (OutputStreamWriter writer = new OutputStreamWriter(gz, StandardCharsets.UTF_8)) {
                    writer.append(xml);
                }
            }
        }
    }
}

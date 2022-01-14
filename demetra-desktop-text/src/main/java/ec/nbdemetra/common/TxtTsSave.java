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
package ec.nbdemetra.common;

import demetra.desktop.Config;
import demetra.desktop.TsActionsSaveSpi;
import demetra.desktop.TsManager;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import demetra.desktop.util.SingleFileExporter;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsInformationType;
import demetra.desktop.DemetraIcons;
import demetra.desktop.datatransfer.ts.TxtDataTransfer;
import ec.util.various.swing.OnAnyThread;
import ec.util.various.swing.OnEDT;
import internal.demetra.tsp.text.TxtFileFilter;
import nbbrd.io.text.Formatter;
import nbbrd.service.ServiceProvider;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;

import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

/**
 * @author Philippe Charles
 * @since 2.2.0
 */
@ServiceProvider
public final class TxtTsSave implements TsActionsSaveSpi {

    private final FileChooserBuilder fileChooser;
    private final OptionsBean options;

    public TxtTsSave() {
        this.fileChooser = SingleFileExporter.newFileChooser(TxtTsSave.class).setFileFilter(new SaveFileFilter());
        this.options = new OptionsBean();
    }

    @Override
    public String getName() {
        return "TxtTsSave";
    }

    @Override
    public String getDisplayName() {
        return "Text file";
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.icon2Image(DemetraIcons.PUZZLE_16);
    }

    @Override
    public void save(List<TsCollection> input) {
        SingleFileExporter.saveToFile(fileChooser, o -> editBean(options), o -> store(input, o, options));
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    @OnEDT
    private static boolean editBean(OptionsBean bean) {
        return new PropertySheetDialogBuilder().title("Options").editSheet(getSheet(bean));
    }

    @OnEDT
    private static void store(List<TsCollection> data, File file, OptionsBean opts) {
        SingleFileExporter
                .builder()
                .file(file)
                .progressLabel("Saving to text file")
                .onErrorNotify("Saving to text file failed")
                .onSuccessNotify("Text file saved")
                .build()
                .execAsync((f, ph) -> store(data, f, opts, ph));
    }

    @OnAnyThread
    private static void store(List<TsCollection> data, File file, OptionsBean options, ProgressHandle ph) throws IOException {
        ph.start();
        ph.progress("Loading time series");
        TsCollection content = data
                .stream()
                .map(col -> col.load(TsInformationType.All, TsManager.getDefault()))
                .flatMap(TsCollection::stream)
                .collect(TsCollection.toTsCollection());

        ph.progress("Creating content");
        TxtDataTransfer handler = new TxtDataTransfer();
        Config config = handler.getConfig().toBuilder()
                .parameter("beginPeriod", Formatter.onBoolean().formatAsString(options.beginPeriod))
                .parameter("showDates", Formatter.onBoolean().formatAsString(options.showDates))
                .parameter("showTitle", Formatter.onBoolean().formatAsString(options.showTitle))
                .parameter("vertical", Formatter.onBoolean().formatAsString(options.vertical))
                .build();
        handler.setConfig(config);
        String stringContent = handler.tsCollectionToString(content);

        ph.progress("Writing file");
        Files.write(file.toPath(), Collections.singleton(stringContent), StandardCharsets.UTF_8);
    }

    private static final class SaveFileFilter extends FileFilter {

        private final TxtFileFilter delegate = new TxtFileFilter();

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || delegate.accept(f);
        }

        @Override
        public String getDescription() {
            return delegate.getFileDescription();
        }
    }

    public static final class OptionsBean {

        public boolean vertical = true;
        public boolean showDates = true;
        public boolean showTitle = true;
        public boolean beginPeriod = true;
    }

    private static Sheet getSheet(OptionsBean bean) {
        Sheet result = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.withBoolean().selectField(bean, "vertical").display("Vertical alignment").add();
        b.withBoolean().selectField(bean, "showDates").display("Include date headers").add();
        b.withBoolean().selectField(bean, "showTitle").display("Include title headers").add();
        b.withBoolean().selectField(bean, "beginPeriod").display("Begin period").add();
        result.put(b.build());

        return result;
    }
    //</editor-fold>
}

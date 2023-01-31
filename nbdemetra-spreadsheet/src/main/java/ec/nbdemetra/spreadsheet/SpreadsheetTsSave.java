/*
 * Copyright 2015 National Bank of Belgium
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
package ec.nbdemetra.spreadsheet;

import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.properties.PropertySheetDialogBuilder;
import ec.nbdemetra.ui.SingleFileExporter;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.tssave.ITsSave;
import ec.nbdemetra.ui.tssave.TsSaveUtil;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsCollectionInformation;
import ec.tss.TsInformationType;
import ec.tss.tsproviders.spreadsheet.engine.SpreadSheetFactory;
import ec.tss.tsproviders.spreadsheet.engine.TsExportOptions;
import ec.util.spreadsheet.Book;
import ec.util.spreadsheet.BookFactoryLoader;
import ec.util.spreadsheet.helpers.ArraySheet;
import ec.util.various.swing.OnAnyThread;
import ec.util.various.swing.OnEDT;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = ITsSave.class)
public final class SpreadsheetTsSave implements ITsSave {

    private final FileChooserBuilder fileChooser;
    private final OptionsBean options;

    public SpreadsheetTsSave() {
        this.fileChooser = TsSaveUtil.fileChooser(SpreadsheetTsSave.class, new SaveFileFilter());
        this.options = new OptionsBean();
    }

    @Override
    public String getName() {
        return "SpreadsheetTsSave";
    }

    @Override
    public String getDisplayName() {
        return "Spreadsheet file";
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }

    @Override
    public void save(Ts[] input) {
        save(TsSaveUtil.toCollections(input));
    }

    @Override
    public void save(TsCollection[] input) {
        TsSaveUtil.saveToFile(fileChooser, o -> editBean(options), o -> store(input, o, options));
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    @OnEDT
    private static boolean editBean(OptionsBean bean) {
        return new PropertySheetDialogBuilder().title("Options").editSheet(getSheet(bean));
    }

    @OnEDT
    private static void store(TsCollection[] data, File file, OptionsBean opts) {
        new SingleFileExporter()
                .file(file)
                .progressLabel("Saving to spreadsheet")
                .onErrorNotify("Saving to spreadsheet failed")
                .onSussessNotify("Spreadsheet saved")
                .execAsync((f, ph) -> store(data, f, opts.getTsExportOptions(), ph));
    }

    @OnAnyThread
    private static void store(TsCollection[] data, File file, TsExportOptions options, ProgressHandle ph) throws IOException {
        ph.progress("Loading time series");
        TsCollectionInformation content = new TsCollectionInformation();
        for (TsCollection col : data) {
            col.load(TsInformationType.All);
            col.stream().map(o -> o.toInfo(TsInformationType.All)).forEach(content.items::add);
        }

        ph.progress("Creating content");
        ArraySheet sheet = SpreadSheetFactory.getDefault().fromTsCollectionInfo(content, options);

        ph.progress("Writing file");
        getFactoryByFile(file)
                .orElseThrow(() -> new IOException("Cannot find spreadsheet factory for file '" + file + "'"))
                .store(file, sheet.toBook());
    }

    private static Optional<? extends Book.Factory> getFactoryByFile(File file) {
        return BookFactoryLoader.get()
                .stream()
                .filter(factory -> factory.canStore() && factory.accept(file))
                .findFirst();
    }

    private static final class SaveFileFilter extends FileFilter {

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || getFactoryByFile(f).isPresent();
        }

        @Override
        public String getDescription() {
            return "Spreadsheet file";
        }
    }

    public static final class OptionsBean {

        public boolean vertical = true;
        public boolean showDates = true;
        public boolean showTitle = true;
        public boolean beginPeriod = true;

        private TsExportOptions getTsExportOptions() {
            return TsExportOptions.create(vertical, showDates, showTitle, beginPeriod);
        }
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

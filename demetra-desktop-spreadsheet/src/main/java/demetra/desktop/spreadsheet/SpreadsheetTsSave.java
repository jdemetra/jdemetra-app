///*
// * Copyright 2015 National Bank of Belgium
// * 
// * Licensed under the EUPL, Version 1.1 or - as soon they will be approved 
// * by the European Commission - subsequent versions of the EUPL (the "Licence");
// * You may not use this work except in compliance with the Licence.
// * You may obtain a copy of the Licence at:
// * 
// * http://ec.europa.eu/idabc/eupl
// * 
// * Unless required by applicable law or agreed to in writing, software 
// * distributed under the Licence is distributed on an "AS IS" basis,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the Licence for the specific language governing permissions and 
// * limitations under the Licence.
// */
//package demetra.desktop.spreadsheet;
//
//import demetra.timeseries.TsCollection;
//import demetra.timeseries.TsInformationType;
//import demetra.desktop.DemetraIcons;
//import demetra.desktop.properties.PropertySheetDialogBuilder;
//import demetra.desktop.util.SingleFileExporter;
//import demetra.desktop.properties.NodePropertySetBuilder;
//import ec.util.spreadsheet.Book;
//import ec.util.spreadsheet.helpers.ArraySheet;
//import ec.util.various.swing.OnAnyThread;
//import ec.util.various.swing.OnEDT;
//import java.awt.Image;
//import java.io.File;
//import java.io.IOException;
//import java.util.List;
//import java.util.Optional;
//import javax.swing.filechooser.FileFilter;
//import org.netbeans.api.progress.ProgressHandle;
//import org.openide.filesystems.FileChooserBuilder;
//import org.openide.nodes.Sheet;
//import org.openide.util.ImageUtilities;
//import org.openide.util.Lookup;
//import org.openide.util.lookup.ServiceProvider;
//import demetra.desktop.TsActionsSaveSpi;
//import demetra.desktop.TsManager;
//
///**
// *
// * @author Philippe Charles
// */
//@ServiceProvider(service = TsActionsSaveSpi.class)
//public final class SpreadsheetTsSave implements TsActionsSaveSpi {
//
//    private final FileChooserBuilder fileChooser;
//    private final OptionsBean options;
//
//    public SpreadsheetTsSave() {
//        this.fileChooser = SingleFileExporter.newFileChooser(SpreadsheetTsSave.class).setFileFilter(new SaveFileFilter());
//        this.options = new OptionsBean();
//    }
//
//    @Override
//    public String getName() {
//        return "SpreadsheetTsSave";
//    }
//
//    @Override
//    public String getDisplayName() {
//        return "Spreadsheet file";
//    }
//
//    @Override
//    public Image getIcon(int type, boolean opened) {
//        return ImageUtilities.icon2Image(DemetraIcons.PUZZLE_16);
//    }
//
//    @Override
//    public void save(List<TsCollection> input) {
//        SingleFileExporter.saveToFile(fileChooser, o -> editBean(options), o -> store(input, o, options));
//    }
//
//    //<editor-fold defaultstate="collapsed" desc="Implementation details">
//    @OnEDT
//    private static boolean editBean(OptionsBean bean) {
//        return new PropertySheetDialogBuilder().title("Options").editSheet(getSheet(bean));
//    }
//
//    @OnEDT
//    private static void store(List<TsCollection> data, File file, OptionsBean opts) {
//        SingleFileExporter
//                .builder()
//                .file(file)
//                .progressLabel("Saving to spreadsheet")
//                .onErrorNotify("Saving to spreadsheet failed")
//                .onSuccessNotify("Spreadsheet saved")
//                .build()
//                .execAsync((f, ph) -> store(data, f, opts.getTsExportOptions(), ph));
//    }
//
//    @OnAnyThread
//    private static void store(List<TsCollection> data, File file, TsExportOptions options, ProgressHandle ph) throws IOException {
//        ph.progress("Loading time series");
//        TsCollection content = data
//                .stream()
//                .map(col -> col.load(TsInformationType.All, TsManager.getDefault()))
//                .flatMap(TsCollection::stream)
//                .collect(TsCollection.toTsCollection());
//
//        ph.progress("Creating content");
//        ArraySheet sheet = SpreadSheetFactory.getDefault().fromTsCollectionInfo(demetra.bridge.TsConverter.fromTsCollectionBuilder(content), options);
//
//        ph.progress("Writing file");
//        getFactoryByFile(file)
//                .orElseThrow(() -> new IOException("Cannot find spreadsheet factory"))
//                .store(file, sheet.toBook());
//    }
//
//    private static Optional<? extends Book.Factory> getFactoryByFile(File file) {
//        return Lookup.getDefault().lookupAll(Book.Factory.class).stream().filter(o -> o.canStore() && o.accept(file)).findFirst();
//    }
//
//    private static final class SaveFileFilter extends FileFilter {
//
//        @Override
//        public boolean accept(File f) {
//            return f.isDirectory() || getFactoryByFile(f).isPresent();
//        }
//
//        @Override
//        public String getDescription() {
//            return "Spreadsheet file";
//        }
//    }
//
//    public static final class OptionsBean {
//
//        public boolean vertical = true;
//        public boolean showDates = true;
//        public boolean showTitle = true;
//        public boolean beginPeriod = true;
//
//        private TsExportOptions getTsExportOptions() {
//            return TsExportOptions.create(vertical, showDates, showTitle, beginPeriod);
//        }
//    }
//
//    private static Sheet getSheet(OptionsBean bean) {
//        Sheet result = new Sheet();
//        NodePropertySetBuilder b = new NodePropertySetBuilder();
//
//        b.withBoolean().selectField(bean, "vertical").display("Vertical alignment").add();
//        b.withBoolean().selectField(bean, "showDates").display("Include date headers").add();
//        b.withBoolean().selectField(bean, "showTitle").display("Include title headers").add();
//        b.withBoolean().selectField(bean, "beginPeriod").display("Begin period").add();
//        result.put(b.build());
//
//        return result;
//    }
//    //</editor-fold>
//}

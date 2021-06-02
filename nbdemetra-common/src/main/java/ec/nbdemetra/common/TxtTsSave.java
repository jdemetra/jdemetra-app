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

import com.google.common.io.Files;
import demetra.timeseries.TsCollection;
import demetra.timeseries.TsInformationType;
import demetra.ui.TsManager;
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.DemetraUiIcon;
import demetra.ui.properties.PropertySheetDialogBuilder;
import ec.nbdemetra.ui.SingleFileExporter;
import demetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.tssave.TsSaveUtil;
import ec.tss.datatransfer.impl.TxtDataTransfer;
import ec.tss.tsproviders.common.txt.TxtFileFilter;
import ec.util.various.swing.OnAnyThread;
import ec.util.various.swing.OnEDT;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;
import demetra.ui.TsActionsSaveSpi;

/**
 *
 * @author Philippe Charles
 * @since 2.2.0
 */
@ServiceProvider(service = TsActionsSaveSpi.class)
public final class TxtTsSave implements TsActionsSaveSpi {

    private final FileChooserBuilder fileChooser;
    private final OptionsBean options;

    public TxtTsSave() {
        this.fileChooser = TsSaveUtil.fileChooser(TxtTsSave.class).setFileFilter(new SaveFileFilter());
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
        return ImageUtilities.icon2Image(DemetraUiIcon.PUZZLE_16);
    }

    @Override
    public void save(List<TsCollection> input) {
        TsSaveUtil.saveToFile(fileChooser, o -> editBean(options), o -> store(input, o, options));
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    @OnEDT
    private static boolean editBean(OptionsBean bean) {
        return new PropertySheetDialogBuilder().title("Options").editSheet(getSheet(bean));
    }

    @OnEDT
    private static void store(List<TsCollection> data, File file, OptionsBean opts) {
        new SingleFileExporter()
                .file(file)
                .progressLabel("Saving to text file")
                .onErrorNotify("Saving to text file failed")
                .onSussessNotify("Text file saved")
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
                .put("beginPeriod", options.beginPeriod)
                .put("showDates", options.showDates)
                .put("showTitle", options.showTitle)
                .put("vertical", options.vertical)
                .build();
        handler.setConfig(config);
        String stringContent = handler.tsCollectionToString(content);

        ph.progress("Writing file");
        Files.write(stringContent, file, StandardCharsets.UTF_8);
    }

    private static final class SaveFileFilter extends FileFilter {

        private final TxtFileFilter delegate = new TxtFileFilter();

        @Override
        public boolean accept(File f) {
            return f.isDirectory() || delegate.accept(f);
        }

        @Override
        public String getDescription() {
            return delegate.getDescription();
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

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
import ec.nbdemetra.ui.Config;
import ec.nbdemetra.ui.DemetraUiIcon;
import ec.nbdemetra.ui.properties.PropertySheetDialogBuilder;
import ec.nbdemetra.ui.SingleFileExporter;
import ec.nbdemetra.ui.properties.IBeanEditor;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.tssave.ITsSave;
import ec.tss.Ts;
import ec.tss.TsCollection;
import ec.tss.TsFactory;
import ec.tss.datatransfer.impl.TxtTssTransferHandler;
import ec.tss.tsproviders.common.txt.TxtFileFilter;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 * @since 2.2.0
 */
@ServiceProvider(service = ITsSave.class)
public final class TxtTsSave implements ITsSave {

    private final FileChooserBuilder fileChooserBuilder;
    private final OptionsEditor optionsEditor;
    private final OptionsBean optionsBean;

    public TxtTsSave() {
        this.fileChooserBuilder = new FileChooserBuilder(TxtTsSave.class)
                .setFileFilter(new SaveFileFilter())
                .setSelectionApprover(SingleFileExporter.overwriteApprover());
        this.optionsEditor = new OptionsEditor();
        this.optionsBean = new OptionsBean();
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
    public void save(Ts[] ts) {
        File target = fileChooserBuilder.showSaveDialog();
        if (target != null && optionsEditor.editBean(optionsBean)) {
            new SingleFileExporter()
                    .file(target)
                    .progressLabel("Saving to text file")
                    .onErrorNotify("Saving to text file failed")
                    .onSussessNotify("Text file saved")
                    .execAsync((f, ph) -> store(ts, f, optionsBean, ph));
        }
    }

    //<editor-fold defaultstate="collapsed" desc="Implementation details">
    private static File store(Ts[] data, File file, OptionsBean options, ProgressHandle ph) throws IOException {
        ph.start();
        ph.progress("Initializing content");
        TsCollection col = Arrays.stream(data).collect(TsFactory.toTsCollection());
        TxtTssTransferHandler handler = new TxtTssTransferHandler();
        Config config = handler.getConfig().toBuilder()
                .put("beginPeriod", options.beginPeriod)
                .put("showDates", options.showDates)
                .put("showTitle", options.showTitle)
                .put("vertical", options.vertical)
                .build();
        handler.setConfig(config);
        ph.progress("Creating content");
        String content = handler.tsCollectionToString(col);
        ph.progress("Writing content");
        Files.write(content, file, StandardCharsets.UTF_8);
        return file;
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

    private static final class OptionsEditor implements IBeanEditor {

        private Sheet getSheet(OptionsBean bean) {
            Sheet result = new Sheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();

            b.withBoolean().selectField(bean, "vertical").display("Vertical alignment").add();
            b.withBoolean().selectField(bean, "showDates").display("Include date headers").add();
            b.withBoolean().selectField(bean, "showTitle").display("Include title headers").add();
            b.withBoolean().selectField(bean, "beginPeriod").display("Begin period").add();
            result.put(b.build());

            return result;
        }

        @Override
        final public boolean editBean(Object bean) {
            OptionsBean config = (OptionsBean) bean;
            return new PropertySheetDialogBuilder().title("Options").editSheet(getSheet(config));
        }
    }
    //</editor-fold>
}

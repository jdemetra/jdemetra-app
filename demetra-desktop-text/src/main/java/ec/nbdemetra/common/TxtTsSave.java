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
import demetra.desktop.ConfigBean;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.util.SingleFileExporter;
import demetra.timeseries.TsCollection;
import demetra.desktop.DemetraIcons;
import demetra.desktop.NamedServiceSupport;
import demetra.desktop.Persistable;
import demetra.desktop.datatransfer.ts.TxtDataTransfer;
import ec.util.various.swing.OnAnyThread;
import ec.util.various.swing.OnEDT;
import internal.demetra.tsp.text.TxtFileFilter;
import nbbrd.io.text.Formatter;
import nbbrd.service.ServiceProvider;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.nodes.Sheet;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import demetra.desktop.TsActionSaveSpi;
import demetra.desktop.TsActionSaveSpiSupport;
import static demetra.desktop.TsActionSaveSpiSupport.newEditor;
import demetra.desktop.util.Persistence;
import static demetra.desktop.util.SingleFileExporter.newFileChooser;
import demetra.tsprovider.util.PropertyHandler;
import nbbrd.design.MightBeGenerated;

/**
 * @author Philippe Charles
 * @since 2.2.0
 */
@ServiceProvider(TsActionSaveSpi.class)
public final class TxtTsSave implements TsActionSaveSpi, Persistable {

    @lombok.experimental.Delegate(types = Persistable.class)
    private final OptionsBean configuration = new OptionsBean();

    @lombok.experimental.Delegate
    private final TsActionSaveSpiSupport support = TsActionSaveSpiSupport
            .builder()
            .name(NamedServiceSupport
                    .builder("TxtTsSave")
                    .displayName("Text file")
                    .icon(DemetraIcons.PUZZLE_16)
                    .build())
            .fileChooser(newFileChooser(TxtTsSave.class).setFileFilter(getFilter()))
            .bean(configuration)
            .editor(newEditor(OptionsBean.class, OptionsBean::asSheet))
            .task(TxtTsSave::getTask)
            .build();

    @OnEDT
    private static SingleFileExporter.SingleFileTask getTask(List<TsCollection> data, Object options) {
        TxtDataTransfer writer = ((OptionsBean) options).toHandler();
        return (file, progress) -> store(file, progress, data, writer);
    }

    @OnAnyThread
    private static void store(File file, ProgressHandle ph, List<TsCollection> data, TxtDataTransfer writer) throws IOException {
        ph.start();
        ph.progress("Loading time series");
        TsCollection content = TsActionSaveSpiSupport.flatLoad(data);

        ph.progress("Creating content");
        String stringContent = writer.tsCollectionToString(content);

        ph.progress("Writing file");
        Files.write(file.toPath(), Collections.singleton(stringContent), StandardCharsets.UTF_8);
    }

    private static FileFilter getFilter() {
        TxtFileFilter delegate = new TxtFileFilter();
        return TsActionSaveSpiSupport.newFileChooserFilter(delegate, delegate.getFileDescription());
    }

    @lombok.Data
    private static final class OptionsBean implements ConfigBean {

        private static final String VERTICAL_PROPERTY = "vertical";
        private static final boolean DEFAULT_VERTICAL = true;
        private boolean vertical = DEFAULT_VERTICAL;

        private static final String SHOW_DATES_PROPERTY = "showDates";
        private static final boolean DEFAULT_SHOW_DATES = true;
        private boolean showDates = DEFAULT_SHOW_DATES;

        private static final String SHOW_TITLE_PROPERTY = "showTitle";
        private static final boolean DEFAULT_SHOW_TITLE = true;
        private boolean showTitle = DEFAULT_SHOW_TITLE;

        private static final String BEGIN_PERIOD_PROPERTY = "beginPeriod";
        private static final boolean DEFAULT_BEGIN_PERIOD = true;
        private boolean beginPeriod = DEFAULT_BEGIN_PERIOD;

        @MightBeGenerated
        @Override
        public Sheet asSheet() {
            Sheet result = new Sheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();

            b.withBoolean().select(VERTICAL_PROPERTY, this::isVertical, this::setVertical).display("Vertical alignment").add();
            b.withBoolean().select(SHOW_DATES_PROPERTY, this::isShowDates, this::setShowDates).display("Include date headers").add();
            b.withBoolean().select(SHOW_TITLE_PROPERTY, this::isShowTitle, this::setShowTitle).display("Include title headers").add();
            b.withBoolean().select(BEGIN_PERIOD_PROPERTY, this::isBeginPeriod, this::setBeginPeriod).display("Begin period").add();
            result.put(b.build());

            return result;
        }

        @Override
        public Config getConfig() {
            return PERSISTENCE.loadConfig(this);
        }

        @Override
        public void setConfig(Config config) throws IllegalArgumentException {
            PERSISTENCE.storeConfig(this, config);
        }

        TxtDataTransfer toHandler() {
            TxtDataTransfer handler = new TxtDataTransfer();
            Config config = handler.getConfig().toBuilder()
                    .parameter("beginPeriod", Formatter.onBoolean().formatAsString(beginPeriod))
                    .parameter("showDates", Formatter.onBoolean().formatAsString(showDates))
                    .parameter("showTitle", Formatter.onBoolean().formatAsString(showTitle))
                    .parameter("vertical", Formatter.onBoolean().formatAsString(vertical))
                    .build();
            handler.setConfig(config);
            return handler;
        }

        @MightBeGenerated
        static final Persistence<OptionsBean> PERSISTENCE = Persistence
                .builderOf(OptionsBean.class)
                .name("INSTANCE")
                .version("VERSION")
                .with(PropertyHandler.onBoolean(VERTICAL_PROPERTY, DEFAULT_VERTICAL), OptionsBean::isVertical, OptionsBean::setVertical)
                .with(PropertyHandler.onBoolean(SHOW_DATES_PROPERTY, DEFAULT_SHOW_DATES), OptionsBean::isShowDates, OptionsBean::setShowDates)
                .with(PropertyHandler.onBoolean(SHOW_TITLE_PROPERTY, DEFAULT_SHOW_TITLE), OptionsBean::isShowTitle, OptionsBean::setShowTitle)
                .with(PropertyHandler.onBoolean(BEGIN_PERIOD_PROPERTY, DEFAULT_BEGIN_PERIOD), OptionsBean::isBeginPeriod, OptionsBean::setBeginPeriod)
                .build();
    }
}

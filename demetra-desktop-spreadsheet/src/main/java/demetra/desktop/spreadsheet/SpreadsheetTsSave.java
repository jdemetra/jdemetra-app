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
package demetra.desktop.spreadsheet;

import demetra.desktop.Config;
import demetra.desktop.ConfigBean;
import demetra.timeseries.TsCollection;
import demetra.desktop.DemetraIcons;
import demetra.desktop.NamedServiceSupport;
import demetra.desktop.Persistable;
import demetra.desktop.TsActionSaveSpi;
import demetra.desktop.TsActionSaveSpiSupport;
import static demetra.desktop.TsActionSaveSpiSupport.newEditor;
import demetra.desktop.util.SingleFileExporter;
import demetra.desktop.properties.NodePropertySetBuilder;
import ec.util.spreadsheet.Book;
import ec.util.spreadsheet.helpers.ArraySheet;
import ec.util.various.swing.OnAnyThread;
import ec.util.various.swing.OnEDT;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.nodes.Sheet;
import static demetra.desktop.tsproviders.TsProviderProperties.addObsFormat;
import demetra.desktop.util.Persistence;
import static demetra.desktop.util.SingleFileExporter.newFileChooser;
import demetra.tsprovider.grid.GridLayout;
import demetra.tsprovider.grid.GridWriter;
import demetra.tsprovider.util.ObsFormat;
import demetra.tsprovider.util.ObsFormatHandler;
import demetra.tsprovider.util.PropertyHandler;
import ec.util.spreadsheet.BookFactoryLoader;
import internal.spreadsheet.grid.SheetGridOutput;
import java.util.Comparator;
import java.util.stream.Collector;
import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.joining;
import java.util.stream.Stream;
import nbbrd.design.MightBeGenerated;
import nbbrd.service.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(TsActionSaveSpi.class)
public final class SpreadsheetTsSave implements TsActionSaveSpi, Persistable {

    @lombok.experimental.Delegate(types = Persistable.class)
    private final Configuration configuration = new Configuration();

    @lombok.experimental.Delegate
    private final TsActionSaveSpiSupport support = TsActionSaveSpiSupport
            .builder()
            .name(NamedServiceSupport
                    .builder("SpreadsheetTsSave")
                    .displayName("Spreadsheet file")
                    .icon(DemetraIcons.PUZZLE_16)
                    .sheet(configuration::asSheet)
                    .build())
            .fileChooser(newFileChooser(SpreadsheetTsSave.class, getFileFilters()))
            .bean(configuration)
            .editor(newEditor(Configuration.class, Configuration::asSheet))
            .task(SpreadsheetTsSave::getTask)
            .build();

    @OnEDT
    private static SingleFileExporter.SingleFileTask getTask(List<TsCollection> data, Object options) {
        GridWriter writer = ((Configuration) options).toGridWriter();
        return (file, progress) -> store(file, progress, data, writer);
    }

    @OnAnyThread
    private static void store(File file, ProgressHandle ph, List<TsCollection> data, GridWriter writer) throws IOException {
        Book.Factory factory = getFactories()
                .filter(o -> o.accept(file))
                .findFirst()
                .orElseThrow(() -> new IOException("Cannot find spreadsheet factory for file '" + file + "'"));

        ph.progress("Loading time series");
        TsCollection content = TsActionSaveSpiSupport.flatLoad(data);

        ph.progress("Creating content");
        SheetGridOutput output = SheetGridOutput.of(factory::isSupportedDataType);
        writer.write(content, output);
        ArraySheet sheet = output.getResult();

        ph.progress("Writing file");
        factory.store(file, sheet.toBook());
    }

    private static Stream<Book.Factory> getFactories() {
        return BookFactoryLoader.get().stream().filter(Book.Factory::canStore);
    }

    private static List<FileFilter> getFileFilters() {
        return Stream.concat(
                Stream.of(getGroupFilter()),
                getFactories()
                        .map(SpreadsheetTsSave::getIndividualFilter)
                        .sorted(Comparator.comparing(FileFilter::getDescription))
        ).toList();
    }

    private static FileFilter getGroupFilter() {
        return TsActionSaveSpiSupport.newFileChooserFilter(
                file -> getFactories().anyMatch(o -> o.accept(file)),
                "Spreadsheet files " + getFactories().collect(TO_EXTENSIONS));
    }

    private static FileFilter getIndividualFilter(Book.Factory factory) {
        return TsActionSaveSpiSupport.newFileChooserFilter(
                factory::accept,
                factory.getName() + " " + Stream.of(factory).collect(TO_EXTENSIONS));
    }

    private static final Collector<Book.Factory, ?, String> TO_EXTENSIONS
            = flatMapping(SpreadsheetTsSave::getUniqueExtensions, joining(", ", "(", ")"));

    private static Stream<String> getUniqueExtensions(Book.Factory factory) {
        return factory
                .getExtensionsByMediaType()
                .values()
                .stream()
                .flatMap(extensions -> extensions.stream().distinct().sorted())
                .map(extension -> "*" + extension);
    }

    @lombok.Data
    private static final class Configuration implements ConfigBean {

        private static final String FORMAT_PROPERTY = "format";
        private static final ObsFormat DEFAULT_FORMAT = GridWriter.DEFAULT.getFormat();
        private ObsFormat format = DEFAULT_FORMAT;

        private static final String LAYOUT_PROPERTY = "layout";
        private static final GridLayout DEFAULT_LAYOUT = GridWriter.DEFAULT.getLayout();
        private GridLayout layout = DEFAULT_LAYOUT;

        private static final String IGNORE_NAMES_PROPERTY = "ignoreNames";
        private static final boolean DEFAULT_IGNORE_NAMES = GridWriter.DEFAULT.isIgnoreNames();
        private boolean ignoreNames = DEFAULT_IGNORE_NAMES;

        private static final String IGNORE_DATES_PROPERTY = "ignoreDates";
        private static final boolean DEFAULT_IGNORE_DATES = GridWriter.DEFAULT.isIgnoreDates();
        private boolean ignoreDates = DEFAULT_IGNORE_DATES;

        private static final String CORNER_LABEL_PROPERTY = "cornerLabel";
        private static final String DEFAULT_CORNER_LABEL = GridWriter.DEFAULT.getCornerLabel();
        private String cornerLabel = DEFAULT_CORNER_LABEL;

        private static final String REVERSE_CHRONOLOGY_PROPERTY = "reverseChronology";
        private static final boolean DEFAULT_REVERSE_CHRONOLOGY = GridWriter.DEFAULT.isReverseChronology();
        private boolean reverseChronology = DEFAULT_REVERSE_CHRONOLOGY;

        @MightBeGenerated
        @Override
        public Sheet asSheet() {
            Sheet result = new Sheet();
            NodePropertySetBuilder b = new NodePropertySetBuilder();

            addObsFormat(b, this::getFormat, this::setFormat);
            b.withEnum(GridLayout.class).select(LAYOUT_PROPERTY, this::getLayout, this::setLayout).display("Layout").add();
            b.withBoolean().select(IGNORE_NAMES_PROPERTY, this::isIgnoreNames, this::setIgnoreNames).display("Ignore names").add();
            b.withBoolean().select(IGNORE_DATES_PROPERTY, this::isIgnoreDates, this::setIgnoreDates).display("Ignore dates").add();
            b.with(String.class).select(CORNER_LABEL_PROPERTY, this::getCornerLabel, this::setCornerLabel).display("Corner label").add();
            b.withBoolean().select(REVERSE_CHRONOLOGY_PROPERTY, this::isReverseChronology, this::setReverseChronology).display("Reverse chronology").add();
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

        GridWriter toGridWriter() {
            return GridWriter
                    .builder()
                    .format(format)
                    .layout(layout)
                    .ignoreNames(ignoreNames)
                    .ignoreDates(ignoreDates)
                    .cornerLabel(cornerLabel)
                    .reverseChronology(reverseChronology)
                    .build();
        }

        @MightBeGenerated
        static final Persistence<Configuration> PERSISTENCE = Persistence
                .builderOf(Configuration.class)
                .name("INSTANCE")
                .version("VERSION")
                .with(
                        ObsFormatHandler
                                .builder()
                                .locale(PropertyHandler.onLocale("locale", DEFAULT_FORMAT.getLocale()))
                                .dateTimePattern(PropertyHandler.onString("dateTimePattern", DEFAULT_FORMAT.getDateTimePattern()))
                                .numberPattern(PropertyHandler.onString("numberPattern", DEFAULT_FORMAT.getNumberPattern()))
                                .ignoreNumberGrouping(PropertyHandler.onBoolean("ignoreNumberGrouping", DEFAULT_FORMAT.isIgnoreNumberGrouping()))
                                .build(),
                        Configuration::getFormat, Configuration::setFormat
                )
                .with(PropertyHandler.onEnum(LAYOUT_PROPERTY, DEFAULT_LAYOUT), Configuration::getLayout, Configuration::setLayout)
                .with(PropertyHandler.onBoolean(IGNORE_NAMES_PROPERTY, DEFAULT_IGNORE_NAMES), Configuration::isIgnoreNames, Configuration::setIgnoreNames)
                .with(PropertyHandler.onBoolean(IGNORE_DATES_PROPERTY, DEFAULT_IGNORE_DATES), Configuration::isIgnoreDates, Configuration::setIgnoreDates)
                .with(PropertyHandler.onString(CORNER_LABEL_PROPERTY, DEFAULT_CORNER_LABEL), Configuration::getCornerLabel, Configuration::setCornerLabel)
                .with(PropertyHandler.onBoolean(REVERSE_CHRONOLOGY_PROPERTY, DEFAULT_REVERSE_CHRONOLOGY), Configuration::isReverseChronology, Configuration::setReverseChronology)
                .build();
    }
}

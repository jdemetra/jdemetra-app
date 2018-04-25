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
package ec.nbdemetra.spreadsheet;

import ec.nbdemetra.ui.properties.FileLoaderFileFilter;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.tsproviders.AbstractDataSourceProviderBuddy;
import ec.nbdemetra.ui.tsproviders.IDataSourceProviderBuddy;
import ec.tss.tsproviders.DataSet;
import ec.tss.tsproviders.DataSource;
import ec.tss.tsproviders.IFileLoader;
import ec.tss.tsproviders.TsProviders;
import ec.tss.tsproviders.spreadsheet.SpreadSheetBean;
import ec.tss.tsproviders.spreadsheet.SpreadSheetProvider;
import ec.tss.tsproviders.spreadsheet.engine.SpreadSheetCollection;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.Sheet.Set;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = IDataSourceProviderBuddy.class)
public class SpreadsheetProviderBuddy extends AbstractDataSourceProviderBuddy {

    @Override
    public String getProviderName() {
        return SpreadSheetProvider.SOURCE;
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/document-table.png", true);
    }

    @Override
    public Image getIcon(DataSource dataSource, int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/tables.png", true);
    }

    @Override
    public Image getIcon(DataSet dataSet, int type, boolean opened) {
        switch (dataSet.getKind()) {
            case COLLECTION:
                return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/table-sheet.png", true);
            case SERIES:
                switch (getAlignType(dataSet)) {
                    case VERTICAL:
                        return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/table-select-column.png", true);
                    case HORIZONTAL:
                        return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/table-select-row.png", true);
                }
                break;
        }
        return null;
    }

    @Messages({
        "dataset.sheetName.display=Sheet name",
        "dataset.seriesName.display=Series name"
    })
    @Override
    protected void fillParamProperties(NodePropertySetBuilder b, DataSet dataSet) {
        b.with(String.class)
                .selectConst("sheetName", SpreadSheetProvider.Y_SHEETNAME.get(dataSet))
                .display(Bundle.dataset_sheetName_display())
                .add();
        if (dataSet.getKind().equals(DataSet.Kind.SERIES)) {
            b.with(String.class)
                    .selectConst("seriesName", SpreadSheetProvider.Z_SERIESNAME.get(dataSet))
                    .display(Bundle.dataset_seriesName_display())
                    .add();
        }
    }

    @Override
    protected List<Set> createSheetSets(Object bean) throws IntrospectionException {
        return bean instanceof SpreadSheetBean
                ? createSheetSets((SpreadSheetBean) bean)
                : super.createSheetSets(bean);
    }

    @Messages({
        "bean.source.display=Source",
        "bean.file.display=Spreadsheet file",
        "bean.file.description=The path to the spreadsheet file.",
        "bean.options.display=Options",
        "bean.dataFormat.display=Data format",
        "bean.dataFormat.description=The format used to read dates and values.",
        "bean.frequency.display=Frequency",
        "bean.frequency.description=.",
        "bean.aggregationType.display=Aggregation type",
        "bean.aggregationType.description=.",
        "bean.cleanMissing.display=Clean missing",
        "bean.cleanMissing.description=Erases the Missing values of the series."
    })
    private List<Set> createSheetSets(SpreadSheetBean bean) {
        List<Set> result = new ArrayList<>();

        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("source").display(Bundle.bean_source_display());
        TsProviders.lookup(IFileLoader.class, SpreadSheetProvider.SOURCE)
                .toJavaUtil()
                .ifPresent(o -> addFileProperty(b, bean, o));
        result.add(b.build());

        b.reset("options").display(Bundle.bean_options_display());
        addObsFormatProperty(b, bean);
        addObsGatheringProperty(b, bean);
        result.add(b.build());

        return result;
    }

    private static SpreadSheetCollection.AlignType getAlignType(DataSet dataSet) {
        return TsProviders.lookup(SpreadSheetProvider.class, SpreadSheetProvider.SOURCE)
                .toJavaUtil()
                .map(o -> {
                    try {
                        return o.getSeries(dataSet).alignType;
                    } catch (IOException ex) {
                        // TODO: log this?
                        return SpreadSheetCollection.AlignType.UNKNOWN;
                    }
                }).orElse(SpreadSheetCollection.AlignType.UNKNOWN);
    }

    private static void addFileProperty(NodePropertySetBuilder b, SpreadSheetBean bean, IFileLoader loader) {
        b.withFile()
                .select(bean, "file")
                .display(Bundle.bean_file_display())
                .description(Bundle.bean_file_description())
                .filterForSwing(new FileLoaderFileFilter(loader))
                .paths(loader.getPaths())
                .directories(false)
                .add();
    }

    private static void addObsFormatProperty(NodePropertySetBuilder b, SpreadSheetBean bean) {
        b.with(DataFormat.class)
                .select(bean, "dataFormat")
                .display(Bundle.bean_dataFormat_display())
                .description(Bundle.bean_dataFormat_description())
                .add();
    }

    private static void addObsGatheringProperty(NodePropertySetBuilder b, SpreadSheetBean bean) {
        b.withEnum(TsFrequency.class)
                .select(bean, "frequency")
                .display(Bundle.bean_frequency_display())
                .description(Bundle.bean_frequency_description())
                .add();
        b.withEnum(TsAggregationType.class)
                .select(bean, "aggregationType")
                .display(Bundle.bean_aggregationType_display())
                .description(Bundle.bean_aggregationType_description())
                .add();
        b.withBoolean()
                .select(bean, "cleanMissing")
                .display(Bundle.bean_cleanMissing_display())
                .description(Bundle.bean_cleanMissing_description())
                .add();
    }
}

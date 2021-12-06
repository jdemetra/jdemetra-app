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
package demetra.desktop.spreadsheet;

import demetra.data.AggregationType;
import demetra.desktop.TsManager;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.tsproviders.AbstractDataSourceProviderBuddy;
import demetra.desktop.tsproviders.DataSourceProviderBuddy;
import demetra.desktop.ui.properties.FileLoaderFileFilter;
import demetra.spreadsheet.SpreadSheetBean;
import demetra.tsprovider.FileLoader;
import demetra.timeseries.TsUnit;
import demetra.timeseries.calendars.RegularFrequency;
import demetra.timeseries.util.ObsGathering;
import demetra.tsprovider.util.ObsFormat;
import nbbrd.service.ServiceProvider;
import org.openide.nodes.Sheet.Set;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;

import java.awt.*;
import java.beans.IntrospectionException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Philippe Charles
 */
@ServiceProvider(DataSourceProviderBuddy.class)
public final class SpreadsheetProviderBuddy extends AbstractDataSourceProviderBuddy {

    private static final String SOURCE = "XCLPRVDR";

    @Override
    public String getProviderName() {
        return SOURCE;
    }

    @Override
    public Image getIconOrNull(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/document-table.png", true);
    }

    @Override
    public Image getIconOrNull(demetra.tsprovider.DataSource dataSource, int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/tables.png", true);
    }

    @Override
    public Image getIconOrNull(demetra.tsprovider.DataSet dataSet, int type, boolean opened) {
        switch (dataSet.getKind()) {
            case COLLECTION:
                return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/table-sheet.png", true);
            case SERIES:
//                switch (getAlignType(dataSet)) {
//                    case VERTICAL:
//                        return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/table-select-column.png", true);
//                    case HORIZONTAL:
                return ImageUtilities.loadImage("ec/nbdemetra/spreadsheet/table-select-row.png", true);
//                }
//                break;
        }
        return null;
    }

//    @Override
//    public boolean editBean(String title, Object bean) throws IntrospectionException {
//        return super.editBean(title, bean instanceof ToFileBean ? ((ToFileBean) bean).getDelegate() : bean);
//    }
    @Messages({
        "dataset.sheetName.display=Sheet name",
        "dataset.seriesName.display=Series name"
    })
    @Override
    protected void fillParamProperties(NodePropertySetBuilder b, demetra.tsprovider.DataSet dataSet) {
//        b.with(String.class)
//                .selectConst("sheetName", SpreadSheetProvider.Y_SHEETNAME.get(dataSet))
//                .display(Bundle.dataset_sheetName_display())
//                .add();
//        if (dataSet.getKind().equals(DataSet.Kind.SERIES)) {
//            b.with(String.class)
//                    .selectConst("seriesName", SpreadSheetProvider.Z_SERIESNAME.get(dataSet))
//                    .display(Bundle.dataset_seriesName_display())
//                    .add();
//        }
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
        "bean.cleanMissing.description=Erases the Missing values of the series.",
        "bean.allowPartial.display=Partial aggregation",
        "bean.allowPartial.description=Allow partial aggregation (only with average and sum aggregation)."
    })
    private List<Set> createSheetSets(SpreadSheetBean bean) {
        List<Set> result = new ArrayList<>();

        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("source")
                .display(Bundle.bean_source_display());
        TsManager.getDefault()
                .getProvider(FileLoader.class, SOURCE)
                .ifPresent(o -> addFileProperty(b, bean, o));
        result.add(b.build());

        b.reset("options").display(Bundle.bean_options_display());
        addObsFormatProperty(b, bean);
        addObsGatheringProperty(b, bean);
        result.add(b.build());

        return result;
    }

    private static void addFileProperty(NodePropertySetBuilder b, SpreadSheetBean bean, FileLoader loader) {
        b.withFile()
                .select(bean, "file")
                .name("file")
                .display(Bundle.bean_file_display())
                .description(Bundle.bean_file_description())
                .filterForSwing(new FileLoaderFileFilter(loader))
                .paths(loader.getPaths())
                .directories(false)
                .add();
    }

    private static void addObsFormatProperty(NodePropertySetBuilder b, SpreadSheetBean bean) {
        b.with(ObsFormat.class)
                .select(bean, "obsFormat")
                .name("format")
                .display(Bundle.bean_dataFormat_display())
                .description(Bundle.bean_dataFormat_description())
                .add();
    }

    private static void addObsGatheringProperty(NodePropertySetBuilder b, SpreadSheetBean bean) {
        b.withEnum(RegularFrequency.class)
                .select(bean, "obsGathering", ObsGathering.class,
                        (ObsGathering og) -> RegularFrequency.valueOf(og.getUnit().getAnnualFrequency()),
                        af -> bean.getObsGathering().toBuilder().unit(af.intValue()==0? TsUnit.UNDEFINED : TsUnit.ofAnnualFrequency(af.intValue())).build())
                .name("frequency")
                .display(Bundle.bean_frequency_display())
                .description(Bundle.bean_frequency_description())
                .add()
                
                .withEnum(AggregationType.class)
                .select(bean, "obsGathering", ObsGathering.class,
                        (ObsGathering og) -> og.getAggregationType(),
                        at -> bean.getObsGathering().toBuilder().aggregationType(at).build())
                .name("aggregation")
                .display(Bundle.bean_aggregationType_display())
                .description(Bundle.bean_aggregationType_description())
                .add()
                
                .withBoolean()
                .select(bean, "obsGathering", ObsGathering.class,
                        (ObsGathering og) -> !og.isIncludeMissingValues(),
                        mv -> bean.getObsGathering().toBuilder().includeMissingValues(!mv).build())
                .name("missing")
                .display(Bundle.bean_cleanMissing_display())
                .description(Bundle.bean_cleanMissing_description())
                .add()
                
//                .withBoolean()
//                .select(bean, "obsGathering", ObsGathering.class,
//                        (ObsGathering og) -> og.isAllowPartialAggregation(),
//                        pa -> bean.getObsGathering().toBuilder().allowPartialAggregation(pa).build())
//                .name("partial")
//                .display(Bundle.bean_allowPartial_display())
//                .description(Bundle.bean_allowPartial_description())
//                .add()
                ;

    }
}

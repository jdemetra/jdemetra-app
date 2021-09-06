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
package ec.nbdemetra.common;

import demetra.bridge.ToFileBean;
import demetra.bridge.TsConverter;
import demetra.desktop.TsManager;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.tsproviders.AbstractDataSourceProviderBuddy;
import demetra.desktop.tsproviders.DataSourceProviderBuddy;
import ec.tss.tsproviders.IFileBean;
import ec.tss.tsproviders.IFileLoader;
import ec.tss.tsproviders.common.txt.TxtBean;
import ec.tss.tsproviders.common.txt.TxtBean.Delimiter;
import ec.tss.tsproviders.common.txt.TxtBean.TextQualifier;
import ec.tss.tsproviders.common.txt.TxtProvider;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import nbbrd.service.ServiceProvider;
import org.openide.nodes.Sheet.Set;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(DataSourceProviderBuddy.class)
public final class TxtProviderBuddy extends AbstractDataSourceProviderBuddy {

    @Override
    public String getProviderName() {
        return TxtProvider.SOURCE;
    }

    @Override
    public Image getIconOrNull(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/common/document-list.png", true);
    }

    @Override
    public boolean editBean(String title, Object bean) throws IntrospectionException {
        return super.editBean(title, bean instanceof ToFileBean ? ((ToFileBean) bean).getDelegate() : bean);
    }

    @Override
    protected List<Set> createSheetSets(Object bean) throws IntrospectionException {
        return bean instanceof TxtBean
                ? createSheetSets((TxtBean) bean)
                : super.createSheetSets(bean);
    }

    private List<Set> createSheetSets(TxtBean bean) {
        List<Set> result = new ArrayList<>();

        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("Source");
        TsManager.getDefault()
                .getProvider(TxtProvider.SOURCE)
                .map(TsConverter::fromTsProvider)
                .filter(TxtProvider.class::isInstance)
                .map(TxtProvider.class::cast)
                .ifPresent(o -> addFileProperty(b, bean, o));
        addReaderProperty(b, bean);
        addCsvDialectProperty(b, bean);
        result.add(b.build());

        b.reset("Content");
        addObsFormatProperty(b, bean);
        addObsGatheringProperty(b, bean);
        result.add(b.build());

        return result;
    }

    private static void addFileProperty(NodePropertySetBuilder b, IFileBean bean, IFileLoader loader) {
        b.withFile()
                .select(bean, "file")
                .display("Text file")
                .description("The path to the text file.")
                //  .filterForSwing(new FileLoaderFileFilter(loader))
                .paths(loader.getPaths())
                .directories(false)
                .add();
    }

    private static void addReaderProperty(NodePropertySetBuilder b, TxtBean bean) {
        b.with(Charset.class)
                .select(bean, "charset")
                .display("Charset")
                .description("The charset used to read the file.")
                .add();
        b.withInt()
                .select(bean, "skipLines")
                .min(0)
                .display("Lines to skip")
                .description("The number of lines to skip before reading the data.")
                .add();
    }

    private static void addCsvDialectProperty(NodePropertySetBuilder b, TxtBean bean) {
        b.withEnum(Delimiter.class)
                .select(bean, "delimiter")
                .display("Delimiter")
                .description("The character used to separate fields.")
                .add();
        b.withEnum(TextQualifier.class)
                .select(bean, "textQualifier")
                .display("Text qualifier")
                .description("The characters used to retreive text fields.")
                .add();
        b.withBoolean()
                .select(bean, "headers")
                .display("Has headers?")
                .description("Use first line as headers.")
                .add();
    }

    private static void addObsFormatProperty(NodePropertySetBuilder b, TxtBean bean) {
        b.with(DataFormat.class)
                .select(bean, "dataFormat")
                .display("Data format")
                .description("The format used to read dates and values.")
                .add();
    }

    private static void addObsGatheringProperty(NodePropertySetBuilder b, TxtBean bean) {
        b.withEnum(TsFrequency.class)
                .select(bean, "frequency")
                .display("Frequency")
                .add();
        b.withEnum(TsAggregationType.class)
                .select(bean, "aggregationType")
                .display("Aggregation type")
                .add();
        b.withBoolean()
                .select(bean, "cleanMissing")
                .display("Clean missing")
                .description("Erases the Missing values of the series.")
                .add();
    }
}

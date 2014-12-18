/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ec.nbdemetra.common;

import ec.nbdemetra.ui.properties.FileLoaderFileFilter;
import ec.nbdemetra.ui.properties.NodePropertySetBuilder;
import ec.nbdemetra.ui.tsproviders.AbstractDataSourceProviderBuddy;
import ec.nbdemetra.ui.tsproviders.IDataSourceProviderBuddy;
import ec.tss.tsproviders.IFileLoader;
import ec.tss.tsproviders.TsProviders;
import ec.tss.tsproviders.common.txt.TxtBean.Delimiter;
import ec.tss.tsproviders.common.txt.TxtBean.TextQualifier;
import ec.tss.tsproviders.common.txt.TxtProvider;
import ec.tss.tsproviders.utils.DataFormat;
import ec.tstoolkit.timeseries.TsAggregationType;
import ec.tstoolkit.timeseries.simplets.TsFrequency;
import java.awt.Image;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.openide.nodes.Sheet.Set;
import org.openide.util.ImageUtilities;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Philippe Charles
 */
@ServiceProvider(service = IDataSourceProviderBuddy.class)
public class TxtProviderBuddy extends AbstractDataSourceProviderBuddy {

    @Override
    public String getProviderName() {
        return TxtProvider.SOURCE;
    }

    @Override
    public Image getIcon(int type, boolean opened) {
        return ImageUtilities.loadImage("ec/nbdemetra/common/document-list.png", true);
    }

    @Override
    protected List<Set> createSheetSets(Object bean) {
        List<Set> result = new ArrayList<>();

        IFileLoader loader = TsProviders.lookup(TxtProvider.class, TxtProvider.SOURCE).get();

        NodePropertySetBuilder b = new NodePropertySetBuilder();

        b.reset("Source");
        b.withFile()
                .select(bean, "file")
                .display("Text file")
                .description("The path to the text file.")
                .filterForSwing(new FileLoaderFileFilter(loader))
                .paths(loader.getPaths())
                .directories(false)
                .add();
        b.with(Charset.class).select(bean, "charset").display("Charset").description("The charset used to read the file.").add();
        b.withEnum(Delimiter.class).select(bean, "delimiter").display("Delimiter").description("The character used to separate fields.").add();
        b.withEnum(TextQualifier.class).select(bean, "textQualifier").display("Text qualifier").description("The characters used to retreive text fields.").add();
        b.withInt().select(bean, "skipLines").min(0).display("Lines to skip").description("The number of lines to skip before reading the data.").add();
        result.add(b.build());

        b.reset("Content");
        b.withBoolean().select(bean, "headers").display("Has headers?").description("Use first line as headers.").add();
        b.with(DataFormat.class).select(bean, "dataFormat").display("Data format").description("The format used to read dates and values.").add();
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
        result.add(b.build());

        return result;
    }
}

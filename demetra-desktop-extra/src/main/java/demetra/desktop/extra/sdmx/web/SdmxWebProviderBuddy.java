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
package demetra.desktop.extra.sdmx.web;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import demetra.desktop.properties.NodePropertySetBuilder;
import demetra.desktop.properties.PropertySheetDialogBuilder;
import demetra.desktop.tsproviders.DataSourceProviderBuddy;
import demetra.desktop.tsproviders.TsProviderProperties;
import demetra.desktop.util.Caches;
import demetra.timeseries.TsMoniker;
import demetra.tsp.extra.sdmx.web.SdmxWebBean;
import demetra.tsp.extra.sdmx.web.SdmxWebProvider;
import demetra.tsprovider.DataSet;
import demetra.tsprovider.DataSource;
import internal.extra.sdmx.SdmxAutoCompletion;
import internal.extra.sdmx.web.SdmxWebFactory;
import java.awt.Image;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;
import nbbrd.service.ServiceProvider;
import nbbrd.design.DirectImpl;
import nbbrd.io.function.IORunnable;
import org.openide.awt.StatusDisplayer;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import sdmxdl.web.SdmxWebManager;
import sdmxdl.web.SdmxWebSource;
import sdmxdl.xml.XmlWebSource;

/**
 * @author Philippe Charles
 */
@DirectImpl
@ServiceProvider(DataSourceProviderBuddy.class)
public final class SdmxWebProviderBuddy implements DataSourceProviderBuddy {

    private static final String SOURCE = "DOTSTAT";

    private final ConcurrentMap autoCompletionCache;

    private File customSources;

    private SdmxWebManager webManager;

    public SdmxWebProviderBuddy() {
        this.autoCompletionCache = Caches.ttlCacheAsMap(Duration.ofMinutes(1));
        this.customSources = new File("");
        this.webManager = SdmxWebFactory.createManager();
        lookupProvider().ifPresent(o -> o.setSdmxManager(webManager));
    }

    @Override
    public String getProviderName() {
        return SOURCE;
    }

    @Override
    public Image getIconOrNull(int type, boolean opened) {
        return SdmxAutoCompletion.getDefaultIcon().getImage();
    }

    @Override
    public Image getIconOrNull(DataSource dataSource, int type, boolean opened) {
        Optional<SdmxWebProvider> lookupProvider = lookupProvider();
        if (lookupProvider.isPresent()) {
            SdmxWebBean bean = lookupProvider.get().decodeBean(dataSource);
            Image result = getIcon(bean);
            if (result != null) {
                return result;
            }
        }
        return DataSourceProviderBuddy.super.getIconOrNull(dataSource, type, opened);
    }

    @Override
    public Image getIconOrNull(DataSet dataSet, int type, boolean opened) {
        switch (dataSet.getKind()) {
            case COLLECTION:
                return ImageUtilities.loadImage("demetra/desktop/icons/folder.png", true);
            case SERIES:
                return ImageUtilities.loadImage("demetra/desktop/icons/chart_line.png", true);
            case DUMMY:
                return null;
        }
        return DataSourceProviderBuddy.super.getIconOrNull(dataSet, type, opened);
    }

    @Override
    public Image getIconOrNull(IOException ex, int type, boolean opened) {
        return ImageUtilities.loadImage("demetra/desktop/icons/exclamation-red.png", true);
    }

    @Override
    public Image getIconOrNull(TsMoniker moniker, int type, boolean opened) {
        // Fix Demetra v2 bug -->
        if (!SOURCE.equals(moniker.getSource())) {
            return DataSourceProviderBuddy.super.getIconOrNull(moniker, type, opened);
        }
        // <--

        Optional<SdmxWebProvider> lookupProvider = lookupProvider();
        if (lookupProvider.isPresent()) {
            SdmxWebProvider provider = lookupProvider.get();
            Optional<DataSet> dataSet = provider.toDataSet(moniker);
            if (dataSet.isPresent()) {
                SdmxWebBean bean = provider.decodeBean(dataSet.get().getDataSource());
                Image result = getIcon(bean);
                if (result != null) {
                    return result;
                }
            }
        }
        return DataSourceProviderBuddy.super.getIconOrNull(moniker, type, opened);
    }

    @Override
    public boolean editBean(String title, Object bean) throws IntrospectionException {
        if (bean instanceof SdmxWebBean) {
            Optional<SdmxWebProvider> provider = lookupProvider();
            if (provider.isPresent()) {
                SdmxWebProvider o = provider.get();
                return new PropertySheetDialogBuilder()
                        .title(title)
                        .icon(getIconOrNull(BeanInfo.ICON_COLOR_16x16, false))
                        .editSheet(createSheet((SdmxWebBean) bean, o.getSdmxManager(), autoCompletionCache));
            }
        }
        return DataSourceProviderBuddy.super.editBean(title, bean);
    }

    private Image getIcon(SdmxWebBean bean) {
        SdmxWebSource source = webManager.getSources().get(bean.getSource());
        return source != null
                ? ImageUtilities.icon2Image(SdmxAutoCompletion.FAVICONS.get(source.getWebsite(), IORunnable.noOp().asUnchecked()))
                : null;
    }

    private static Optional<SdmxWebProvider> lookupProvider() {
        return Optional.ofNullable(Lookup.getDefault().lookup(SdmxWebProvider.class));
    }

    private static List<SdmxWebSource> loadSources(File file) {
        if (file.exists()) {
            try {
                return XmlWebSource.getParser().parseFile(file);
            } catch (IOException ex) {
                StatusDisplayer.getDefault().setStatusText(ex.getMessage());
            }
        }
        return Collections.emptyList();
    }

    @NbBundle.Messages({
        "bean.cache.description=Mechanism used to improve performance."})
    private static Sheet createSheet(SdmxWebBean bean, SdmxWebManager manager, ConcurrentMap cache) {
        Sheet result = new Sheet();
        NodePropertySetBuilder b = new NodePropertySetBuilder();
        result.put(withSource(b.reset("Source"), bean, manager, cache).build());
        result.put(withOptions(b.reset("Options"), bean, manager, cache).build());
        result.put(withCache(b.reset("Cache").description(Bundle.bean_cache_description()), bean).build());
        return result;
    }

    @NbBundle.Messages({
        "bean.source.display=Provider",
        "bean.source.description=The identifier of the service that provides data.",
        "bean.flow.display=Dataflow",
        "bean.flow.description=The identifier of a specific dataflow.",})
    private static NodePropertySetBuilder withSource(NodePropertySetBuilder b, SdmxWebBean bean, SdmxWebManager manager, ConcurrentMap cache) {
        b.withAutoCompletion()
                .select("source", bean::getSource, bean::setSource)
                .servicePath(SdmxWebSource.class.getName())
                .display(Bundle.bean_source_display())
                .description(Bundle.bean_source_description())
                .add();
        b.withAutoCompletion()
                .select("flow", bean::getFlow, bean::setFlow)
                .source(SdmxAutoCompletion.onFlows(manager, bean::getSource, cache))
                .cellRenderer(SdmxAutoCompletion.getFlowsRenderer())
                .display(Bundle.bean_flow_display())
                .description(Bundle.bean_flow_description())
                .add();
        return b;
    }

    @NbBundle.Messages({
        "bean.dimensions.display=Dataflow dimensions",
        "bean.dimensions.description=An optional comma-separated list of dimensions that defines the order used to hierarchise time series.",
        "bean.labelAttribute.display=Series label attribute",
        "bean.labelAttribute.description=An optional attribute that carries the label of time series."
    })
    private static NodePropertySetBuilder withOptions(NodePropertySetBuilder b, SdmxWebBean bean, SdmxWebManager manager, ConcurrentMap cache) {
        b.withAutoCompletion()
                .select(bean, "dimensions", List.class,
                        Joiner.on(',')::join, Splitter.on(',').trimResults().omitEmptyStrings()::splitToList)
                .source(SdmxAutoCompletion.onDimensions(manager, bean::getSource, bean::getFlow, cache))
                .separator(",")
                .defaultValueSupplier(() -> SdmxAutoCompletion.getDefaultDimensionsAsString(manager, bean::getSource, bean::getFlow, cache, ","))
                .cellRenderer(SdmxAutoCompletion.getDimensionsRenderer())
                .display(Bundle.bean_dimensions_display())
                .description(Bundle.bean_dimensions_description())
                .add();
        b.withAutoCompletion()
                .select("labelAttribute", bean::getLabelAttribute, bean::setLabelAttribute)
                .display(Bundle.bean_labelAttribute_display())
                .description(Bundle.bean_labelAttribute_description())
                .add();
        return b;
    }

    private static NodePropertySetBuilder withCache(NodePropertySetBuilder b, SdmxWebBean bean) {
        TsProviderProperties.addBulkCube(b, bean::getCacheConfig, bean::setCacheConfig);
        return b;
    }
}
